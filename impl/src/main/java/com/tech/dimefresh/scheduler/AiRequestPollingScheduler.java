package com.tech.dimefresh.scheduler;


import com.tech.dimefresh.config.properties.S3Properties;
import com.tech.dimefresh.entity.*;
import com.tech.dimefresh.repository.AiRequestRepository;
import com.tech.dimefresh.repository.ChatMessageRepository;
import com.tech.dimefresh.repository.MessageTypeRepository;
import com.tech.dimefresh.repository.S3ObjectRepository;
import com.tech.dimefresh.s3.S3Manager;
import com.tech.dimefresh.s3.S3Model;
import com.tech.dimefresh.scheduler.dto.GenApiResultResponse;
import com.tech.dimefresh.utils.GenApiProperties;
import com.tech.dimefresh.utils.HttpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class AiRequestPollingScheduler {
    private final AiRequestRepository aiRequestRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MessageTypeRepository messageTypeRepository;
    private final S3ObjectRepository s3ObjectRepository;
    private final S3Manager s3Manager;
    private final S3Properties s3Properties;
    private final RestTemplate restTemplate;
    private final GenApiProperties genApiProperties;

    public static final String IMAGE_CONTENT_TYPE = "image/png";

    @Scheduled(timeUnit = TimeUnit.SECONDS, initialDelay = 60, fixedDelay = 5)
    public void updateAiRequestStatus() {
        List<AiRequest> processing = aiRequestRepository.findAllWithStatus(AiRequestStatus.PROCESSING);

        MessageType imageType = messageTypeRepository.findByTitle(MessageTypeEnum.IMAGE.name())
                .orElseThrow(() -> new RuntimeException("Message type IMAGE not found"));

        for (AiRequest aiRequest : processing) {
            try {
                if (aiRequest.getApiServiceRequestId() == null) {
                    log.warn("Skip aiRequest={}, apiServiceRequestId is null", aiRequest.getId());
                    continue;
                }
                HttpHeaders headers = HttpUtils.prepareHeadersForResult(genApiProperties.getToken());
                HttpEntity<Void> entity = new HttpEntity<>(headers);
                String resultUri = genApiProperties.getResultUrl() + "/" + aiRequest.getApiServiceRequestId();

                ResponseEntity<GenApiResultResponse> resultResp = restTemplate.exchange(
                        resultUri,
                        HttpMethod.GET,
                        entity,
                        GenApiResultResponse.class
                );

                GenApiResultResponse body = resultResp.getBody();

                //TODO: потом тоже с этим разобраться
                if(!AiRequestStatus.SUCCESS.getApiRepresentation().equals(body.status()))
                    return;

                String imageUrl = body.result().getFirst();

                ResponseEntity<byte[]> imageResp = restTemplate.exchange(
                        URI.create(imageUrl),
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        byte[].class
                );

                byte[] imageBytes = imageResp.getBody();
                //TODO: пока остается
                if (imageBytes == null || imageBytes.length == 0) {
                    continue;
                }

                String contentType = IMAGE_CONTENT_TYPE;

                UUID objectId = UUID.randomUUID();
                S3Object s3Object = new S3Object();
                s3Object.setId(objectId);
                s3Object.setBucket(s3Properties.getImagesBucket());
                s3Object.setContentType(contentType);
                s3Object.setWeight(imageBytes.length);
                s3ObjectRepository.save(s3Object);

                s3Manager.save(new S3Model(
                        objectId.toString(),
                        contentType,
                        s3Object.getBucket(),
                        imageBytes
                ));

                ChatMessage msg = aiRequest.getChatMsgId();
                msg.setMessageType(imageType);
                msg.setMsgContentText(null);
                msg.setS3Object(s3Object);
                msg.setCreatedAt(Instant.now());
                chatMessageRepository.save(msg);
                aiRequest.setStatus(AiRequestStatus.SUCCESS);
                aiRequestRepository.save(aiRequest);

            } catch (Exception ex) {
                log.error("Failed to update status for aiRequest={}", aiRequest.getId(), ex);
            }
        }

    }
}

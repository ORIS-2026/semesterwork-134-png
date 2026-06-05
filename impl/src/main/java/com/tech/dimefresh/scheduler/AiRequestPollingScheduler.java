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
import org.springframework.transaction.annotation.Transactional;
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

    @Scheduled(timeUnit = TimeUnit.SECONDS, initialDelay = 10, fixedDelay = 15)
    @Transactional
    public void updateAiRequestStatus() {
        List<AiRequest> processing = aiRequestRepository.findAllWithStatus(AiRequestStatus.PROCESSING);
        log.info("Количество заявок которые надо обработать: {}", processing.size());

        MessageType imageType = messageTypeRepository.findByTitle(MessageTypeEnum.IMAGE)
                .orElseThrow(() -> new RuntimeException("Message type IMAGE not found"));

        for (AiRequest aiRequest : processing) {
            log.info("Заявка на обработке: {}", aiRequest.getApiServiceRequestId());
            try {
                if (aiRequest.getApiServiceRequestId() == null) {
                    log.warn("Skip aiRequest={}, apiServiceRequestId is null", aiRequest.getId());
                    continue;
                }
                HttpHeaders headers = HttpUtils.prepareHeadersForResult(genApiProperties.getToken());
                HttpEntity<Void> entity = new HttpEntity<>(headers);
                String resultUri = genApiProperties.getResultUrl() + "/" + aiRequest.getApiServiceRequestId();

                log.info("До получение результата по заявке {}", aiRequest.getApiServiceRequestId());
                ResponseEntity<GenApiResultResponse> resultResp = restTemplate.exchange(
                        resultUri,
                        HttpMethod.GET,
                        entity,
                        GenApiResultResponse.class
                );
                log.info("Запрос выполнили по заявке {}", aiRequest.getApiServiceRequestId());

                GenApiResultResponse body = resultResp.getBody();
                if(body == null){
                    log.info("Тело ответа - null, что-то пошло не так");
                    continue;
                }

                log.info("Результат заявки {}", body.status());

                if(!AiRequestStatus.SUCCESS.getApiRepresentation().equals(body.status())) {
                    log.info("Неудачно");
                    continue;
                }

                String imageUrl = body.result().getFirst();
                log.info("Ссылка на результат генерации: {}", imageUrl);

                ResponseEntity<byte[]> imageResp = restTemplate.exchange(
                        URI.create(imageUrl),
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        byte[].class
                );
                log.info("Извлекли результат генерации");

                byte[] imageBytes = imageResp.getBody();

                if (imageBytes == null || imageBytes.length == 0) {
                    log.info("Картинка не пришла либо количество байт в картинке - 0");
                    continue;
                }

                String contentType = IMAGE_CONTENT_TYPE;

                S3Object s3Object = new S3Object();
                s3Object.setBucket(s3Properties.getImagesBucket());
                s3Object.setContentType(contentType);
                s3Object.setWeight(imageBytes.length);
                s3Object = s3ObjectRepository.save(s3Object);
                log.info("Сохранение s3object в postgresql");

                s3Manager.save(new S3Model(
                        s3Object.getId().toString(),
                        contentType,
                        s3Object.getBucket(),
                        imageBytes
                ));
                log.info("Сохранение s3object в minio");

                ChatMessage msg = aiRequest.getChatMsg();
                msg.setMessageType(imageType);
                msg.setMsgContentText(null);
                msg.setS3Object(s3Object);
                msg.setCreatedAt(Instant.now());
                chatMessageRepository.save(msg);
                aiRequest.setStatus(AiRequestStatus.SUCCESS);
                aiRequestRepository.save(aiRequest);

            } catch (Exception ex) {
                log.info(ex.getMessage());
            }
        }

    }
}

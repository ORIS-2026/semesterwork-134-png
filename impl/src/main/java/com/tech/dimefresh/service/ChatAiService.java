package com.tech.dimefresh.service;

import com.tech.dimefresh.dto.ChatMessageDto;
import com.tech.dimefresh.dto.S3ObjectDto;
import com.tech.dimefresh.entity.*;
import com.tech.dimefresh.repository.AiRequestRepository;
import com.tech.dimefresh.repository.ChatMessageRepository;
import com.tech.dimefresh.repository.ChatRepository;
import com.tech.dimefresh.repository.MessageTypeRepository;
import com.tech.dimefresh.s3.S3Manager;
import com.tech.dimefresh.service.dto.GenApiRequest;
import com.tech.dimefresh.service.dto.GenApiResponse;
import com.tech.dimefresh.utils.GenApiProperties;
import com.tech.dimefresh.utils.HttpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatAiService {

    private final ChatRepository chatRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final AiRequestRepository aiRequestRepository;
    private final MessageTypeRepository messageTypeRepository;

    private final S3Manager s3Manager;

    private final RestTemplate restTemplate;
    private final GenApiProperties genApiProperties;


    public static final String CREATED_AT_ATTR = "createdAt";
    public static final int PAGE_SIZE = 15;

    @Transactional
    public void handlePrompt(Long chatId, String prompt) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found: " + chatId));

        MessageType textType = messageTypeRepository.findByTitle(MessageTypeEnum.TEXT.name())
                .orElseThrow(() -> new RuntimeException("Message type TEXT not found"));

        ChatMessage userMessage = new ChatMessage();
        userMessage.setChat(chat);
        userMessage.setAccount(chat.getOwner());
        userMessage.setMessageType(textType);
        userMessage.setMsgContentText(prompt);
        userMessage.setByBot(false);
        userMessage.setCreatedAt(Instant.now());
        chatMessageRepository.save(userMessage);

        List<AiRequest> activeRequests =
                aiRequestRepository.findByChatMsgId_Chat_IdAndStatus(chatId, AiRequestStatus.PROCESSING);

        if (!activeRequests.isEmpty()) {

            ChatMessage botWarning = new ChatMessage();
            botWarning.setChat(chat);
            botWarning.setMessageType(textType);
            botWarning.setMsgContentText("У вас пока обрабатывается прошлый запрос");
            botWarning.setByBot(true);
            botWarning.setCreatedAt(Instant.now().plusMillis(1));
            chatMessageRepository.save(botWarning);

            log.info("Active AI request exists for chat {}, warning sent", chatId);
        }
        else {
            AiRequest aiRequest = new AiRequest();
            aiRequest.setStatus(AiRequestStatus.PROCESSING);
            aiRequest = aiRequestRepository.save(aiRequest);

            ChatMessage botMessage = new ChatMessage();
            botMessage.setChat(chat);
            botMessage.setAccount(null);
            botMessage.setMessageType(textType);
            botMessage.setMsgContentText("идет генерация...");
            botMessage.setByBot(true);
            botMessage.setCreatedAt(Instant.now().plusMillis(1));
            chatMessageRepository.save(botMessage);

            aiRequest.setChatMsgId(botMessage);


            try {
                HttpHeaders headers = HttpUtils.prepareHeadersForGeneration(genApiProperties.getToken());
                GenApiRequest requestBody = new GenApiRequest(prompt, genApiProperties.getQuality());
                HttpEntity<GenApiRequest> requestEntity = new HttpEntity<>(requestBody, headers);

                GenApiResponse responseBody =
                        restTemplate.postForEntity(genApiProperties.getRequestUrl(), requestEntity, GenApiResponse.class).getBody();
                aiRequest.setApiServiceRequestId(responseBody.request_id());//TODO: null pointer позже обработать

                log.info("AI request sent for chatId={}, prompt='{}'", chatId, prompt);
            } catch (Exception e) {
                log.error("Failed to send AI generation request", e);
            }

            aiRequestRepository.save(aiRequest);
        }
    }


    @Transactional
    public List<ChatMessageDto> getMessages(Long chatId, int page) {
        Sort sort = Sort.by(Sort.Direction.DESC, CREATED_AT_ATTR);
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, sort);

        Page<ChatMessage> messagePage = chatMessageRepository.findAllByChatId(chatId, pageable);
        MessageType textType = messageTypeRepository.findByTitle(MessageTypeEnum.TEXT.name())
                .orElseThrow(() -> new RuntimeException("Message type TEXT not found"));


        return messagePage.stream()
                .map(chatMessage -> {
                    UUID msgId = chatMessage.getId();
                    Boolean isTextMsg = chatMessage.getMessageType().getId() == textType.getId();
                    Boolean byBot = chatMessage.isByBot();

                    String textMsgContent = isTextMsg ? chatMessage.getMsgContentText() : null;
                    S3ObjectDto s3ObjectDto = null;

                    if(!isTextMsg) {
                        S3Object s3Object = chatMessage.getS3Object();
                        URL url = s3Manager.get(s3Object.getId().toString(), s3Object.getBucket());

                        s3ObjectDto = new S3ObjectDto(
                                s3Object.getId().toString(),
                                s3Object.getContentType(),
                                url.toString()
                        );
                    }

                    return new ChatMessageDto(
                            msgId,
                            isTextMsg,
                            byBot,
                            textMsgContent,
                            s3ObjectDto
                    );
                })
                .toList();
    }
}

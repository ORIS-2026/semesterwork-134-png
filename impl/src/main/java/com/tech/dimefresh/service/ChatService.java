package com.tech.dimefresh.service;

import com.tech.dimefresh.config.properties.S3Properties;
import com.tech.dimefresh.dto.AccountRegisterDto;
import com.tech.dimefresh.dto.ChatMessageDto;
import com.tech.dimefresh.dto.S3ObjectDto;
import com.tech.dimefresh.entity.*;
import com.tech.dimefresh.exception.rest.badreq.BadRequestExceptionRest;
import com.tech.dimefresh.exception.rest.internal.InternalServerTroubleExceptionRest;
import com.tech.dimefresh.repository.*;
import com.tech.dimefresh.s3.S3Manager;
import com.tech.dimefresh.s3.S3Model;
import com.tech.dimefresh.scheduler.dto.GenApiResultResponse;
import com.tech.dimefresh.security.util.AuthenticationFacade;
import com.tech.dimefresh.service.dto.CreateNewsDto;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final NewsService newsService;

    private final ChatRepository chatRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final AiRequestRepository aiRequestRepository;
    private final MessageTypeRepository messageTypeRepository;
    private final AccountRepository accountRepository;
    private final S3ObjectRepository s3ObjectRepository;

    private final S3Manager s3Manager;
    private final S3Properties s3Properties;

    private final RestTemplate restTemplate;
    private final GenApiProperties genApiProperties;

    public static final String IMAGE_CONTENT_TYPE = "image/png";

    public static final String CREATED_AT_ATTR = "createdAt";
    public static final int PAGE_SIZE = 7;

    private final AuthenticationFacade authenticationFacade;

    @Transactional
    public void handlePrompt(Long chatId, String prompt) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found: " + chatId));
        if(!authenticationFacade.getAuthenticatedUserId()
                .equals(chat.getOwner().getId()))
            throw new RuntimeException("Нет прав на создание запроса для генерации в этом чате");//TODO: forbidden

        if(prompt.length() > 500)
            throw new BadRequestExceptionRest("Слишком длинный запрос");//TODO: 400

        MessageType textType = messageTypeRepository.findByTitle(MessageTypeEnum.TEXT)
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
                aiRequestRepository.findByChatIdAndStatus(chatId, AiRequestStatus.PROCESSING);

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
            ChatMessage botMessage = new ChatMessage();
            botMessage.setChat(chat);
            botMessage.setAccount(null);
            botMessage.setMessageType(textType);
            botMessage.setMsgContentText("идет генерация...");
            botMessage.setByBot(true);
            botMessage.setCreatedAt(Instant.now().plusMillis(1));
            botMessage = chatMessageRepository.save(botMessage);

            AiRequest aiRequest = new AiRequest();
            aiRequest.setStatus(AiRequestStatus.PROCESSING);
            aiRequest.setChatMsg(botMessage);
            aiRequest = aiRequestRepository.save(aiRequest);

            try {
                HttpHeaders headers = HttpUtils.prepareHeadersForGeneration(genApiProperties.getToken());
                GenApiRequest requestBody = new GenApiRequest(prompt, genApiProperties.getQuality());
                HttpEntity<GenApiRequest> requestEntity = new HttpEntity<>(requestBody, headers);

                GenApiResponse responseBody =
                        restTemplate.postForEntity(genApiProperties.getRequestUrl(), requestEntity, GenApiResponse.class).getBody();

                aiRequest.setApiServiceRequestId(responseBody.request_id());

                log.info("AI request sent for chatId={}, prompt='{}'", chatId, prompt);
            } catch (Exception e) {
                log.error("Failed to send AI generation request", e);
                botMessage.setMsgContentText("Не получилось запустить генерацию");
            }

            aiRequestRepository.save(aiRequest);

            //создание поста
            newsService.createNews(new CreateNewsDto(
                    botMessage.getId(),
                    userMessage.getId()
            ));
        }
    }


    @Transactional
    public List<ChatMessageDto> getMessages(Long chatId, int page) {
        Sort sort = Sort.by(Sort.Direction.DESC, CREATED_AT_ATTR);
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, sort);

        Page<ChatMessage> messagePage = chatMessageRepository.findAllByChatId(chatId, pageable);
        log.info("Получены сообщения по чату: {}", chatId);
        MessageType textType = messageTypeRepository.findByTitle(MessageTypeEnum.TEXT)
                .orElseThrow(() -> {
                    log.info("Message type TEXT not found");
                    return new InternalServerTroubleExceptionRest();
                });


        return messagePage.stream()
                .map(chatMessage -> {
                    UUID msgId = chatMessage.getId();
                    Boolean isTextMsg = chatMessage.getMessageType().getId() == textType.getId();
                    Boolean byBot = chatMessage.isByBot();

                    String textMsgContent = isTextMsg ? chatMessage.getMsgContentText() : null;
                    S3ObjectDto s3ObjectDto = null;

                    if(!isTextMsg) {
                        S3Object s3Object = chatMessage.getS3Object();
                        String url = s3Manager.get(s3Object.getId().toString(), s3Object.getBucket());

                        s3ObjectDto = new S3ObjectDto(
                                s3Object.getId().toString(),
                                s3Object.getContentType(),
                                url
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

    @Transactional
    public Long getChatId() {
        Long authenticatedUserId = authenticationFacade.getAuthenticatedUserId();

        Chat chat = chatRepository.findByOwnerId(authenticatedUserId)
                .orElse(null);

        if(chat == null) {
            chat = new Chat();

            Account authenticatedAccount = accountRepository.findById(authenticatedUserId)
                    .orElseThrow(() -> {
                        log.info("Не найден пользователь с id: {}", authenticatedUserId);
                        return new InternalServerTroubleExceptionRest();
                    });
            chat.setOwner(authenticatedAccount);
            chat = chatRepository.save(chat);
        }

        return chat.getId();
    }

    @Transactional
    public void updateAiRequestsStatus() {
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


                ChatMessage msg = aiRequest.getChatMsg();

                if(!AiRequestStatus.SUCCESS.getApiRepresentation().equals(body.status())) {
                    log.info("Неудачно");
                    newsService.cancelNewsByChatMessageId(msg.getId());
                }
                else {
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

                    msg.setMessageType(imageType);
                    msg.setMsgContentText(null);
                    msg.setS3Object(s3Object);
                    msg.setCreatedAt(Instant.now());
                    chatMessageRepository.save(msg);
                    aiRequest.setStatus(AiRequestStatus.SUCCESS);
                    aiRequestRepository.save(aiRequest);

                    newsService.publishNewsByChatMessageId(msg.getId());
                }


            } catch (Exception ex) {
                log.info(ex.getMessage());
            }
        }
    }
}

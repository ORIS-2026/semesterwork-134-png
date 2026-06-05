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
import com.tech.dimefresh.service.ChatService;
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
    private final ChatService chatService;

    @Scheduled(timeUnit = TimeUnit.SECONDS, initialDelay = 10, fixedDelay = 15)
    @Transactional
    public void updateAiRequestStatus() {
        chatService.updateAiRequestsStatus();
    }
}

package com.tech.dimefresh.scheduler;


import com.tech.dimefresh.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

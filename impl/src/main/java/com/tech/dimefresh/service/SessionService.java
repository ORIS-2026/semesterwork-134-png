package com.tech.dimefresh.service;


import com.tech.dimefresh.dto.AccountInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final RedisTemplate<String, Long> redisTemplate;
    private final AccountService accountService;

    private static final Duration SESSION_TTL_HOURS = Duration.ofHours(24);
    private static final String SESSION_KEY_PREFIX = "session:";

    public String createSession(Long accountId) {
        String sessionId = UUID.randomUUID().toString();
        String redisKey = SESSION_KEY_PREFIX + sessionId;
        redisTemplate.opsForValue()
                .set(
                        redisKey,
                        accountId,
                        SESSION_TTL_HOURS
                );
        return sessionId;
    }

    public void deleteSession(String sessionId) {
        String redisKey = SESSION_KEY_PREFIX + sessionId;
        redisTemplate.delete(redisKey);
    }

    public Optional<Long> getAccountIdBySession(String sessionId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(SESSION_KEY_PREFIX + sessionId));
    }

    public AccountInfoDto getAccountInfo(String sessionId) {
        Long accountId = redisTemplate.opsForValue()
                .get(SESSION_KEY_PREFIX + sessionId);

        return accountService.getAccountInfo(accountId);
    }
}

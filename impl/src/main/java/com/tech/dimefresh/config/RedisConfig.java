package com.tech.dimefresh.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.UUID;


@Configuration
@RequiredArgsConstructor
public class RedisConfig {


    @Bean
    public RedisTemplate<String, Long> redisTemplate(
            RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, Long> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);

        GenericToStringSerializer<Long> longSerializer = new GenericToStringSerializer<>(Long.class);
        template.setValueSerializer(longSerializer);


        template.afterPropertiesSet();

        return template;
    }
}

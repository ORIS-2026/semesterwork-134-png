package com.tech.dimefresh.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;


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

    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory redisConnectionFactory
    ){
        RedisCacheConfiguration config = RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .serializeKeysWith(
                        RedisSerializationContext
                                .SerializationPair
                                .fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext
                                .SerializationPair
                                .fromSerializer(new GenericJackson2JsonRedisSerializer())
                );
        Map<String, RedisCacheConfiguration> perCacheConfigs = Map.of(
                "accounts_avatars", config.entryTtl(Duration.ofMinutes(9))
        );

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(config)
                .withInitialCacheConfigurations(perCacheConfigs)
                .build();
    }
}

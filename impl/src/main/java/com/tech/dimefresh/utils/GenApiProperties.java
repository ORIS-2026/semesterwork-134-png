package com.tech.dimefresh.utils;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration
@Getter
public class GenApiProperties {

    private final String requestUrl;
    private final String resultUrl;
    private final String quality;
    private final String token;


    public GenApiProperties(
            @Value("${gen-api.generate.url}") String requestUrl,
            @Value("${gen-api.result.url}") String resultUrl,
            @Value("${gen-api.quality}") String quality,
            @Value("${gen-api.security.token}") String token) {
        this.requestUrl = requestUrl;
        this.resultUrl = resultUrl;
        this.quality = quality;
        this.token = token;
    }
}

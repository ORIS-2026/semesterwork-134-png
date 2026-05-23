package com.tech.dimefresh.security.properties;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class SecurityProperties {

    private final String clientId;
    private final String clientSecret;

    public SecurityProperties(
            @Value("${security.oauth.google.client_id}") String clientId,
            @Value("${security.oauth.google.client_secret}") String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

}

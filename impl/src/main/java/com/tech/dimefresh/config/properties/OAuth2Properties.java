package com.tech.dimefresh.config.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class OAuth2Properties {

    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final String authUri;
    private final String tokenUri;
    private final String userInfoUri;
    private final String scopes;

    public OAuth2Properties(
            @Value("${oauth2.google.client-id}") String clientId,
            @Value("${oauth2.google.client-secret}") String clientSecret,
            @Value("${oauth2.google.redirect-uri}") String redirectUri,
            @Value("${oauth2.google.auth-uri}") String authUri,
            @Value("${oauth2.google.token-uri}") String tokenUri,
            @Value("${oauth2.google.scopes}") String scopes,
            @Value("${oauth2.google.user-info-uri}") String userInfoUri
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.authUri = authUri;
        this.tokenUri = tokenUri;
        this.userInfoUri = userInfoUri;
        this.scopes = scopes;
    }
}

package com.tech.dimefresh.service;


import com.tech.dimefresh.config.properties.OAuth2Properties;
import com.tech.dimefresh.dto.AccountCreateDto;
import com.tech.dimefresh.dto.OAuth2TokenResponse;
import com.tech.dimefresh.dto.OAuthUserRespDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OAuth2Service {
    private final OAuth2Properties oAuth2Properties;
    private final RestTemplate restTemplate;
    private final AccountService accountService;
    private final SessionService sessionService;

    public String loginByOAuth2(String authorizationCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", authorizationCode);
        body.add("client_id", oAuth2Properties.getClientId());
        body.add("client_secret", oAuth2Properties.getClientSecret());
        body.add("redirect_uri", oAuth2Properties.getRedirectUri());
        body.add("grant_type",    "authorization_code");

        HttpEntity<MultiValueMap<String, String>> multiValueMapHttpEntity = new HttpEntity<>(body, headers);

        OAuth2TokenResponse tokenResponseBody = restTemplate.exchange(
                oAuth2Properties.getTokenUri(),
                HttpMethod.POST,
                multiValueMapHttpEntity,
                OAuth2TokenResponse.class
        ).getBody();

        HttpHeaders tokenExchangeHeaders = new HttpHeaders();
        tokenExchangeHeaders.setBearerAuth(tokenResponseBody.accessToken());

        HttpEntity<String> httpEntityUserInfo = new HttpEntity<>(tokenExchangeHeaders);

        OAuthUserRespDto responseDto = restTemplate.exchange(
                        "https://openidconnect.googleapis.com/v1/userinfo",
                        HttpMethod.GET,
                        httpEntityUserInfo,
                        OAuthUserRespDto.class
                )
                .getBody();

        if(responseDto.email() == null)
            throw new RuntimeException("Непредвиденная ошибка");

        Long accountId;

        if(!accountService.existsAccountWithEmail(responseDto.email())) {
            accountId = accountService.createAccount(new AccountCreateDto(
                    responseDto.name(),
                    null,
                    responseDto.email(),
                    true,
                    responseDto.sub()
            )).id();

        }
        else {
            accountId = accountService.findByEmail(responseDto.email()).id();
        }

        return sessionService.createSession(accountId);

    }
}

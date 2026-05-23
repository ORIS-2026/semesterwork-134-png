package com.tech.dimefresh.security.controller;


import com.tech.dimefresh.config.properties.OAuth2Properties;
import com.tech.dimefresh.security.controller.util.CookiesUtils;
import com.tech.dimefresh.service.OAuth2Service;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@RestController
@RequestMapping("/api/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {
    private final OAuth2Properties oAuth2Properties;
    private final OAuth2Service oAuth2Service;

    @GetMapping("/authorize")
    public void authorize(HttpServletResponse response) {

        String authUrl = UriComponentsBuilder
                .fromUriString(oAuth2Properties.getAuthUri())
                .queryParam("client_id", oAuth2Properties.getClientId())
                .queryParam("redirect_uri", oAuth2Properties.getRedirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", oAuth2Properties.getScopes())
                .build()
                .toUriString();

        try {
            response.sendRedirect(authUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/callback")
    public ResponseEntity<?> callback(
            @RequestParam String code,
            @RequestParam(required = false) String error,
            HttpServletResponse response) {

        if (error != null) {
            return ResponseEntity.badRequest().build();
        }

        String sessionId = oAuth2Service.loginByOAuth2(code);

        response.addCookie(
                CookiesUtils.prepareSessionCookie(sessionId)
        );

        return ResponseEntity.ok(null);
    }
}

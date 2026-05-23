package com.tech.dimefresh.utils;


import lombok.experimental.UtilityClass;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

@UtilityClass
public class HttpUtils {

    public HttpHeaders prepareHeadersForGeneration(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return headers;
    }

    public HttpHeaders prepareHeadersForResult(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }
}

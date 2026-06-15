package com.tech.dimefresh.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OAuth2TokenResponse (
        @JsonProperty("access_token")
        String accessToken
) {
}

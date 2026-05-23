package com.tech.dimefresh.dto;

public record OAuthUserRespDto(
        String sub,
        String name,
        String email
) {
}
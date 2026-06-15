package com.tech.dimefresh.dto;

public record AccountInfoDto(
        Long id,
        String name,
        String email,
        Boolean oauthed,
        String avatarUrl
) {
}

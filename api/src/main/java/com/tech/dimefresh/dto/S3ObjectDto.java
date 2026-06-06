package com.tech.dimefresh.dto;

public record S3ObjectDto(
        String key,
        String contentType,
        String url
) {}

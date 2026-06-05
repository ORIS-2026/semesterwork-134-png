package com.tech.dimefresh.dto;

import java.time.Instant;
import java.util.UUID;

public record NewsDto(
        UUID newsId,
        String prompt,
        String imageUrl,

        Long accountId,
        String name,

        Instant publishedAt
) {
}

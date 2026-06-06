package com.tech.dimefresh.service.dto;

import java.time.Instant;
import java.util.UUID;

public record NewsDataProjection(
        UUID newsId,
        String prompt,

        String s3Bucket,
        String s3Key,

        Long accountId,
        String name,

        Instant publishedAt,
        Long likedAccounts
) {
}

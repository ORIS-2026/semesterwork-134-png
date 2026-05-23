package com.tech.dimefresh.dto;

import java.util.UUID;

public record ChatMessageDto(
        UUID id,
        Boolean isTextMsg,
        Boolean byBot,
        String textMsgContent,
        S3ObjectDto s3Object
) {}

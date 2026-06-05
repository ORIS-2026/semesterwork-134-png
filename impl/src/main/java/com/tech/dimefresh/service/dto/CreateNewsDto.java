package com.tech.dimefresh.service.dto;

import java.util.UUID;

public record CreateNewsDto(
        UUID imageChatMessageId,
        UUID userChatMessageId
) {
}

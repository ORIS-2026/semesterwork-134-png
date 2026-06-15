package com.tech.dimefresh.dto;

import org.springframework.web.multipart.MultipartFile;

public record AddAvatarDto(
        Long accountId,
        MultipartFile multipartFile
) {
}

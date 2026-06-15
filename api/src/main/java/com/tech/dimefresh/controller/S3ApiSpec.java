package com.tech.dimefresh.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "S3", description = "Получение presigned URL для объектов из S3")
@RequestMapping("/api/s3")
public interface S3ApiSpec {

    @Operation(summary = "Получить URL объекта", description = "Возвращает временный presigned URL для скачивания объекта из S3")
    @ApiResponse(responseCode = "200", description = "Presigned URL")
    @GetMapping("/{key}")
    String getObjectUrl(
            @Parameter(description = "Ключ объекта в S3") @PathVariable String key
    );
}

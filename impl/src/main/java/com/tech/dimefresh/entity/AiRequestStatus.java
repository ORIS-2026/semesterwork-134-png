package com.tech.dimefresh.entity;

import lombok.Getter;

@Getter
public enum AiRequestStatus {
    PROCESSING("processing"), SUCCESS("success");

    private final String apiRepresentation;

    AiRequestStatus(String apiRepresentation) {
        this.apiRepresentation = apiRepresentation;
    }
}
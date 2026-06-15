package com.tech.dimefresh.entity;

import java.util.UUID;

public record SessionEntity (
        Long userId,
        UUID sessionId
){
}

package com.tech.dimefresh.scheduler.dto;

import java.util.List;

public record GenApiResultResponse (
        String status,
        List<String> result
){
}

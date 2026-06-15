package com.tech.dimefresh.s3;

public record S3Model (
    String key,
    String contentType,
    String bucket,
    byte[] file
){}

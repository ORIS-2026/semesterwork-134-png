package com.tech.dimefresh.config.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class S3Properties {

    private final String username;
    private final String password;
    private final String url;
    private final String publicUrl;
    private final String pool;
    private final String imagesBucket;

    public S3Properties(@Value("${minio.root.user}") String username,
                        @Value("${minio.root.password}") String password,
                        @Value("${minio.url}") String url,
                        @Value("${minio.public-url}") String publicUrl,
                        @Value("${minio.pool}") String pool,
                        @Value("${minio.bucket.images}") String imagesBucket) {
        this.username = username;
        this.password = password;
        this.url = url;
        this.publicUrl = publicUrl;
        this.pool = pool;
        this.imagesBucket = imagesBucket;
    }
}

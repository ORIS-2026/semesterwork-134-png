package com.tech.dimefresh.config;


import com.tech.dimefresh.config.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
public class S3Config {
    private final S3Properties s3Properties;


    @Bean
    public S3Client s3Client(){
        return S3Client.builder()
                .serviceConfiguration(s3Configuration())
                .httpClient(ApacheHttpClient.create())
                .region(Region.of(s3Properties.getPool()))
                .endpointOverride(URI.create(s3Properties.getUrl()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials()))
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(s3Properties.getPool()))
                .endpointOverride(URI.create(s3Properties.getPublicUrl()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials()))
                .serviceConfiguration(s3Configuration())
                .build();
    }

    private S3Configuration s3Configuration() {
        return S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build();
    }

    private AwsCredentials awsCredentials() {
        return AwsBasicCredentials
                .create(s3Properties.getUsername(), s3Properties.getPassword());
    }

}

package com.tech.dimefresh.controller.rest;

import com.tech.dimefresh.config.properties.S3Properties;
import com.tech.dimefresh.controller.S3ApiSpec;
import com.tech.dimefresh.s3.S3Manager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class S3Api implements S3ApiSpec {

    private final S3Properties s3Properties;
    private final S3Manager s3Manager;

    @Override
    public String getObjectUrl(String key) {
        return s3Manager.get(key, s3Properties.getImagesBucket());
    }
}

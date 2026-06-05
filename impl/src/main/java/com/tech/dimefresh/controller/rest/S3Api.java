package com.tech.dimefresh.controller.rest;


import com.tech.dimefresh.config.properties.S3Properties;
import com.tech.dimefresh.s3.S3Manager;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/s3")
@RequiredArgsConstructor
public class S3Api {
    private final S3Properties s3Properties;
    private final S3Manager s3Manager;


    @GetMapping(path = "/{key}")
    public String getObjectUrl(@PathVariable String key) {
        return s3Manager.get(key, s3Properties.getImagesBucket());
    }
}

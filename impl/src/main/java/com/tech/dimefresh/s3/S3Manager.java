package com.tech.dimefresh.s3;


import com.tech.dimefresh.config.properties.S3Properties;
import com.tech.dimefresh.s3.util.S3Util;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;


import java.net.URL;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3Manager {
    private final S3Client s3Client;
    private final S3Properties s3Properties;

    @PostConstruct
    public void initBucket() {
        if(!S3Util.doesBucketExist(s3Properties.getImagesBucket(), s3Client)){
            s3Client.createBucket(request -> request.bucket(s3Properties.getImagesBucket()));
            log.info("Bucket для файлов сгенерированных картинок создан");
        }
    }

    public void save(S3Model object) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(object.bucket())
                .key(object.key())
                .contentType(object.contentType())
                .build();
        PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest,
                RequestBody.fromBytes(object.file()));
    }

    public URL get(String key, String bucket) {
        try {
            GetUrlRequest getObjectRequest = GetUrlRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            return s3Client.utilities().getUrl(getObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

}

package com.tech.dimefresh.s3.util;


import lombok.experimental.UtilityClass;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;

@UtilityClass
public class S3Util {

    public boolean doesBucketExist(String bucketName, S3Client s3SyncClient) {
        try {
            s3SyncClient.headBucket(r -> r.bucket(bucketName));
            return true;
        } catch (NoSuchBucketException exception) {
            return false;
        }
    }
}

package dev.knalis.vleapi.service.amazon;

import com.amazonaws.services.s3.AmazonS3;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class S3BucketInitializer {
    private final AmazonS3 s3;
    private final String bucket;

    public S3BucketInitializer(AmazonS3 s3, @Value("${cloud.s3.bucket}") String bucket) {
        this.s3 = s3;
        this.bucket = bucket;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void ensureBucket() {
        try {
            if (!s3.doesBucketExistV2(bucket)) {
                s3.createBucket(bucket);
            }
        } catch (Exception ignored) {
        }
    }
}


package dev.knalis.vleapi.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonS3Config {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static:us-east-1}")
    private String region;

    @Value("${cloud.s3.bucket}")
    private String bucket;

    @Value("${cloud.s3.endpoint:}")
    private String endpoint; // optional (e.g., http://minio:9000)

    @Bean
    public AmazonS3 amazonS3() {
        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)));

        if (endpoint != null && !endpoint.isBlank()) {
            builder = builder
                    .withPathStyleAccessEnabled(true)
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region));
        } else {
            builder = builder.withRegion(region);
        }
        return builder.build();
    }
}
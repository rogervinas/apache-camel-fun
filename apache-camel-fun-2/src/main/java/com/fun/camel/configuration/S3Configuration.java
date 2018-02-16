package com.fun.camel.configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Configuration {

    @Bean("s3Credentials")
    public AWSCredentials createS3Credentials(
            @Value("${s3.credentials.accessKey}") String accessKey,
            @Value("${s3.credentials.secretKey}") String secretKey
    ) {
        return new BasicAWSCredentials(accessKey, secretKey);
    }

    @Bean("s3EndpointConfiguration")
    public AWSKMSClientBuilder.EndpointConfiguration createS3EndpointConfiguration(
            @Value("${s3.endpoint}") String endpoint,
            @Value("${s3.region}") String region
    ) {
        return new AwsClientBuilder.EndpointConfiguration(endpoint, region);
    }

    @Bean("s3Client")
    public AmazonS3 createS3Client(
        AWSCredentials credentials,
        AWSKMSClientBuilder.EndpointConfiguration endpointConfiguration
    ) {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withPathStyleAccessEnabled(false)
                .withEndpointConfiguration(endpointConfiguration)
                .build();
    }
}

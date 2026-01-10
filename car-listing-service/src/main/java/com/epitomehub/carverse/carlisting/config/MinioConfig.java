package com.epitomehub.carverse.carlisting.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Bean
    public MinioClient minioClient(
            @Value("${storage.s3.endpoint}") String endpoint,
            @Value("${storage.s3.accessKey}") String accessKey,
            @Value("${storage.s3.secretKey}") String secretKey
    ) {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}

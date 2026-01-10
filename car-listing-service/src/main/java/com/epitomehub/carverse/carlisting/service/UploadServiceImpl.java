package com.epitomehub.carverse.carlisting.service;

import com.epitomehub.carverse.carlisting.dto.PresignUploadRequest;
import com.epitomehub.carverse.carlisting.dto.PresignUploadResponse;
import com.epitomehub.carverse.carlisting.entity.Car;
import com.epitomehub.carverse.carlisting.repository.CarRepository;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UploadServiceImpl implements UploadService {

    private final CarRepository carRepository;
    private final MinioClient minioClient;

    @Value("${storage.s3.endpoint}")
    private String endpoint;

    @Value("${storage.s3.bucket}")
    private String bucket;

    @Value("${storage.s3.publicBaseUrl}")
    private String publicBaseUrl;

    @Override
    public PresignUploadResponse presignCarImageUpload(Long ownerId, Long carId, PresignUploadRequest req) {

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found: " + carId));

        if (car.getOwnerId() == null || !car.getOwnerId().equals(ownerId)) {
            throw new AccessDeniedException("Forbidden");
        }

        String cleanName = req.fileName().replaceAll("[^a-zA-Z0-9._-]", "_");
        String objectKey = "cars/" + carId + "/"
                + Instant.now().toEpochMilli() + "_"
                + UUID.randomUUID() + "_"
                + cleanName;

        try {
            String uploadUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucket)
                            .object(objectKey)
                            .expiry(10 * 60) // 10 minutes
                            .build()
            );

            String publicUrl = publicBaseUrl + "/" + objectKey;
            return new PresignUploadResponse(objectKey, uploadUrl, publicUrl);

        } catch (Exception e) {
            log.error("Presign failed. endpoint={}, bucket={}, objectKey={}",
                    endpoint, bucket, objectKey, e);
            throw new IllegalStateException("Failed to generate upload url: " + e.getMessage(), e);
        }
    }
}

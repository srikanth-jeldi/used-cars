package com.epitomehub.carverse.carlisting.service;

import com.epitomehub.carverse.carlisting.dto.PresignUploadRequest;
import com.epitomehub.carverse.carlisting.dto.PresignUploadResponse;

public interface UploadService {
    PresignUploadResponse presignCarImageUpload(Long ownerId, Long carId, PresignUploadRequest req);
}

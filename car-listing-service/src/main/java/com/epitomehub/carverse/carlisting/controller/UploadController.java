package com.epitomehub.carverse.carlisting.controller;

import com.epitomehub.carverse.carlisting.dto.PresignUploadRequest;
import com.epitomehub.carverse.carlisting.dto.PresignUploadResponse;
import com.epitomehub.carverse.carlisting.service.UploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/uploads")
public class UploadController {

    private final UploadService uploadService;

    @PostMapping("/cars/{carId}/images/presign")
    public PresignUploadResponse presign(@PathVariable Long carId,
                                         @Valid @RequestBody PresignUploadRequest req,
                                         Authentication authentication) {

        // IMPORTANT:
        // Replace this line with your real JWT userId extractor if your principal is not Long.
        // For now keeping your current logic.
        Long ownerId = (Long) authentication.getPrincipal();

        return uploadService.presignCarImageUpload(ownerId, carId, req);
    }
}

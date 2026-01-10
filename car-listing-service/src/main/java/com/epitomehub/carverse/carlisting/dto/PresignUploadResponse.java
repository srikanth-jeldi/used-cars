package com.epitomehub.carverse.carlisting.dto;

public record PresignUploadResponse(
        String objectKey,
        String uploadUrl,
        String publicUrl
) {}

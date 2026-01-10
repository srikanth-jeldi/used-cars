package com.epitomehub.carverse.carlisting.dto;

import jakarta.validation.constraints.NotBlank;

public record PresignUploadRequest(
        @NotBlank String fileName,
        @NotBlank String contentType
) {}

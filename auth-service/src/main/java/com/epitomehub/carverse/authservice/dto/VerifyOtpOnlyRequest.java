package com.epitomehub.carverse.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyOtpOnlyRequest {

    @NotBlank
    private String otpCode;
}

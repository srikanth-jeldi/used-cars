package com.epitomehub.carverse.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyOtpOnlyRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String otpCode;
}

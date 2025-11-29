package com.epitomehub.carverse.notificationservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpNotificationRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String fullName;

    @NotBlank
    private String otpCode;

    // optional: for SMS later
    private String phone;
}

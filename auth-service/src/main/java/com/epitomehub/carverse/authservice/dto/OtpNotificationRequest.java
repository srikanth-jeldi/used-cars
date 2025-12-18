package com.epitomehub.carverse.authservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OtpNotificationRequest {
    private String email;
    private String fullName;
    private String phone;
    private String otpCode;
}

package com.epitomehub.carverse.authservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OtpNotificationRequest {

    private String email;
    private String fullName;
    private String otpCode;
    private String phone;
}

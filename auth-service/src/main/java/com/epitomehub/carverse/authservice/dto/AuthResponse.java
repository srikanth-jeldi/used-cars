package com.epitomehub.carverse.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private String fullName;
    private String email;
    private String phone;
    private boolean enabled;
}

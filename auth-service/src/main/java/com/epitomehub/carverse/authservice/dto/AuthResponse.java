package com.epitomehub.carverse.authservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;

    private String fullName;
    private String email;
    private String phone;

    private boolean enabled;
}

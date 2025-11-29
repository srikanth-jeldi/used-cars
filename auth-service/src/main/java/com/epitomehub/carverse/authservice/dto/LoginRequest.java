package com.epitomehub.carverse.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank
    private String username; // can be email or phone

    @NotBlank
    private String password;
}

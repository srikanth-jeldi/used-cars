package com.epitomehub.carverse.authservice.dto;

import java.util.Set;

public record MeResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        Set<String> roles
) {}

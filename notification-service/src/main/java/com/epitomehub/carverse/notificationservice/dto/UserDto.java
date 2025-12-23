package com.epitomehub.carverse.notificationservice.dto;

public record UserDto(
        Long id,
        String email,
        String fullName,
        String phone
) {}

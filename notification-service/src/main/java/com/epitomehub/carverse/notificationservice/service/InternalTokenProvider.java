package com.epitomehub.carverse.notificationservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InternalTokenProvider {

    private final String token;

    public InternalTokenProvider(
            @Value("${security.internal.token:carverse-internal-secret-123}") String token
    ) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
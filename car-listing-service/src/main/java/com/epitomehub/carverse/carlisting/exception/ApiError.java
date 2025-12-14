package com.epitomehub.carverse.carlisting.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Getter
@Builder
public class ApiError {
    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    // for validation field errors: {"title":"must not be blank", ...}
    private Map<String, String> fieldErrors;
}
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
    private String error;      // short reason: "Unauthorized", "Bad Request"
    private String message;    // detailed message
    private String path;

    private Map<String, String> fieldErrors;

    public static ApiError of(int status, String error, String message, String path) {
        return ApiError.builder()
                .timestamp(Instant.now())
                .status(status)
                .error(error)
                .message(message)
                .path(path)
                .build();
    }

    public static ApiError of(int status, String message, String path) {
        // backward-compatible overload if you want
        return of(status, HttpStatusText.from(status), message, path);
    }

    // small helper to avoid repeating strings
    static final class HttpStatusText {
        static String from(int status) {
            return switch (status) {
                case 400 -> "Bad Request";
                case 401 -> "Unauthorized";
                case 403 -> "Forbidden";
                case 404 -> "Not Found";
                case 405 -> "Method Not Allowed";
                default -> "Internal Server Error";
            };
        }
    }
}

package com.epitomehub.carverse.authservice.controller;

import com.epitomehub.carverse.authservice.dto.*;
import com.epitomehub.carverse.authservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/test")
    public String test() {
        return "Auth Service is up 🚗🔑";
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyOtp(@Valid @RequestBody VerifyOtpOnlyRequest request) {
        return ResponseEntity.ok(authService.verifyRegistrationOtpOnly(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> me(Authentication authentication) {

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new AccessDeniedException("Unauthorized");
        }

        Object principal = authentication.getPrincipal();

        Long userId;
        if (principal instanceof Long) {
            userId = (Long) principal;
        } else if (principal instanceof String) {
            userId = Long.parseLong((String) principal);
        } else {
            throw new AccessDeniedException("Invalid principal");
        }

        return ResponseEntity.ok(authService.me(userId));
    }
}

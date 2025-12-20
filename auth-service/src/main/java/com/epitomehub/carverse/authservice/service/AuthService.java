package com.epitomehub.carverse.authservice.service;

import com.epitomehub.carverse.authservice.dto.*;
import com.epitomehub.carverse.authservice.entity.OtpToken;
import com.epitomehub.carverse.authservice.entity.Role;
import com.epitomehub.carverse.authservice.entity.User;
import com.epitomehub.carverse.authservice.exception.BadRequestException;
import com.epitomehub.carverse.authservice.exception.ResourceNotFoundException;
import com.epitomehub.carverse.authservice.repository.OtpTokenRepository;
import com.epitomehub.carverse.authservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Random;
import java.util.Set;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private static final String NOTIFICATION_SERVICE_OTP_URL =
            "http://notification-service/api/notifications/otp";

    private final UserRepository userRepository;
    private final OtpTokenRepository otpTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RestTemplate restTemplate;

    public AuthService(UserRepository userRepository,
                       OtpTokenRepository otpTokenRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.otpTokenRepository = otpTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.restTemplate = restTemplate;
    }

    public ApiResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already in use");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Phone already in use");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(false) // must verify OTP
                .locked(false)
                .roles(Set.of(Role.ROLE_USER))
                .build();

        userRepository.save(user);

        // Generate OTP (6-digit)
        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        // Save OTP in DB
        otpTokenRepository.save(
                OtpToken.builder()
                        .email(user.getEmail())                    // ← Changed from identifier
                        .otp(otp)                                  // ← Changed from otpCode
                        .type(OtpToken.OtpType.REGISTRATION)
                        .expiresAt(Instant.now().plusSeconds(5 * 60)) // 5 minutes
                        .used(false)
                        .build()
        );

        // Prepare request for notification service
        OtpNotificationRequest otpRequest = OtpNotificationRequest.builder()
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .otpCode(otp)
                .build();

        postOtpWithRetry(otpRequest);

        return new ApiResponse(true, "User registered. OTP sent to your email/phone.");
    }

    public ApiResponse verifyRegistrationOtpOnly(VerifyOtpOnlyRequest request) {

        String email = request.getEmail();
        String providedOtp = request.getOtpCode();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        OtpToken token = otpTokenRepository
                .findTopByEmailAndTypeOrderByIdDesc(email, OtpToken.OtpType.REGISTRATION) // ← Changed from findTopByIdentifier...
                .orElseThrow(() -> new BadRequestException("OTP not found. Please request again."));

        if (token.isUsed()) {
            throw new BadRequestException("OTP already used. Please request again.");
        }
        if (token.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("OTP expired. Please request again.");
        }
        if (!token.getOtp().equals(providedOtp)) {  // ← Changed from getOtpCode()
            throw new BadRequestException("Invalid OTP");
        }

        token.setUsed(true);
        otpTokenRepository.save(token);

        user.setEnabled(true);
        userRepository.save(user);

        return new ApiResponse(true, "OTP verified. Account activated.");
    }

    public AuthResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.isEnabled()) {
            throw new BadRequestException("Account is disabled. Please verify OTP.");
        }

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .enabled(user.isEnabled())
                .build();
    }

    private void postOtpWithRetry(OtpNotificationRequest otpRequest) {
        int maxAttempts = 3;
        long delayMs = 400;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                restTemplate.postForEntity(
                        NOTIFICATION_SERVICE_OTP_URL,
                        otpRequest,
                        Void.class
                );
                return; // Success → exit
            } catch (RestClientException ex) {
                log.error("OTP call failed (attempt {}/{}): {}", attempt, maxAttempts, ex.getMessage());

                if (attempt == maxAttempts) {
                    throw new BadRequestException("Unable to send OTP right now. Please try again.");
                }

                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new BadRequestException("Unable to send OTP right now. Please try again.");
                }
            }
        }
    }
    public MeResponse getMeById(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return MeResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .enabled(user.isEnabled())
                .locked(user.isLocked())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
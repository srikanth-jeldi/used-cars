package com.epitomehub.carverse.authservice.service;

import com.epitomehub.carverse.authservice.dto.ApiResponse;
import com.epitomehub.carverse.authservice.dto.AuthResponse;
import com.epitomehub.carverse.authservice.dto.LoginRequest;
import com.epitomehub.carverse.authservice.dto.OtpNotificationRequest;
import com.epitomehub.carverse.authservice.dto.RegisterRequest;
import com.epitomehub.carverse.authservice.dto.VerifyOtpOnlyRequest;
import com.epitomehub.carverse.authservice.entity.Role;
import com.epitomehub.carverse.authservice.entity.User;
import com.epitomehub.carverse.authservice.exception.BadRequestException;
import com.epitomehub.carverse.authservice.exception.ResourceNotFoundException;
import com.epitomehub.carverse.authservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Random;
import java.util.Set;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final RestTemplate restTemplate;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       OtpService otpService,
                       RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.otpService = otpService;
        this.restTemplate = restTemplate;
    }

    // 🔹 REGISTER: creates user (enabled=false), generates OTP, stores it, and calls notification-service
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
                .enabled(true)          // <<< NEW
                .locked(false)          // <<< NEW
                // .roles(Set.of(Role.USER))  // mee project lo roles ela unayo ade vadandi
                .build();

        userRepository.save(user);

        // Generate OTP (6 digits)
        String otpCode = String.format("%06d", new Random().nextInt(999999));

        // Store OTP mapped to this user's email (identifier)
        otpService.storeOtp(user.getEmail(), otpCode);

        // Call notification-service to send email/SMS
        OtpNotificationRequest otpRequest = OtpNotificationRequest.builder()
                .email(user.getEmail())
                .fullName(user.getFullName())
                .otpCode(otpCode)
                .phone(user.getPhone())
                .build();

        try {
            restTemplate.postForEntity(
                    "http://notification-service/api/notifications/otp",
                    otpRequest,
                    Void.class
            );
        } catch (Exception ex) {
            // Log and continue; you can change this to fail the request if you want strict behavior
            log.error("Failed to call notification-service: {}", ex.getMessage(), ex);
        }

        // Do NOT return OTP in response now (more secure)
        return new ApiResponse(true,
                "User registered. OTP sent to your email/phone.");
    }

    // 🔹 VERIFY OTP: only receives otpCode, fetches identifier from OtpService
    public ApiResponse verifyRegistrationOtpOnly(VerifyOtpOnlyRequest request) {

        String otp = request.getOtpCode();

        String identifier = otpService.getIdentifierByOtp(otp);
        if (identifier == null) {
            throw new BadRequestException("Invalid or expired OTP");
        }

        User user = userRepository.findByEmail(identifier)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setEnabled(true);
        userRepository.save(user);

        // Remove OTP after successful verification
        otpService.removeOtp(otp);

        return new ApiResponse(true, "OTP verified. Account activated.");
    }

    // 🔹 LOGIN: email/phone + password → JWT + refresh token
    public AuthResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // userDetails.getUsername() is email from CustomUserDetailsService
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.isEnabled()) {
            throw new BadRequestException("Account not verified. Please verify OTP.");
        }

        // 🔑 Access token from User (includes userId claim)
        String accessToken = jwtService.generateToken(user);

        // 🔁 Refresh token from UserDetails
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
}

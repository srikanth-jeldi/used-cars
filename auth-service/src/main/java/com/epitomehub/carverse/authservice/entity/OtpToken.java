package com.epitomehub.carverse.authservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "otp_tokens")
public class OtpToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 6-digit OTP
    @Column(nullable = false, length = 6)
    private String otpCode;

    @Column(nullable = false, length = 100)
    private String identifier; // email or phone

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean used;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OtpType type;

    public enum OtpType {
        REGISTRATION,
        FORGOT_PASSWORD
    }
}

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
    private Long id;  // Perfect match with migration

    @Column(name = "email", nullable = false, length = 255)
    private String email;  // Match with DB column "email"

    @Column(name = "otp", nullable = false, length = 10)
    private String otp;  // Match with DB column "otp"

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "used", nullable = false)
    private boolean used;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OtpType type;

    public enum OtpType {
        REGISTRATION,
        FORGOT_PASSWORD
    }
}
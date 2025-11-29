package com.epitomehub.carverse.authservice.repository;

import com.epitomehub.carverse.authservice.entity.OtpToken;
import com.epitomehub.carverse.authservice.entity.OtpToken.OtpType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

    Optional<OtpToken> findTopByIdentifierAndTypeAndUsedFalseOrderByExpiresAtDesc(
            String identifier,
            OtpType type
    );
}

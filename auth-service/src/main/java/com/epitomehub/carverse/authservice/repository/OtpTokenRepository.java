package com.epitomehub.carverse.authservice.repository;

import com.epitomehub.carverse.authservice.entity.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {

    Optional<OtpToken> findTopByEmailAndTypeOrderByIdDesc(String email, OtpToken.OtpType type);
}

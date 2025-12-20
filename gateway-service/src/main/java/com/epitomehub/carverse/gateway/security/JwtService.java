package com.epitomehub.carverse.gateway.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret}")
    private String secretKey;

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token); // ← idi signature + expiration full validate chestundi
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("Invalid JWT signature");
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired");
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported");
        } catch (IllegalArgumentException e) {
            log.warn("JWT token is empty");
        } catch (Exception e) {
            log.warn("JWT validation failed: {}", e.getMessage());
        }
        return false;
    }

    public Long extractUserId(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Object userIdObj = claims.get("userId");
            if (userIdObj == null) return null;
            if (userIdObj instanceof Number number) return number.longValue();
            if (userIdObj instanceof String str) return Long.parseLong(str);
            return null;
        } catch (Exception e) {
            log.warn("Failed to extract userId", e);
            return null;
        }
    }

    public List<String> extractRoles(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Object roles = claims.get("roles");
            if (roles instanceof List<?> list) {
                return list.stream()
                        .map(Object::toString)
                        .toList();
            }
            return List.of();
        } catch (Exception e) {
            log.warn("Failed to extract roles", e);
            return List.of();
        }
    }

    public String extractUsername(String token) {
        try {
            return extractAllClaims(token).getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
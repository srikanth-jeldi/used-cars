package com.epitomehub.carverse.chatservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Base64;

@Service
public class JwtService {

    private final Key signingKey;

    public JwtService(@Value("${jwt.secret}") String base64Secret) {
        byte[] keyBytes = Base64.getDecoder().decode(base64Secret);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);

        // Choose ONE based on how your auth-service creates token:
        // 1) If you store userId in subject:
        // return Long.parseLong(claims.getSubject());

        // 2) If you store userId as a claim "userId":
        Object userId = claims.get("userId");
        if (userId == null) return null;

        if (userId instanceof Number n) return n.longValue();
        return Long.parseLong(userId.toString());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

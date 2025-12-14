package com.epitomehub.carverse.notificationservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // actuator & error
                        .requestMatchers("/actuator/**", "/error").permitAll()

                        // OTP (called by auth-service)
                        .requestMatchers(HttpMethod.POST, "/api/notifications/otp").permitAll()

                        // Chat notification (called by chat-service)
                        .requestMatchers(HttpMethod.POST, "/api/notifications/chat-message").permitAll()

                        // everything else secured
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}

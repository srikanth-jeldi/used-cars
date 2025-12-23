package com.epitomehub.carverse.notificationservice.config;

import com.epitomehub.carverse.notificationservice.security.InternalTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final InternalTokenFilter internalTokenFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // We are not using form-login / http-basic / JWT here
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // Internal token filter protects /api/internal/**
                .addFilterBefore(internalTokenFilter, UsernamePasswordAuthenticationFilter.class)

                .authorizeHttpRequests(auth -> auth

                        // actuator & error
                        .requestMatchers("/actuator/**", "/error").permitAll()

                        // OTP (called by auth-service)
                        .requestMatchers(HttpMethod.POST, "/api/notifications/otp").permitAll()

                        // Chat notification (called by chat-service)
                        .requestMatchers(HttpMethod.POST, "/api/notifications/chat-message").permitAll()

                        // internal endpoints must have X-INTERNAL-TOKEN (enforced by filter)
                        .requestMatchers("/api/internal/**").permitAll()
                        .requestMatchers("api/notifications/**").permitAll()
                        // everything else blocked (safer for internal microservice)
                        .anyRequest().denyAll()
                );

        return http.build();
    }
}

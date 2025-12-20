package com.epitomehub.carverse.chatservice.security;

import com.epitomehub.carverse.chatservice.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // IMPORTANT: enable CORS in Spring Security
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // IMPORTANT: allow browser preflight (CORS)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // allow websocket sockjs handshake endpoints (/ws-chat/info etc.)
                        .requestMatchers("/ws-chat/**").permitAll()

                        // (optional) actuator for testing
                        .requestMatchers("/actuator/**").permitAll()

                        // If you want this REST endpoint to work WITHOUT JWT, uncomment:
                        // .requestMatchers(HttpMethod.POST, "/api/chats/messages").permitAll()

                        .anyRequest().authenticated()
                )

                // ✅ IMPORTANT: add JWT filter before Spring’s default auth filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

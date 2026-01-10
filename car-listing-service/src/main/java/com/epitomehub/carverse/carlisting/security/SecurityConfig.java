package com.epitomehub.carverse.carlisting.security;

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

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()

                        // IMPORTANT: protect /me first (more specific)
                        .requestMatchers(HttpMethod.GET, "/api/cars/me" ).authenticated()
                        .requestMatchers(HttpMethod.GET,"/api/cars/me/**").authenticated()
                        // public reads (everything else)
                        .requestMatchers(HttpMethod.GET, "/api/cars/**").permitAll()

                        // protected writes
                        .requestMatchers(HttpMethod.POST, "/api/cars/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/cars/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/cars/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/cars/**").authenticated()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);  // Added this to ensure the JWT filter is in the chain

        return http.build();
    }
}
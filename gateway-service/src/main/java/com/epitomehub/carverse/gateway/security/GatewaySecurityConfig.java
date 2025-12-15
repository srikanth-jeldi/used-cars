package com.epitomehub.carverse.gatewayservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                // 🔴 CSRF off in gateway
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // allow health
                        .pathMatchers("/actuator/**").permitAll()
                        // allow auth APIs (login/register) without token
                        .pathMatchers("/api/auth/**").permitAll()
                        // for now, allow everything (later we can secure with JWT at gateway)
                        .anyExchange().permitAll()
                )
                .build();
    }
}

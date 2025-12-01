package com.epitomehub.carverse.notificationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // REST API, CSRF not needed
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Health checks open unte baguntundi
                        .requestMatchers("/actuator/**").permitAll()
                        // 🔔 OTP & chat notifications – internal only, so NO auth
                        .requestMatchers("/api/notifications/**").permitAll()
                        // Any other future endpoints -> auth required
                        .anyRequest().authenticated()
                )
                // basic auth enable chesina, above permitAll valla notifications open untayi
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}

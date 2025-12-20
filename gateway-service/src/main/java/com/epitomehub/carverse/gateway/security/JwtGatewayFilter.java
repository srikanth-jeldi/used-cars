package com.epitomehub.carverse.gateway.security;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtGatewayFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtGatewayFilter.class);

    private final JwtService jwtService;

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    // Only auth & actuator open – chat, cars protected
    private static final String[] PUBLIC_PATHS = {
            "/api/auth/**",
            "/actuator/**"
    };

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        if (isPublic(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        if (!jwtService.isTokenValid(token)) {
            log.warn("Invalid or expired JWT token for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        Long userId = jwtService.extractUserId(token);
        List<String> roles = jwtService.extractRoles(token);

        if (userId == null) {
            log.warn("UserId not found in JWT token");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        ServerHttpRequest mutated = exchange.getRequest().mutate()
                .header("X-User-Id", String.valueOf(userId))
                .header("X-Roles", String.join(",", roles))
                .build();

        return chain.filter(exchange.mutate().request(mutated).build());
    }

    private boolean isPublic(String path) {
        for (String p : PUBLIC_PATHS) {
            if (PATH_MATCHER.match(p, path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
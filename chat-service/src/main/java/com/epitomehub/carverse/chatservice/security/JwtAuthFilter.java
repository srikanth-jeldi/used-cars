package com.epitomehub.carverse.chatservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        /* -------------------------------------------------
           1) ALLOW CORS PREFLIGHT (MOST IMPORTANT FIX)
        -------------------------------------------------- */
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        /* -------------------------------------------------
           2) SKIP WEBSOCKET / SSE HANDSHAKE ENDPOINTS
        -------------------------------------------------- */
        String path = request.getRequestURI();
        if (path != null &&
                (path.startsWith("/ws-chat")
                        || path.startsWith("/sse")
                        || path.startsWith("/topic"))) {
            filterChain.doFilter(request, response);
            return;
        }

        /* -------------------------------------------------
           3) READ AUTHORIZATION HEADER
        -------------------------------------------------- */
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);

        /* -------------------------------------------------
           4) VALIDATE TOKEN
        -------------------------------------------------- */
        if (!jwtService.isTokenValid(jwt)) {
            filterChain.doFilter(request, response);
            return;
        }

        /* -------------------------------------------------
           5) EXTRACT USER ID FROM TOKEN
        -------------------------------------------------- */
        Long userId = jwtService.extractUserId(jwt);
        if (userId == null) {
            filterChain.doFilter(request, response);
            return;
        }

        /* -------------------------------------------------
           6) SET AUTHENTICATION CONTEXT
        -------------------------------------------------- */
        if (SecurityContextHolder.getContext().getAuthentication() == null) {

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userId,          // principal = userId
                            null,
                            Collections.emptyList()
                    );

            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}

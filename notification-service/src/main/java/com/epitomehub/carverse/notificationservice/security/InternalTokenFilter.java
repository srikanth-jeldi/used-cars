package com.epitomehub.carverse.notificationservice.security;

import com.epitomehub.carverse.notificationservice.service.InternalTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class InternalTokenFilter extends OncePerRequestFilter {

    private final InternalTokenProvider tokenProvider;

    @Autowired
    public InternalTokenFilter(InternalTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Protect only internal endpoints (your choice)
        String path = request.getRequestURI();
        return !path.startsWith("/api/notifications/chat-message");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader("X-INTERNAL-TOKEN");
        if (token == null || !token.equals(tokenProvider.getToken())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: invalid internal token");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
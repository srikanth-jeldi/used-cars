package com.epitomehub.carverse.chatservice.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUser {

    private CurrentUser() {}

    /**
     * Expect principal/username to be the userId (common in JWT filter).
     * If your JWT filter sets authentication name to userId string, this works.
     */
    public static Long id() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new IllegalStateException("Unauthenticated");
        }
        try {
            return Long.parseLong(auth.getName());
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Authentication name is not userId: " + auth.getName());
        }
    }
}

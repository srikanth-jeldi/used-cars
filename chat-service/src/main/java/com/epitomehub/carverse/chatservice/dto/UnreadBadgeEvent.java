package com.epitomehub.carverse.chatservice.dto;

public record UnreadBadgeEvent(
        Long conversationId,
        long unreadCount,
        long totalUnread
) {}

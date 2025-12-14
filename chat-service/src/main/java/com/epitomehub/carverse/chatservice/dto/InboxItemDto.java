package com.epitomehub.carverse.chatservice.dto;

import java.time.LocalDateTime;

public record InboxItemDto(
        Long conversationId,
        Long otherUserId,
        String lastMessage,
        LocalDateTime lastMessageAt,
        long unreadCount
) {}

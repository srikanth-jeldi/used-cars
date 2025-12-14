package com.epitomehub.carverse.chatservice.dto;

import java.time.LocalDateTime;

public record ChatMessageResponse(
        Long id,
        Long conversationId,
        Long senderId,
        Long receiverId,
        String message,
        boolean isRead,
        LocalDateTime createdAt
) {}

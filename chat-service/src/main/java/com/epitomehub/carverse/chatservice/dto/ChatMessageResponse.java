package com.epitomehub.carverse.chatservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessageResponse {

    private Long id;
    private Long senderId;
    private Long receiverId;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
}
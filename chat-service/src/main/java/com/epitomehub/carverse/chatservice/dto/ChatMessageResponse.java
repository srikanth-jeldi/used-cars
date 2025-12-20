package com.epitomehub.carverse.chatservice.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponse {

    private Long id;
    private Long conversationId;
    private Long senderId;
    private Long receiverId;
    private String message;
    private boolean read;
    private LocalDateTime createdAt;
}

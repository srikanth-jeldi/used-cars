package com.epitomehub.carverse.chatservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnreadCountResponse {
    private Long userId;
    private long unreadCount;
}
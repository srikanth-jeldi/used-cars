package com.epitomehub.carverse.chatservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConversationResponse {
    private Long id;
    private Long user1Id;
    private Long user2Id;
}

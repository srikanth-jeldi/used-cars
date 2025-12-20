package com.epitomehub.carverse.chatservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateConversationRequest {
    private Long user1Id;
    private Long user2Id;
    @NotNull
    private Long otherUserId;
}

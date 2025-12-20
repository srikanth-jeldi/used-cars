package com.epitomehub.carverse.chatservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendMessageRequest(
         Long conversationId,
        @NotNull Long receiverId,
        @NotBlank String message
) {}

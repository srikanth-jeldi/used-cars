package com.epitomehub.carverse.chatservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MarkReadRequest {

    @NotNull
    private Long conversationId;
}
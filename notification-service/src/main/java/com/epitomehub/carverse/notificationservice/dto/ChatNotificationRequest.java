package com.epitomehub.carverse.notificationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChatNotificationRequest {

    @NotNull
    private Long receiverId;

    @NotNull
    private Long senderId;

    @NotNull
    private Long conversationId;

    @NotBlank
    private String messagePreview;

    private String carTitle;
    private String chatUrl;

    private boolean sendEmail = true;
    private boolean sendSms = false;
}

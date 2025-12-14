package com.epitomehub.carverse.chatservice.integration.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatNotificationRequest {

    private String toEmail;
    private String toName;
    private String toPhone;

    private String fromName;
    private Long fromUserId;

    private String messagePreview;
    private String carTitle;
    private String chatUrl;

    private boolean sendEmail;
    private boolean sendSms;
}

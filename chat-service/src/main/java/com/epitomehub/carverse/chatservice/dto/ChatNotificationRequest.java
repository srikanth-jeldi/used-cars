package com.epitomehub.carverse.chatservice.dto;

import lombok.Data;

@Data
public class ChatNotificationRequest {

    private String fromName;
    private String toName;
    private String toEmail;
    private String toPhone;

    private String messagePreview;

    private String carTitle;   // optional
    private String chatUrl;    // deep link

    private boolean sendEmail = true;
    private boolean sendSms = false;

    private Long receiverId;
    private Long senderId;
    private Long conversationId;


}





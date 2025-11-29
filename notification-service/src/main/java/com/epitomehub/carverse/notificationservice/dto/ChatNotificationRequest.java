package com.epitomehub.carverse.notificationservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatNotificationRequest {

    @NotBlank
    private String fromName;

    @NotBlank
    private String toName;

    @NotBlank
    @Email
    private String toEmail;

    private String toPhone;

    @NotBlank
    private String messagePreview;

    private String carTitle;        // optional
    private String chatUrl;         // deep link

    private boolean sendEmail = true;
    private boolean sendSms = false;
}

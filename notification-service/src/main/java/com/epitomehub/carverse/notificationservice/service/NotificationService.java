package com.epitomehub.carverse.notificationservice.service;

import com.epitomehub.carverse.notificationservice.dto.ChatNotificationRequest;
import com.epitomehub.carverse.notificationservice.dto.OtpNotificationRequest;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final EmailService emailService;
    private final SmsService smsService;

    // OTP notification (email + optional SMS)
    public void sendOtpNotification(OtpNotificationRequest request) {

        String subject = "Your CarVerse OTP Code";
        String html = """
                <html>
                <body>
                    <p>Hi %s,</p>
                    <p>Your CarVerse verification OTP is:</p>
                    <h2>%s</h2>
                    <p>This code will expire soon. Do not share it with anyone.</p>
                    <br/>
                    <p>Regards,<br/>CarVerse Team</p>
                </body>
                </html>
                """.formatted(request.getFullName(), request.getOtpCode());

        emailService.sendHtmlMail(request.getEmail(), subject, html);

        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            smsService.sendSms(request.getPhone(),
                    "Your CarVerse OTP is " + request.getOtpCode());
        }
    }

    // Chat notification (buyer/seller message)
    public void sendChatNotification(ChatNotificationRequest request) {

        if (request.isSendEmail()) {
            String subject = "New message on CarVerse from " + request.getFromName();

            String html = """
                    <html>
                    <body>
                        <p>Hi %s,</p>
                        <p>You have a new message from <b>%s</b> on CarVerse.</p>
                        %s
                        <p>Message preview:</p>
                        <blockquote>%s</blockquote>
                        %s
                        <p>Regards,<br/>CarVerse Team</p>
                    </body>
                    </html>
                    """.formatted(
                    request.getToName(),
                    request.getFromName(),
                    request.getCarTitle() != null && !request.getCarTitle().isBlank()
                            ? "<p>Car: <b>" + request.getCarTitle() + "</b></p>"
                            : "",
                    request.getMessagePreview(),
                    request.getChatUrl() != null && !request.getChatUrl().isBlank()
                            ? "<p><a href=\"" + request.getChatUrl() + "\">Open chat</a></p>"
                            : ""
            );

            emailService.sendHtmlMail(request.getToEmail(), subject, html);
        }

        if (request.isSendSms() && request.getToPhone() != null && !request.getToPhone().isBlank()) {
            String smsText = "New CarVerse message from " + request.getFromName() +
                    ": " + request.getMessagePreview();
            smsService.sendSms(request.getToPhone(), smsText);
        }
    }
}

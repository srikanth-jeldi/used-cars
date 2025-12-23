package com.epitomehub.carverse.notificationservice.service;

import com.epitomehub.carverse.notificationservice.dto.ChatNotificationRequest;
import com.epitomehub.carverse.notificationservice.dto.OtpNotificationRequest;
import com.epitomehub.carverse.notificationservice.dto.UserDto;
import com.epitomehub.carverse.notificationservice.util.EmailRateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final EmailService emailService;
    private final SmsService smsService;
    private final EmailRateLimiter emailRateLimiter;

    // ✅ Redis-cached user fetch
    private final UserDirectoryService userDirectoryService;

    // ---------------- OTP (unchanged) ----------------

    @Async
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
            smsService.sendSms(
                    request.getPhone(),
                    "Your CarVerse OTP is " + request.getOtpCode()
            );
        }
    }

    // ---------------- CHAT NOTIFICATION (FINAL) ----------------

    @Async
    public void sendChatNotification(ChatNotificationRequest request) {

        if (request.getReceiverId() == null || request.getSenderId() == null || request.getConversationId() == null) {
            log.warn("Invalid chat notification request (missing ids): {}", request);
            return;
        }

        UserDto receiver = userDirectoryService.getUserById(request.getReceiverId());
        if (receiver == null || receiver.email() == null || receiver.email().isBlank()) {
            log.warn("Receiver email not found. receiverId={}", request.getReceiverId());
            return;
        }

        UserDto sender = userDirectoryService.getUserById(request.getSenderId());

        String toName = receiver.fullName() != null && !receiver.fullName().isBlank()
                ? receiver.fullName()
                : "User " + receiver.id();

        String fromName = sender != null && sender.fullName() != null && !sender.fullName().isBlank()
                ? sender.fullName()
                : "User " + request.getSenderId();

        // -------- EMAIL --------
        if (request.isSendEmail()) {

            // ✅ rate-limit only email (do not block SMS)
            String rateKey = request.getReceiverId() + ":" + request.getConversationId();
            if (!emailRateLimiter.allow(rateKey)) {
                log.info("Rate-limited chat email. receiverId={}, conversationId={}",
                        request.getReceiverId(), request.getConversationId());
            } else {

                String subject = "New message on CarVerse from " + fromName;

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
                        toName,
                        fromName,
                        request.getCarTitle() != null && !request.getCarTitle().isBlank()
                                ? "<p>Car: <b>" + request.getCarTitle() + "</b></p>"
                                : "",
                        request.getMessagePreview(),
                        request.getChatUrl() != null && !request.getChatUrl().isBlank()
                                ? "<p><a href=\"" + request.getChatUrl() + "\">Open chat</a></p>"
                                : ""
                );

                emailService.sendHtmlMail(receiver.email(), subject, html);
            }
        }

        // -------- SMS --------
        if (request.isSendSms()
                && receiver.phone() != null
                && !receiver.phone().isBlank()) {

            String smsText = "New CarVerse message from " + fromName +
                    ": " + request.getMessagePreview();

            smsService.sendSms(receiver.phone(), smsText);
        }

        log.info("Chat notification processed. senderId={}, receiverId={}, conversationId={}",
                request.getSenderId(), request.getReceiverId(), request.getConversationId());
    }
}

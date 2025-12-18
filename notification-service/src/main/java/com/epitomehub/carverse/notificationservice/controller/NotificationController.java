package com.epitomehub.carverse.notificationservice.controller;

import com.epitomehub.carverse.notificationservice.dto.ApiResponse;
import com.epitomehub.carverse.notificationservice.dto.ChatNotificationRequest;
import com.epitomehub.carverse.notificationservice.dto.OtpNotificationRequest;
import com.epitomehub.carverse.notificationservice.service.NotificationService;
import com.epitomehub.carverse.notificationservice.service.EmailService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final EmailService emailService;
    private static final Logger log =
            LoggerFactory.getLogger(NotificationController.class);


    public NotificationController(NotificationService notificationService,
                                  EmailService emailService) {
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    /**
     * Sends OTP via Email (SMS later)
     */
    @PostMapping("/otp")
    public ResponseEntity<Void> sendOtp(@Valid @RequestBody OtpNotificationRequest request) {

        log.info("OTP request received for email={}", request.getEmail());

        String subject = "Carverse OTP Verification";
        String body =
                "Hi " + request.getFullName() + ",\n\n"
                        + "Your OTP is: " + request.getOtpCode() + "\n\n"
                        + "If you did not request this, please ignore this email.\n\n"
                        + "Thanks,\nCarverse Team";

        emailService.sendOtpEmail(request.getEmail(), subject, body);

        return ResponseEntity.ok().build();
    }

    /**
     * Called by chat-service when a new message is sent.
     * Sends chat notification email/SMS.
     */
    @PostMapping("/chat-message")
    public ResponseEntity<ApiResponse> sendChatMessageNotification(
            @Valid @RequestBody ChatNotificationRequest request) {
        notificationService.sendChatNotification(request);
        return ResponseEntity.ok(new ApiResponse(true, "Chat notification sent"));
    }

    /**
     * Simple health check for gateway / k8s.
     */
    @GetMapping("/health-check")
    public String health() {
        return "Notification service is running ✅";
    }

    /**
     * Manual test endpoint to verify SMTP configuration.
     * Example:
     *   GET http://localhost:7003/api/notifications/test-email?to=jeldi.srikanth@gmail.com
     */
    @GetMapping("/test-email")
    public ResponseEntity<ApiResponse> sendTestEmail(@RequestParam("to") String to) {
        String subject = "CarVerse Test Email";
        String body = """
                <h2>CarVerse Notification Service</h2>
                <p>This is a test email from <b>notification-service</b>.</p>
                <p>If you received this, SMTP configuration is working correctly ✅</p>
                """;

        emailService.sendHtmlMail(to, subject, body);

        return ResponseEntity.ok(
                new ApiResponse(true, "Test email sent to " + to)
        );
    }
}

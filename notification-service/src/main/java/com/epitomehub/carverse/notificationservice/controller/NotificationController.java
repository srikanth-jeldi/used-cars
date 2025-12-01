package com.epitomehub.carverse.notificationservice.controller;

import com.epitomehub.carverse.notificationservice.dto.ApiResponse;
import com.epitomehub.carverse.notificationservice.dto.ChatNotificationRequest;
import com.epitomehub.carverse.notificationservice.dto.OtpNotificationRequest;
import com.epitomehub.carverse.notificationservice.service.NotificationService;
import com.epitomehub.carverse.notificationservice.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final EmailService emailService;

    public NotificationController(NotificationService notificationService,
                                  EmailService emailService) {
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    /**
     * Called by auth-service when user registers.
     * Sends OTP via email/SMS.
     */
    @PostMapping("/otp")
    public ResponseEntity<ApiResponse> sendOtp(@Valid @RequestBody OtpNotificationRequest request) {
        notificationService.sendOtpNotification(request);
        return ResponseEntity.ok(new ApiResponse(true, "OTP notification sent"));
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

package com.epitomehub.carverse.notificationservice.controller;

import com.epitomehub.carverse.notificationservice.dto.ApiResponse;
import com.epitomehub.carverse.notificationservice.dto.ChatNotificationRequest;
import com.epitomehub.carverse.notificationservice.dto.OtpNotificationRequest;
import com.epitomehub.carverse.notificationservice.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Called by auth-service when user registers
    @PostMapping("/otp")
    public ResponseEntity<ApiResponse> sendOtp(@Valid @RequestBody OtpNotificationRequest request) {
        notificationService.sendOtpNotification(request);
        return ResponseEntity.ok(new ApiResponse(true, "OTP notification sent"));
    }

    // Called by chat-service when a new message is sent
    @PostMapping("/chat-message")
    public ResponseEntity<ApiResponse> sendChatMessageNotification(
            @Valid @RequestBody ChatNotificationRequest request) {
        notificationService.sendChatNotification(request);
        return ResponseEntity.ok(new ApiResponse(true, "Chat notification sent"));
    }

    @GetMapping("/health-check")
    public String health() {
        return "Notification service is running ✅";
    }
}

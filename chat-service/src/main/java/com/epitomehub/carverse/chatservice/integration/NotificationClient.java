package com.epitomehub.carverse.chatservice.integration;

import com.epitomehub.carverse.chatservice.integration.dto.ChatNotificationRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class NotificationClient {

    private static final Logger log = LoggerFactory.getLogger(NotificationClient.class);

    private final RestTemplate restTemplate;

    @Value("${notification.base-url}")
    private String notificationBaseUrl;

    @Value("${notification.enabled:true}")
    private boolean enabled;

    public void sendChatNotification(ChatNotificationRequest request) {
        if (!enabled) return;

        String url = notificationBaseUrl + "/api/notifications/chat-message";
        try {
            restTemplate.postForEntity(url, request, String.class);
        } catch (Exception ex) {
            // do NOT fail chat message save
            log.warn("Notification service call failed: {}", ex.getMessage());
        }
    }
}

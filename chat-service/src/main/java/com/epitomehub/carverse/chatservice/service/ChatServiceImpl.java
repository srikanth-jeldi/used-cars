package com.epitomehub.carverse.chatservice.service;

import com.epitomehub.carverse.chatservice.dto.ChatMessageResponse;
import com.epitomehub.carverse.chatservice.dto.SendMessageRequest;
import com.epitomehub.carverse.chatservice.entity.ChatMessage;
import com.epitomehub.carverse.chatservice.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatServiceImpl.class);

    private final ChatMessageRepository repository;

    // Step 4: call notification-service after saving message
    private final RestTemplate restTemplate;

    // Step 4: notification-service base url (later move to application.yml)
    private static final String NOTIFICATION_URL = "http://localhost:7003/api/notifications/chat-message";

    @Override
    public ChatMessageResponse sendMessage(Long senderId, SendMessageRequest request) {

        ChatMessage message = ChatMessage.builder()
                .conversationId(request.getConversationId())
                .senderId(senderId)
                .receiverId(request.getReceiverId())
                .message(request.getMessage())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        ChatMessage saved = repository.save(message);

        // Step 4: Notify (do NOT fail chat if notification fails)
        sendChatEmailNotificationSafe(saved);

        return mapToResponse(saved);
    }

    @Override
    public List<ChatMessageResponse> getMessages(Long conversationId) {
        return repository.findByConversationIdOrderByCreatedAtAsc(conversationId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public int markConversationAsRead(Long receiverId, Long conversationId) {
        return repository.markConversationAsRead(conversationId, receiverId);
    }

    @Override
    public long getUnreadCount(Long receiverId) {
        return repository.countByReceiverIdAndIsReadFalse(receiverId);
    }

    private ChatMessageResponse mapToResponse(ChatMessage message) {
        return ChatMessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .message(message.getMessage())
                .isRead(message.isRead())
                .createdAt(message.getCreatedAt())
                .build();
    }

    /**
     * Step 4 helper: calls notification-service safely.
     * If notification-service is down or request fails, chat still succeeds.
     */
    private void sendChatEmailNotificationSafe(ChatMessage saved) {
        try {
            // Temporary placeholders (Step 4.2 will fetch from auth-service/user-service)
            ChatNotificationRequest payload = ChatNotificationRequest.builder()
                    .toEmail("receiver@example.com")          // TODO: fetch receiver email by receiverId
                    .toName("Receiver")                      // TODO: fetch receiver name
                    .toPhone(null)                           // optional
                    .fromName("Sender")                      // TODO: fetch sender name
                    .fromUserId(saved.getSenderId())
                    .messagePreview(saved.getMessage())
                    .carTitle(null)
                    .chatUrl(null)
                    .sendEmail(true)
                    .sendSms(false)
                    .build();

            restTemplate.postForEntity(NOTIFICATION_URL, payload, Object.class);
        } catch (Exception ex) {
            log.warn("Notification-service call failed (ignored): {}", ex.getMessage());
        }
    }
    @lombok.Data
    @lombok.Builder
    private static class ChatNotificationRequest {
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
}
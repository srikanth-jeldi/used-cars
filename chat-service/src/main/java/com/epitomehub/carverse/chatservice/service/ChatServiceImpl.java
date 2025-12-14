package com.epitomehub.carverse.chatservice.service;

import com.epitomehub.carverse.chatservice.dto.*;
import com.epitomehub.carverse.chatservice.entity.ChatMessage;
import com.epitomehub.carverse.chatservice.repository.ChatMessageRepository;
import com.epitomehub.carverse.chatservice.sse.SseHub;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final SseHub sseHub;

    // optional: if you already have restTemplate bean
    private final RestTemplate restTemplate;

    @Value("${notification.base-url:http://localhost:7003}")
    private String notificationBaseUrl;

    @Override
    public ChatMessageResponse sendMessage(Long senderId, SendMessageRequest request) {

        ChatMessage saved = chatMessageRepository.save(
                ChatMessage.builder()
                        .conversationId(request.conversationId())
                        .senderId(senderId)
                        .receiverId(request.receiverId())
                        .message(request.message())
                        .isRead(false)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        // Fire-and-forget: notify notification-service (do not break chat if fails)
        try {
            String url = notificationBaseUrl + "/api/notifications/chat-message";
            restTemplate.postForEntity(url, saved, Void.class);
        } catch (Exception ignored) {
            // keep silent or log warn in your logger
        }

        // SSE badge update for receiver
        publishUnreadBadge(saved.getConversationId(), request.receiverId());

        return toResponse(saved);
    }

    @Override
    public List<ChatMessageResponse> getMessages(Long conversationId) {
        return chatMessageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public int markConversationAsRead(Long receiverId, Long conversationId) {
        int updated = chatMessageRepository.markConversationAsRead(conversationId, receiverId);

        // SSE badge update for receiver
        publishUnreadBadge(conversationId, receiverId);

        return updated;
    }

    @Override
    public long getUnreadCount(Long receiverId) {
        return chatMessageRepository.countByReceiverIdAndIsReadFalse(receiverId);
    }

    @Override
    public List<UnreadByConversationResponse> getUnreadCountPerConversation(Long receiverId) {
        return chatMessageRepository.unreadCountsByConversation(receiverId)
                .stream()
                .map(v -> new UnreadByConversationResponse(v.getConversationId(), Long.parseLong(v.getUnreadCount().toString())))
                .toList();
    }

    @Override
    public List<InboxItemDto> getInbox(Long userId) {
        return chatMessageRepository.inbox(userId)
                .stream()
                .map(r -> new InboxItemDto(
                        r.getConversationId(),
                        r.getOtherUserId(),
                        r.getLastMessage(),
                        r.getLastMessageAt(),
                        r.getUnreadCount() == null ? 0L : r.getUnreadCount()
                ))
                .toList();
    }

    private void publishUnreadBadge(Long conversationId, Long userId) {
        long convUnread = chatMessageRepository.countByConversationIdAndReceiverIdAndIsReadFalse(conversationId, userId);
        long totalUnread = chatMessageRepository.countByReceiverIdAndIsReadFalse(userId);

        sseHub.publish(userId, "unread-badge",
                new UnreadBadgeEvent(conversationId, convUnread, totalUnread));
    }

    private ChatMessageResponse toResponse(ChatMessage m) {
        return new ChatMessageResponse(
                m.getId(),
                m.getConversationId(),
                m.getSenderId(),
                m.getReceiverId(),
                m.getMessage(),
                m.isRead(),
                m.getCreatedAt()
        );
    }
}

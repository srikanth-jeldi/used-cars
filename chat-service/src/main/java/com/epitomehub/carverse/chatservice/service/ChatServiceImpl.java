package com.epitomehub.carverse.chatservice.service;

import com.epitomehub.carverse.chatservice.dto.*;
import com.epitomehub.carverse.chatservice.entity.ChatMessage;
import com.epitomehub.carverse.chatservice.entity.Conversation;
import com.epitomehub.carverse.chatservice.repository.ChatMessageRepository;
import com.epitomehub.carverse.chatservice.repository.ConversationRepository;
import com.epitomehub.carverse.chatservice.sse.SseHub;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ConversationRepository conversationRepository;
    private final SseHub sseHub;
    private final RestTemplate restTemplate;

    // ✅ WebSocket live pushes
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${notification.base-url:http://localhost:7003}")
    private String notificationBaseUrl;

    @Override
    public ChatMessageResponse sendMessage(Long senderId, SendMessageRequest request) {

        Long receiverId = request.receiverId();
        Long conversationId = request.conversationId();

        if (receiverId == null) {
            throw new IllegalArgumentException("receiverId is required");
        }

        // If conversationId not provided -> create/find conversation by senderId + receiverId
        if (conversationId == null) {
            conversationId = createOrGetConversation(senderId, receiverId).getId();
        }

        // ✅ FIX: make it effectively final for lambda usage
        final Long cid = conversationId;

        // ✅ SECURITY CHECK for provided/derived conversationId
        Conversation conv = conversationRepository.findById(cid)
                .orElseThrow(() -> new RuntimeException("Conversation not found: " + cid));

        boolean isMember = senderId.equals(conv.getUser1Id()) || senderId.equals(conv.getUser2Id());
        if (!isMember) {
            throw new AccessDeniedException("Forbidden: not a member of conversation " + cid);
        }

        // Ensure receiverId is the OTHER participant (strict)
        Long otherUserId = senderId.equals(conv.getUser1Id()) ? conv.getUser2Id() : conv.getUser1Id();
        if (!otherUserId.equals(receiverId)) {
            throw new AccessDeniedException("Forbidden: receiverId does not match conversation other member");
        }

        ChatMessage saved = chatMessageRepository.save(
                ChatMessage.builder()
                        .conversationId(cid)
                        .senderId(senderId)
                        .receiverId(receiverId)
                        .message(request.message())
                        .isRead(false)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        ChatMessageResponse response = toResponse(saved);

        // ✅ WebSocket push (receiver live)
        messagingTemplate.convertAndSend("/topic/users/" + receiverId, response);

        // ✅ SSE push (receiver live)
        // (use your existing method name; if yours is publish(...) keep that)
        sseHub.publish(receiverId, "chat-message", response);

        // ✅ Optional: Notification service call (wrap to avoid breaking chat if it fails)
        try {
            // Example endpoint; adjust if you already have a DTO / API in notification-service
            // restTemplate.postForEntity(notificationBaseUrl + "/api/notifications/chat", response, Void.class);
        } catch (Exception ignored) {
            // do not fail chat message just because notification failed
        }

        return response;
    }

    @Override
    public List<ChatMessageResponse> getMessages(Long conversationId, Pageable pageable) {
        Page<ChatMessage> messages =
                chatMessageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId, pageable);
        return messages.stream().map(this::toResponse).toList();
    }

    @Override
    public int markConversationAsRead(Long receiverId, Long conversationId) {
        int updated = chatMessageRepository.markConversationAsRead(conversationId, receiverId);
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
                .map(v -> new UnreadByConversationResponse(v.getConversationId(), v.getUnreadCount()))
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
        long convUnread =
                chatMessageRepository.countByConversationIdAndReceiverIdAndIsReadFalse(conversationId, userId);
        long totalUnread =
                chatMessageRepository.countByReceiverIdAndIsReadFalse(userId);

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

    private Conversation createOrGetConversation(Long user1, Long user2) {
        long a = Math.min(user1, user2);
        long b = Math.max(user1, user2);

        Optional<Conversation> existing = conversationRepository.findByUsers(a, b);
        if (existing.isPresent()) {
            return existing.get();
        }

        LocalDateTime now = LocalDateTime.now();

        return conversationRepository.save(
                Conversation.builder()
                        .user1Id(a)
                        .user2Id(b)
                        .createdAt(now)
                        .updatedAt(now)
                        .build()
        );
    }
}

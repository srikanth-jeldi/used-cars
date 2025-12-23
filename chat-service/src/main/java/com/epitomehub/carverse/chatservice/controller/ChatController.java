package com.epitomehub.carverse.chatservice.controller;

import com.epitomehub.carverse.chatservice.dto.*;
import com.epitomehub.carverse.chatservice.entity.Conversation;
import com.epitomehub.carverse.chatservice.repository.ConversationRepository;
import com.epitomehub.carverse.chatservice.service.ChatService;
import com.epitomehub.carverse.chatservice.sse.SseHub;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SseHub sseHub;
    private final ConversationRepository conversationRepository;

    // -----------------------------
    // Messages
    // -----------------------------

    @PostMapping("/messages")
    public ChatMessageResponse sendMessage(Authentication authentication,
                                           @Valid @RequestBody SendMessageRequest request) {
        Long senderId = requireUserId(authentication);
        return chatService.sendMessage(senderId, request);
    }

    @GetMapping("/{conversationId}/messages")
    public List<ChatMessageResponse> getMessages(Authentication authentication,
                                                 @PathVariable Long conversationId,
                                                 Pageable pageable) {
        Long userId = requireUserId(authentication);
        requireConversationMember(userId, conversationId);
        return chatService.getMessages(conversationId, pageable);
    }

    @PatchMapping("/{conversationId}/read")
    public String markAsRead(Authentication authentication,
                             @PathVariable Long conversationId) {
        Long receiverId = requireUserId(authentication);
        requireConversationMember(receiverId, conversationId);
        int updated = chatService.markConversationAsRead(receiverId, conversationId);
        return "Marked as read. Updated rows: " + updated;
    }

    // -----------------------------
    // Unread counts + inbox
    // -----------------------------

    @GetMapping("/unread-count")
    public UnreadCountResponse unreadCount(Authentication authentication) {
        Long receiverId = requireUserId(authentication);
        long count = chatService.getUnreadCount(receiverId);
        return new UnreadCountResponse(receiverId, count);
    }

    @GetMapping("/unread-count/by-conversation")
    public List<UnreadByConversationResponse> unreadCountByConversation(Authentication authentication) {
        Long receiverId = requireUserId(authentication);
        return chatService.getUnreadCountPerConversation(receiverId);
    }

    @GetMapping("/inbox")
    public List<InboxItemDto> inbox(Authentication authentication) {
        Long userId = requireUserId(authentication);
        return chatService.getInbox(userId);
    }

    // -----------------------------
    // SSE subscribe
    // -----------------------------

    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(Authentication authentication) {
        Long userId = requireUserId(authentication);
        return sseHub.subscribe(userId);
    }

    // -----------------------------
    // Conversation create/get (by current user + otherUserId)
    // -----------------------------

    /**
     * UI should send: { "otherUserId": 15 }
     * Current user comes from JWT/principal.
     */
    @PostMapping("/conversation")
    public ResponseEntity<ConversationResponse> getOrCreateConversation(
            Authentication authentication,
            @Valid @RequestBody CreateConversationRequest request
    ) {
        Long me = requireUserId(authentication);
        Long other = request.getOtherUserId();

        if (other == null) {
            return ResponseEntity.badRequest().build();
        }

        long a = Math.min(me, other);
        long b = Math.max(me, other);

        Conversation conv = conversationRepository
                .findByUsers(a, b)
                .orElseGet(() -> {
                    LocalDateTime now = LocalDateTime.now();
                    Conversation c = new Conversation();
                    c.setUser1Id(a);
                    c.setUser2Id(b);
                    c.setCreatedAt(now);
                    c.setUpdatedAt(now);
                    return conversationRepository.save(c);
                });

        return ResponseEntity.ok(
                ConversationResponse.builder()
                        .id(conv.getId())
                        .user1Id(conv.getUser1Id())
                        .user2Id(conv.getUser2Id())
                        .build()
        );
    }

    // -----------------------------
    // Helpers
    // -----------------------------

    private Long requireUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new AccessDeniedException("Unauthorized");
        }
        if (!(authentication.getPrincipal() instanceof Long)) {
            throw new AccessDeniedException("Invalid principal");
        }
        return (Long) authentication.getPrincipal();
    }

    private Conversation requireConversationMember(Long userId, Long conversationId) {
        Conversation conv = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found: " + conversationId));

        boolean member = userId.equals(conv.getUser1Id()) || userId.equals(conv.getUser2Id());
        if (!member) {
            throw new AccessDeniedException("Forbidden: not a member of conversation " + conversationId);
        }
        return conv;
    }
}

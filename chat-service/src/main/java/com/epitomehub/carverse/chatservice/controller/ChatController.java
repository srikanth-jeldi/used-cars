package com.epitomehub.carverse.chatservice.controller;

import com.epitomehub.carverse.chatservice.dto.*;
import com.epitomehub.carverse.chatservice.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.epitomehub.carverse.chatservice.sse.SseHub;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SseHub sseHub;

    @PostMapping("/messages")
    public ChatMessageResponse sendMessage(Authentication authentication,
                                           @Valid @RequestBody SendMessageRequest request) {
        Long senderId = requireUserId(authentication);
        return chatService.sendMessage(senderId, request);
    }

    @GetMapping("/{conversationId}/messages")
    public List<ChatMessageResponse> getMessages(@PathVariable Long conversationId) {
        return chatService.getMessages(conversationId);
    }

    @PatchMapping("/{conversationId}/read")
    public String markAsRead(Authentication authentication,
                             @PathVariable Long conversationId) {
        Long receiverId = requireUserId(authentication);
        int updated = chatService.markConversationAsRead(receiverId, conversationId);
        return "Marked as read. Updated rows: " + updated;
    }

    // TOTAL unread (requires JWT)
    @GetMapping("/unread-count")
    public UnreadCountResponse unreadCount(Authentication authentication) {
        Long receiverId = requireUserId(authentication);
        long count = chatService.getUnreadCount(receiverId);
        return new UnreadCountResponse(receiverId, count);
    }

    // NEW: unread count per conversation (requires JWT)
    @GetMapping("/unread-count/by-conversation")
    public List<UnreadByConversationResponse> unreadCountByConversation(Authentication authentication) {
        Long receiverId = requireUserId(authentication);
        return chatService.getUnreadCountPerConversation(receiverId);
    }

    // NEW: inbox (lastMessage + unreadCount) (requires JWT)
    @GetMapping("/inbox")
    public List<InboxItemDto> inbox(Authentication authentication) {
        Long userId = requireUserId(authentication);
        return chatService.getInbox(userId);
    }

    // NEW: SSE stream for real-time badge updates (requires JWT in Postman; browser needs token/cookie strategy)
    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(Authentication authentication) {
        Long userId = requireUserId(authentication);
        return sseHub.subscribe(userId);
    }

    private Long requireUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new AccessDeniedException("Unauthorized");
        }
        if (!(authentication.getPrincipal() instanceof Long)) {
            throw new AccessDeniedException("Invalid principal");
        }
        return (Long) authentication.getPrincipal();
    }
}

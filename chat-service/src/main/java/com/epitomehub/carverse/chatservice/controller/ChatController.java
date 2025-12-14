package com.epitomehub.carverse.chatservice.controller;

import com.epitomehub.carverse.chatservice.dto.ChatMessageResponse;
import com.epitomehub.carverse.chatservice.dto.SendMessageRequest;
import com.epitomehub.carverse.chatservice.dto.UnreadCountResponse;
import com.epitomehub.carverse.chatservice.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // Step 5: senderId comes from JWT (Authentication principal)
    @PostMapping("/messages")
    public ChatMessageResponse sendMessage(
            Authentication authentication,
            @Valid @RequestBody SendMessageRequest request) {

        Long senderId = (Long) authentication.getPrincipal();
        return chatService.sendMessage(senderId, request);
    }

    @GetMapping("/{conversationId}/messages")
    public List<ChatMessageResponse> getMessages(@PathVariable Long conversationId) {
        return chatService.getMessages(conversationId);
    }

    // Step 5: receiverId comes from JWT (Authentication principal)
    @PatchMapping("/{conversationId}/read")
    public String markAsRead(
            Authentication authentication,
            @PathVariable Long conversationId) {

        Long receiverId = (Long) authentication.getPrincipal();
        int updated = chatService.markConversationAsRead(receiverId, conversationId);
        return "Marked as read. Updated rows: " + updated;
    }

    // Step 5: unread count uses JWT principal
    @GetMapping("/unread-count")
    public UnreadCountResponse unreadCount(Authentication authentication) {

        Long receiverId = (Long) authentication.getPrincipal();
        long count = chatService.getUnreadCount(receiverId);
        return new UnreadCountResponse(receiverId, count);
    }
}

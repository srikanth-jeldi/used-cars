package com.epitomehub.carverse.chatservice.service;

import com.epitomehub.carverse.chatservice.dto.ChatMessageResponse;
import com.epitomehub.carverse.chatservice.dto.SendMessageRequest;

import java.util.List;

public interface ChatService {

    ChatMessageResponse sendMessage(Long senderId, SendMessageRequest request);

    List<ChatMessageResponse> getMessages(Long conversationId);

    int markConversationAsRead(Long receiverId, Long conversationId);

    long getUnreadCount(Long receiverId);
}
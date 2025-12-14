package com.epitomehub.carverse.chatservice.service;

import com.epitomehub.carverse.chatservice.dto.*;

import java.util.List;

public interface ChatService {

    ChatMessageResponse sendMessage(Long senderId, SendMessageRequest request);

    List<ChatMessageResponse> getMessages(Long conversationId);

    int markConversationAsRead(Long receiverId, Long conversationId);

    long getUnreadCount(Long receiverId);

    List<UnreadByConversationResponse> getUnreadCountPerConversation(Long receiverId);

    List<InboxItemDto> getInbox(Long userId);
}

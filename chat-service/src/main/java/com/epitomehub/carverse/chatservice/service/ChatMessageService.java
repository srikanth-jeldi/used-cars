package com.epitomehub.carverse.chatservice.service;

import com.epitomehub.carverse.chatservice.entity.ChatMessage;
import com.epitomehub.carverse.chatservice.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatMessage sendMessage(ChatMessage message) {
        message.setRead(false);
        message.setCreatedAt(LocalDateTime.now());

        ChatMessage saved = chatMessageRepository.save(message);

        // ✅ Production simple: push to receiver topic
        messagingTemplate.convertAndSend(
                "/topic/users/" + saved.getReceiverId(),
                saved
        );

        // Optional: also push to sender (so sender UI updates too)
        messagingTemplate.convertAndSend(
                "/topic/users/" + saved.getSenderId(),
                saved
        );

        return saved;
    }
}

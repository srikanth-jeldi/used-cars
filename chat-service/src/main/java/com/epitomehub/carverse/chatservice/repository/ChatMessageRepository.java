package com.epitomehub.carverse.chatservice.repository;


import com.epitomehub.carverse.chatservice.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByConversationIdOrderByCreatedAtAsc(Long conversationId);

    long countByReceiverIdAndIsReadFalse(Long receiverId);

    List<ChatMessage> findByReceiverIdAndIsReadFalseOrderByCreatedAtAsc(Long receiverId);

    @Modifying
    @Transactional
    @Query("""
        update ChatMessage m
           set m.isRead = true
         where m.conversationId = :conversationId
           and m.receiverId = :receiverId
           and m.isRead = false
    """)
    int markConversationAsRead(Long conversationId, Long receiverId);
}
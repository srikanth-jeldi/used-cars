package com.epitomehub.carverse.chatservice.repository;

import com.epitomehub.carverse.chatservice.entity.ChatMessage;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Page<ChatMessage> findByConversationIdOrderByCreatedAtAsc(Long conversationId, Pageable pageable);

    Optional<ChatMessage> findTopByConversationIdOrderByCreatedAtDesc(Long conversationId);

    long countByReceiverIdAndIsReadFalse(Long receiverId);

    long countByConversationIdAndReceiverIdAndIsReadFalse(Long conversationId, Long receiverId);

    @Query("""
        SELECT m.conversationId AS conversationId, COUNT(m) AS unreadCount
        FROM ChatMessage m
        WHERE m.receiverId = :userId AND m.isRead = false
        GROUP BY m.conversationId
        """)
    List<UnreadCountView> unreadCountsByConversation(@Param("userId") Long userId);

    interface UnreadCountView {
        Long getConversationId();
        Long getUnreadCount();
    }

    @Modifying
    @Transactional
    @Query("""
        UPDATE ChatMessage m
        SET m.isRead = true
        WHERE m.conversationId = :conversationId
          AND m.receiverId = :receiverId
          AND m.isRead = false
        """)
    int markConversationAsRead(@Param("conversationId") Long conversationId,
                               @Param("receiverId") Long receiverId);

    @Query(value = """
        SELECT
            t.conversation_id AS conversationId,
            CASE WHEN t.sender_id = :userId THEN t.receiver_id ELSE t.sender_id END AS otherUserId,
            t.message AS lastMessage,
            t.created_at AS lastMessageAt,
            t.unreadCount AS unreadCount
        FROM (
            SELECT
                m.*,
                ROW_NUMBER() OVER (PARTITION BY m.conversation_id ORDER BY m.created_at DESC) AS rn,
                SUM(CASE WHEN m.receiver_id = :userId AND m.is_read = 0 THEN 1 ELSE 0 END)
                    OVER (PARTITION BY m.conversation_id) AS unreadCount
            FROM chat_messages m
            WHERE m.sender_id = :userId OR m.receiver_id = :userId
        ) t
        WHERE t.rn = 1
        ORDER BY t.created_at DESC
        """, nativeQuery = true)
    List<InboxRowView> inbox(@Param("userId") Long userId);

    interface InboxRowView {
        Long getConversationId();
        Long getOtherUserId();
        String getLastMessage();
        LocalDateTime getLastMessageAt();
        Long getUnreadCount();
    }
}
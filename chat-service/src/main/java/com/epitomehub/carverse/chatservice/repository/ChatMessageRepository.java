package com.epitomehub.carverse.chatservice.repository;

import com.epitomehub.carverse.chatservice.entity.ChatMessage;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // existing
    List<ChatMessage> findByConversationIdOrderByCreatedAtAsc(Long conversationId);

    long countByReceiverIdAndIsReadFalse(Long receiverId);

    // new: unread count for one conversation
    long countByConversationIdAndReceiverIdAndIsReadFalse(Long conversationId, Long receiverId);

    // new: last message of a conversation
    Optional<ChatMessage> findTopByConversationIdOrderByCreatedAtDesc(Long conversationId);

    // new: grouped unread counts per conversation (for badges)
    @Query("""
        select m.conversationId as conversationId, count(m) as unreadCount
        from ChatMessage m
        where m.receiverId = :userId and m.isRead = false
        group by m.conversationId
    """)
    List<UnreadCountView> unreadCountsByConversation(@Param("userId") Long userId);

    interface UnreadCountView {
        Long getConversationId();
        Long getUnreadCount();
    }

    @Modifying
    @Transactional
    @Query("""
        update ChatMessage m
           set m.isRead = true
         where m.conversationId = :conversationId
           and m.receiverId = :receiverId
           and m.isRead = false
    """)
    int markConversationAsRead(@Param("conversationId") Long conversationId,
                               @Param("receiverId") Long receiverId);

    // INBOX (MySQL 8 window functions) — no conversations table required
    @Query(value = """
        select
            t.conversation_id as conversationId,
            case when t.sender_id = :userId then t.receiver_id else t.sender_id end as otherUserId,
            t.message as lastMessage,
            t.created_at as lastMessageAt,
            t.unreadCount as unreadCount
        from (
            select
                m.*,
                row_number() over (partition by m.conversation_id order by m.created_at desc) as rn,
                sum(case when m.receiver_id = :userId and m.is_read = 0 then 1 else 0 end)
                    over (partition by m.conversation_id) as unreadCount
            from chat_messages m
            where m.sender_id = :userId or m.receiver_id = :userId
        ) t
        where t.rn = 1
        order by t.created_at desc
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

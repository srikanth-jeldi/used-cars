package com.epitomehub.carverse.chatservice.repository;

import com.epitomehub.carverse.chatservice.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    // Find conversation between two users (order-agnostic)
    @Query("SELECT c FROM Conversation c WHERE (c.user1Id = :user1 AND c.user2Id = :user2) OR (c.user1Id = :user2 AND c.user2Id = :user1)")
    Optional<Conversation> findByUsers(@Param("user1") Long user1, @Param("user2") Long user2);

}
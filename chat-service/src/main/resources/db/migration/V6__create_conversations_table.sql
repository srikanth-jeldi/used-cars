CREATE TABLE IF NOT EXISTS conversations (
                                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                             user1_id BIGINT NOT NULL,
                                             user2_id BIGINT NOT NULL,
                                             created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    UNIQUE KEY uk_conversations_users (user1_id, user2_id),
    INDEX idx_conversations_user1 (user1_id),
    INDEX idx_conversations_user2 (user2_id)
    );

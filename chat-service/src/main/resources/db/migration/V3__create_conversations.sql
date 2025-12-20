CREATE TABLE IF NOT EXISTS conversations (
                                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                             user1_id BIGINT NOT NULL,
                                             user2_id BIGINT NOT NULL,
                                             created_at DATETIME(6) NOT NULL,
    UNIQUE KEY uk_users (user1_id, user2_id)
    );

CREATE INDEX idx_conv_user1 ON conversations (user1_id);
CREATE INDEX idx_conv_user2 ON conversations (user2_id);
CREATE TABLE IF NOT EXISTS chat_messages (
                                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                             conversation_id BIGINT NOT NULL,
                                             sender_id BIGINT NOT NULL,
                                             receiver_id BIGINT NOT NULL,
                                             message VARCHAR(2000) NOT NULL,
    is_read BIT NOT NULL,
    created_at DATETIME(6) NOT NULL
    );

-- indexes (MySQL-compatible)
CREATE INDEX idx_chat_conv_created
    ON chat_messages (conversation_id, created_at);

CREATE INDEX idx_chat_receiver_read
    ON chat_messages (receiver_id, is_read);

CREATE INDEX idx_chat_receiver_conv_read
    ON chat_messages (receiver_id, conversation_id, is_read);

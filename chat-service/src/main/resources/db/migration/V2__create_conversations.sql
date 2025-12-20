CREATE TABLE IF NOT EXISTS chat_messages (
                                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                             conversation_id BIGINT NOT NULL,
                                             sender_id BIGINT NOT NULL,
                                             receiver_id BIGINT NOT NULL,
                                             message VARCHAR(2000) NOT NULL,
    is_read BIT NOT NULL,
    created_at DATETIME(6) NOT NULL
    );



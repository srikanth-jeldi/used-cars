CREATE TABLE IF NOT EXISTS otp_tokens (
                                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          otp_code VARCHAR(20) NOT NULL,
    identifier VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used TINYINT(1) NOT NULL DEFAULT 0,
    type VARCHAR(30) NOT NULL
    );

CREATE INDEX idx_otp_identifier_type_used
    ON otp_tokens(identifier, type, used);

CREATE INDEX idx_otp_expires_at
    ON otp_tokens(expires_at);

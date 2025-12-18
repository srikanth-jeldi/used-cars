CREATE TABLE IF NOT EXISTS otp_tokens (
                                          id BIGINT NOT NULL AUTO_INCREMENT,
                                          email VARCHAR(255) NOT NULL,
    otp VARCHAR(10) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BIT NOT NULL,
    PRIMARY KEY (id),
    KEY idx_otp_tokens_email (email)
    );
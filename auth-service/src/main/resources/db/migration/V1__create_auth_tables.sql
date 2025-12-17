-- =========================
-- AUTH SERVICE - V1 Schema
-- =========================

-- USERS
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                     full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled BIT NOT NULL,
    locked  BIT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

-- USER ROLES (matches entity column name: role)
CREATE TABLE IF NOT EXISTS user_roles (
                                          user_id BIGINT NOT NULL,
                                          role VARCHAR(50) NOT NULL,
    CONSTRAINT fk_user_roles_user
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON DELETE CASCADE
    );

-- OTP TOKENS (type is ENUM to match your entity mapping)
CREATE TABLE IF NOT EXISTS otp_tokens (
                                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                          otp_code VARCHAR(20) NOT NULL,
    identifier VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BIT NOT NULL,
    type ENUM('registration','forgot_password') NOT NULL
    );

-- =========================
-- INDEXES (MySQL syntax)
-- =========================

-- Users uniqueness
CREATE UNIQUE INDEX ux_users_email ON users(email);
CREATE UNIQUE INDEX ux_users_phone ON users(phone);

-- user_roles lookups
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);

-- otp_tokens lookups
CREATE INDEX idx_otp_identifier_type_used ON otp_tokens(identifier, type, used);
CREATE INDEX idx_otp_expires_at ON otp_tokens(expires_at);

-- Ensure conversations table has correct columns expected by JPA mapping:
-- user1_id, user2_id, created_at, updated_at

-- 1) If table does not exist, create it
CREATE TABLE IF NOT EXISTS conversations (
                                             id BIGINT NOT NULL AUTO_INCREMENT,
                                             user1_id BIGINT NOT NULL,
                                             user2_id BIGINT NOT NULL,
                                             created_at DATETIME NOT NULL,
                                             updated_at DATETIME NOT NULL,
                                             PRIMARY KEY (id),
    CONSTRAINT uk_conversation_users UNIQUE (user1_id, user2_id)
    ) ENGINE=InnoDB;

-- 2) If table exists but columns are wrong, try to add missing columns safely
-- (These statements will fail only if column already exists; that’s fine in manual runs,
-- but Flyway needs clean execution. So we keep it minimal and deterministic.)
--
-- IMPORTANT:
-- If your table currently has columns like user1id/user2id or user1Id/user2Id,
-- replace them using CHANGE COLUMN (uncomment the relevant block below).

/*
-- If current columns are user1id/user2id:
ALTER TABLE conversations
  CHANGE COLUMN user1id user1_id BIGINT NOT NULL,
  CHANGE COLUMN user2id user2_id BIGINT NOT NULL;
*/

/*
-- If current columns are user1Id/user2Id:
ALTER TABLE conversations
  CHANGE COLUMN user1Id user1_id BIGINT NOT NULL,
  CHANGE COLUMN user2Id user2_id BIGINT NOT NULL;
*/

-- Ensure timestamp columns exist (if they don't exist, add them)
-- Uncomment only if needed based on SHOW COLUMNS output.
/*
ALTER TABLE conversations
  ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
*/

-- V5__add_updated_at.sql
-- MySQL-safe: add column only if it does not exist (works even if column already exists)

SET @db := DATABASE();

SET @col_exists := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = @db
    AND TABLE_NAME = 'conversations'
    AND COLUMN_NAME = 'updated_at'
);

SET @sql := IF(
  @col_exists = 0,
  'ALTER TABLE conversations ADD COLUMN updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)',
  'SELECT 1'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add updated_at to stage_records if missing (idempotent: safe for DBs created with V1 that already include this column)
SET @col_exists = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'stage_records' AND COLUMN_NAME = 'updated_at');

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE stage_records ADD COLUMN updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP',
    'SELECT 1');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

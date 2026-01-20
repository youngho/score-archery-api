-- Arrow Master Game - 통합 MariaDB Schema (2026년 기준 최종 버전)
-- MariaDB 10.6+ 권장
-- 문자셋: utf8mb4 (이모지 지원)
-- 엔진: InnoDB (트랜잭션 지원)
-- 
-- 통합 내용:
-- - grok_schema의 우수한 설계 (DATETIME, BIGINT, CHECK 제약조건, 확장 가능한 인벤토리)
-- - claude_schema의 추가 기능 (user_profiles, achievements, challenges)
-- - 보안 강화: public_id 추가 (외부 노출용 base62 인코딩 ID, 내부는 BIGINT 사용)

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================================
-- 1. users (기본 사용자 정보)
-- ============================================================================
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    public_id CHAR(22) NOT NULL UNIQUE,          -- 외부 노출용 ID (base62 인코딩)
    nickname VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255),
    
    avatar_url VARCHAR(500),
    level INT DEFAULT 1 CHECK (level >= 1),
    experience_points BIGINT DEFAULT 0 CHECK (experience_points >= 0),
    
    coins BIGINT DEFAULT 0 CHECK (coins >= 0),
    gems BIGINT DEFAULT 0 CHECK (gems >= 0),
    
    account_type ENUM('guest', 'regular', 'premium') DEFAULT 'guest',
    is_guest TINYINT(1) DEFAULT 1,
    
    apple_id VARCHAR(255) UNIQUE,
    facebook_id VARCHAR(255) UNIQUE,
    google_id VARCHAR(255) UNIQUE,
    
    is_active TINYINT(1) DEFAULT 1,
    is_banned TINYINT(1) DEFAULT 0,
    ban_reason TEXT,
    ban_until DATETIME NULL,                -- 2038 문제 대비
    
    total_play_time INT DEFAULT 0 CHECK (total_play_time >= 0),  -- 총 플레이 시간 (초)
    last_login_at DATETIME NULL,
    login_count INT DEFAULT 0,
    
    settings JSON,                              -- 나머지 세부 설정
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME NULL,
    
    INDEX idx_users_public_id (public_id),
    INDEX idx_users_nickname (nickname),
    INDEX idx_users_social (apple_id, facebook_id, google_id),
    INDEX idx_users_status (is_active, is_banned)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================================
-- 2. user_profiles (사용자 프로필 확장 - claude_schema에서 추가)
-- ============================================================================
DROP TABLE IF EXISTS user_profiles;
CREATE TABLE user_profiles (
    profile_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    
    -- 프로필 상세
    bio TEXT,
    birth_date DATE,
    gender VARCHAR(20),
    country CHAR(2),
    preferred_language VARCHAR(10) DEFAULT 'ko',
    preferred_timezone VARCHAR(50) DEFAULT 'Asia/Seoul',
    
    -- 통계
    total_matches INT DEFAULT 0 CHECK (total_matches >= 0),
    total_wins INT DEFAULT 0 CHECK (total_wins >= 0),
    total_losses INT DEFAULT 0 CHECK (total_losses >= 0),
    win_rate DECIMAL(5,2) DEFAULT 0.00 CHECK (win_rate BETWEEN 0 AND 100),
    
    highest_score BIGINT DEFAULT 0 CHECK (highest_score >= 0),
    highest_combo INT DEFAULT 0 CHECK (highest_combo >= 0),
    total_arrows_fired BIGINT DEFAULT 0 CHECK (total_arrows_fired >= 0),
    total_bullseyes INT DEFAULT 0 CHECK (total_bullseyes >= 0),
    accuracy_rate DECIMAL(5,2) DEFAULT 0.00 CHECK (accuracy_rate BETWEEN 0 AND 100),
    
    -- 선호도
    favorite_arrow_type VARCHAR(50),
    favorite_stage_id INT,
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_profiles_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================================
-- 3. user_sessions (세션 관리)
-- ============================================================================
DROP TABLE IF EXISTS user_sessions;
CREATE TABLE user_sessions (
    session_id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    user_id BIGINT NOT NULL,
    
    access_token VARCHAR(500) NOT NULL,
    refresh_token VARCHAR(500),
    device_id VARCHAR(255),
    device_type VARCHAR(50),
    device_model VARCHAR(100),              -- claude_schema에서 추가
    os_version VARCHAR(50),                -- claude_schema에서 추가
    app_version VARCHAR(50),               -- claude_schema에서 추가
    
    ip_address VARCHAR(45),
    country_code CHAR(2),
    
    is_active TINYINT(1) DEFAULT 1,
    last_activity_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    expires_at DATETIME NOT NULL,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_sessions_user (user_id),
    INDEX idx_sessions_token (access_token(255)),
    INDEX idx_sessions_expires (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================================
-- 4. player_inventory_items (확장 가능한 인벤토리 - 행 기반)
-- ============================================================================
DROP TABLE IF EXISTS player_inventory_items;
CREATE TABLE player_inventory_items (
    inventory_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    item_type VARCHAR(100) NOT NULL,           -- fire_arrow, ice_arrow, premium_skin 등
    quantity BIGINT NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    
    acquired_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    expires_at DATETIME NULL,                  -- 기간 한정 아이템
    
    UNIQUE KEY uk_user_item (user_id, item_type),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    
    INDEX idx_inventory_user (user_id),
    INDEX idx_inventory_type (item_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================================
-- 5. player_progress (진행도)
-- ============================================================================
DROP TABLE IF EXISTS player_progress;
CREATE TABLE player_progress (
    progress_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    
    current_world INT DEFAULT 1 CHECK (current_world >= 1),
    current_stage INT DEFAULT 1 CHECK (current_stage >= 1),
    max_unlocked_world INT DEFAULT 1,
    max_unlocked_stage INT DEFAULT 1,
    
    total_stages_completed INT DEFAULT 0,
    total_stars_earned INT DEFAULT 0,
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_progress_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================================
-- 6. stage_records (스테이지 최고 기록)
-- ============================================================================
DROP TABLE IF EXISTS stage_records;
CREATE TABLE stage_records (
    record_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    
    world_number INT NOT NULL,
    stage_number INT NOT NULL,
    difficulty ENUM('easy','normal','hard','expert') DEFAULT 'normal',
    
    stars_earned TINYINT DEFAULT 0 CHECK (stars_earned BETWEEN 0 AND 3),
    high_score BIGINT DEFAULT 0,
    best_time_seconds INT,
    best_accuracy DECIMAL(5,2) DEFAULT 0.00 CHECK (best_accuracy BETWEEN 0 AND 100),
    best_combo INT DEFAULT 0,
    
    total_attempts INT DEFAULT 0,
    total_completions INT DEFAULT 0,
    first_clear_at DATETIME NULL,
    last_played_at DATETIME NULL,
    
    UNIQUE KEY uk_stage_record (user_id, world_number, stage_number, difficulty),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    
    INDEX idx_stage_user (user_id),
    INDEX idx_stage_info (world_number, stage_number, difficulty),
    INDEX idx_stage_score (high_score DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================================
-- 7. match_history (상세 플레이 기록 - 대용량 예상, 파티셔닝 적용)
-- ============================================================================
DROP TABLE IF EXISTS match_history;
CREATE TABLE match_history (
    match_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    public_id CHAR(22) NOT NULL UNIQUE,         -- 외부 노출용 ID (base62 인코딩)
    user_id BIGINT NOT NULL,
    
    world_number INT NOT NULL,
    stage_number INT NOT NULL,
    difficulty ENUM('easy','normal','hard','expert') DEFAULT 'normal',
    
    is_completed TINYINT(1) DEFAULT 0,
    stars_earned TINYINT DEFAULT 0 CHECK (stars_earned BETWEEN 0 AND 3),
    final_score BIGINT DEFAULT 0,
    
    -- 상세 통계 (claude_schema에서 추가)
    total_arrows_used INT DEFAULT 0 CHECK (total_arrows_used >= 0),
    arrows_hit INT DEFAULT 0 CHECK (arrows_hit >= 0),
    arrows_missed INT DEFAULT 0 CHECK (arrows_missed >= 0),
    accuracy DECIMAL(5,2) DEFAULT 0.00 CHECK (accuracy BETWEEN 0 AND 100),
    
    bullseyes INT DEFAULT 0 CHECK (bullseyes >= 0),
    perfects INT DEFAULT 0 CHECK (perfects >= 0),
    max_combo INT DEFAULT 0 CHECK (max_combo >= 0),
    
    -- 사용한 특수 화살 (claude_schema에서 추가)
    special_arrows_used JSON,
    
    play_duration_seconds INT CHECK (play_duration_seconds >= 0),
    started_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    completed_at DATETIME NULL,
    
    INDEX idx_match_public_id (public_id),
    INDEX idx_match_user_time (user_id, started_at DESC),
    INDEX idx_match_stage (world_number, stage_number, difficulty),
    INDEX idx_match_completed (completed_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================================
-- 8. player_achievements (업적 시스템 - claude_schema에서 추가)
-- ============================================================================
DROP TABLE IF EXISTS player_achievements;
CREATE TABLE player_achievements (
    achievement_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    public_id CHAR(22) NOT NULL UNIQUE,         -- 외부 노출용 ID (base62 인코딩)
    user_id BIGINT NOT NULL,
    achievement_type VARCHAR(100) NOT NULL,
    
    -- 진행도
    current_progress INT DEFAULT 0 CHECK (current_progress >= 0),
    required_progress INT NOT NULL CHECK (required_progress > 0),
    is_completed TINYINT(1) DEFAULT 0,
    
    -- 보상
    reward_coins INT DEFAULT 0 CHECK (reward_coins >= 0),
    reward_gems INT DEFAULT 0 CHECK (reward_gems >= 0),
    reward_items JSON,
    is_claimed TINYINT(1) DEFAULT 0,
    
    -- 타임스탬프
    unlocked_at DATETIME NULL,
    claimed_at DATETIME NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_achievement (user_id, achievement_type),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    
    INDEX idx_achievements_public_id (public_id),
    INDEX idx_achievements_user (user_id),
    INDEX idx_achievements_type (achievement_type),
    INDEX idx_achievements_completed (is_completed)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================================
-- 9. daily_challenges (데일리 챌린지 - claude_schema에서 추가)
-- ============================================================================
DROP TABLE IF EXISTS daily_challenges;
CREATE TABLE daily_challenges (
    challenge_id INT AUTO_INCREMENT PRIMARY KEY,
    
    -- 챌린지 정보
    challenge_date DATE NOT NULL UNIQUE,
    challenge_type VARCHAR(50) NOT NULL,
    
    -- 목표
    target_value INT NOT NULL CHECK (target_value > 0),
    difficulty VARCHAR(20),
    
    -- 보상
    reward_coins INT DEFAULT 0 CHECK (reward_coins >= 0),
    reward_gems INT DEFAULT 0 CHECK (reward_gems >= 0),
    reward_items JSON,
    
    -- 설정
    config JSON,
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_challenges_date (challenge_date DESC),
    INDEX idx_challenges_type (challenge_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================================
-- 10. player_challenges (플레이어 챌린지 진행 - claude_schema에서 추가)
-- ============================================================================
DROP TABLE IF EXISTS player_challenges;
CREATE TABLE player_challenges (
    player_challenge_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    challenge_id INT NOT NULL,
    
    -- 진행도
    current_progress INT DEFAULT 0 CHECK (current_progress >= 0),
    is_completed TINYINT(1) DEFAULT 0,
    is_claimed TINYINT(1) DEFAULT 0,
    
    -- 타임스탬프
    started_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    completed_at DATETIME NULL,
    claimed_at DATETIME NULL,
    
    UNIQUE KEY uk_player_challenge (user_id, challenge_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (challenge_id) REFERENCES daily_challenges(challenge_id) ON DELETE CASCADE,
    
    INDEX idx_player_challenges_user (user_id),
    INDEX idx_player_challenges_challenge (challenge_id),
    INDEX idx_player_challenges_completed (is_completed)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================================
-- 11. leaderboards (동적 랭킹 계산용 - rank 컬럼 없음)
-- ============================================================================
DROP TABLE IF EXISTS leaderboards;
CREATE TABLE leaderboards (
    entry_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    board_type VARCHAR(50) NOT NULL,          -- global_weekly, world_1_best, friends 등
    board_key VARCHAR(100),                   -- 2026-01, season_3 등
    
    score BIGINT NOT NULL,
    metadata JSON,
    
    recorded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    expires_at DATETIME NULL,
    
    UNIQUE KEY uk_leaderboard_entry (board_type, board_key, user_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    
    INDEX idx_leaderboard_ranking (board_type, board_key, score DESC),
    INDEX idx_leaderboard_user (user_id, board_type),
    INDEX idx_leaderboard_expires (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================================
-- 12. shop_products (상점 상품)
-- ============================================================================
DROP TABLE IF EXISTS shop_products;
CREATE TABLE shop_products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_sku VARCHAR(100) NOT NULL UNIQUE,
    product_name VARCHAR(200) NOT NULL,
    description TEXT,
    category VARCHAR(50),
    
    price_usd DECIMAL(10,2),
    price_krw BIGINT,
    original_price_usd DECIMAL(10,2),
    original_price_krw BIGINT,
    discount_percent TINYINT DEFAULT 0 CHECK (discount_percent BETWEEN 0 AND 100),
    
    contents JSON NOT NULL,                    -- 지급 아이템 목록
    icon_url VARCHAR(500),
    banner_url VARCHAR(500),                   -- claude_schema에서 추가
    
    is_active TINYINT(1) DEFAULT 1,
    is_featured TINYINT(1) DEFAULT 0,
    is_limited TINYINT(1) DEFAULT 0,
    
    purchase_limit INT,
    available_from DATETIME NULL,
    available_until DATETIME NULL,
    
    display_order INT DEFAULT 0,
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_products_category (category),
    INDEX idx_products_active (is_active),
    INDEX idx_products_featured (is_featured)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================================
-- 13. purchase_transactions (결제 내역)
-- ============================================================================
DROP TABLE IF EXISTS purchase_transactions;
CREATE TABLE purchase_transactions (
    transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    public_id CHAR(22) NOT NULL UNIQUE,         -- 외부 노출용 ID (base62 인코딩)
    user_id BIGINT NOT NULL,
    product_id INT,
    
    transaction_type VARCHAR(50),
    store_type VARCHAR(20),                    -- apple, google, web 등
    store_transaction_id VARCHAR(255) UNIQUE,
    
    currency CHAR(3),
    amount DECIMAL(12,2),
    
    product_snapshot JSON,
    receipt_data TEXT,
    receipt_verified TINYINT(1) DEFAULT 0,
    verification_time DATETIME NULL,           -- claude_schema에서 추가
    
    status ENUM('pending','completed','failed','refunded') DEFAULT 'pending',
    error_message TEXT,
    
    items_granted TINYINT(1) DEFAULT 0,
    granted_at DATETIME NULL,
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES shop_products(product_id) ON DELETE SET NULL,
    
    INDEX idx_transactions_public_id (public_id),
    INDEX idx_transactions_user (user_id),
    INDEX idx_transactions_status (status),
    INDEX idx_transactions_store_id (store_transaction_id),
    INDEX idx_transactions_created (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================================
-- 14. currency_log (통화 변화 로그)
-- ============================================================================
DROP TABLE IF EXISTS currency_log;
CREATE TABLE currency_log (
    log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    
    currency_type ENUM('coins','gems') NOT NULL,
    amount BIGINT NOT NULL,                    -- 양수:획득, 음수:소비
    balance_after BIGINT NOT NULL,
    
    reason VARCHAR(50) NOT NULL,
    reference_type VARCHAR(50),
    reference_id BIGINT,
    
    description TEXT,
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    
    INDEX idx_currency_user (user_id),
    INDEX idx_currency_time (created_at DESC),
    INDEX idx_currency_reference (reference_type, reference_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================================
-- 15. friendships (친구 관계)
-- ============================================================================
DROP TABLE IF EXISTS friendships;
CREATE TABLE friendships (
    friendship_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    public_id CHAR(22) NOT NULL UNIQUE,         -- 외부 노출용 ID (base62 인코딩)
    user_id BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    
    status ENUM('pending','accepted','rejected','blocked') DEFAULT 'pending',
    
    requested_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    accepted_at DATETIME NULL,
    
    UNIQUE KEY uk_friendship (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CHECK (user_id != friend_id),
    
    INDEX idx_friends_public_id (public_id),
    INDEX idx_friends_user (user_id),
    INDEX idx_friends_friend (friend_id),
    INDEX idx_friends_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================================
-- 끝
-- ============================================================================
-- 실행 후 확인용:
-- SHOW TABLES;
-- SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE();

-- ============================================================================
-- public_id 사용 가이드
-- ============================================================================
-- 
-- 보안을 위해 내부 DB 키(BIGINT AUTO_INCREMENT)는 외부에 노출하지 않고,
-- public_id(CHAR(22) base62 인코딩)만 클라이언트/API에 노출합니다.
--
-- public_id가 추가된 테이블:
-- - users (가장 중요)
-- - match_history
-- - purchase_transactions
-- - friendships
-- - player_achievements
--
-- 애플리케이션 레벨에서 public_id 생성 방법:
-- 1. 랜덤 문자열 생성 (예: 22자리 base62)
-- 2. UNIQUE 제약조건으로 중복 체크
-- 3. 생성 실패 시 재시도
--
-- 예시 (Java):
--   String publicId = generateBase62Id(22);
--   // DB에 저장 시 UNIQUE 제약조건으로 중복 체크
--
-- 예시 (Python):
--   import secrets
--   import base64
--   public_id = base64.urlsafe_b64encode(secrets.token_bytes(16))[:22].decode()
--
-- base62 문자셋: 0-9, A-Z, a-z (총 62자)
-- 22자리 base62 = 약 2^131 가능한 조합 (충분히 안전)

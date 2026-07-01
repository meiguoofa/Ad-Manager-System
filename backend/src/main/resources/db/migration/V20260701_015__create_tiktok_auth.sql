-- TikTok OAuth 授权记录:保存回调原始参数(auth_code/state)与换取的 token 结果,便于溯源。
CREATE TABLE IF NOT EXISTS tiktok_auth (
    id                   VARCHAR(36) PRIMARY KEY,
    app_id               VARCHAR(64)  NOT NULL,
    auth_code            VARCHAR(512),
    state                VARCHAR(255),
    callback_received_at DATETIME(3),
    advertiser_ids       TEXT,
    access_token         VARCHAR(512),
    refresh_token        VARCHAR(512),
    scope                VARCHAR(255),
    exchange_status      VARCHAR(20) NOT NULL DEFAULT 'pending',
    raw_response         TEXT,
    created_at           DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at           DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    KEY idx_tiktok_auth_status (exchange_status),
    KEY idx_tiktok_auth_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

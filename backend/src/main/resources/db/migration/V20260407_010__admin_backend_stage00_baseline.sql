-- Stage 00 baseline for the admin RBAC backend.
-- This migration only prepares schema/contracts; it does not create real admin accounts.

CREATE TABLE IF NOT EXISTS admin_users (
    id                 VARCHAR(36) PRIMARY KEY,
    username           VARCHAR(64) NOT NULL,
    password_hash      VARCHAR(100) NOT NULL,
    display_name       VARCHAR(100) NOT NULL,
    email              VARCHAR(255),
    status             VARCHAR(20) NOT NULL DEFAULT 'pending_init',
    created_at         DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at         DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    last_login_at      DATETIME(3),
    login_fail_count   INT NOT NULL DEFAULT 0,
    locked_until       DATETIME(3),
    UNIQUE KEY uk_admin_users_username (username),
    KEY idx_admin_users_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS admin_roles (
    id            VARCHAR(36) PRIMARY KEY,
    role_code     VARCHAR(50) NOT NULL,
    display_name  VARCHAR(100) NOT NULL,
    description   VARCHAR(255),
    created_at    DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at    DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_admin_roles_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS admin_user_roles (
    user_id     VARCHAR(36) NOT NULL,
    role_id     VARCHAR(36) NOT NULL,
    granted_by  VARCHAR(36),
    granted_at  DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    PRIMARY KEY (user_id, role_id),
    KEY idx_admin_user_roles_role_id (role_id),
    KEY idx_admin_user_roles_granted_by (granted_by),
    CONSTRAINT fk_admin_user_roles_user
        FOREIGN KEY (user_id) REFERENCES admin_users(id),
    CONSTRAINT fk_admin_user_roles_role
        FOREIGN KEY (role_id) REFERENCES admin_roles(id),
    CONSTRAINT fk_admin_user_roles_granted_by
        FOREIGN KEY (granted_by) REFERENCES admin_users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS admin_sessions (
    id          VARCHAR(36) PRIMARY KEY,
    user_id     VARCHAR(36) NOT NULL,
    token_hash  VARCHAR(128) NOT NULL,
    ip          VARCHAR(45),
    user_agent  VARCHAR(512),
    created_at  DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    expires_at  DATETIME(3) NOT NULL,
    revoked_at  DATETIME(3),
    UNIQUE KEY uk_admin_sessions_token_hash (token_hash),
    KEY idx_admin_sessions_user_id (user_id),
    KEY idx_admin_sessions_expires_at (expires_at),
    CONSTRAINT fk_admin_sessions_user
        FOREIGN KEY (user_id) REFERENCES admin_users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS admin_audit_logs (
    id              VARCHAR(36) PRIMARY KEY,
    operator_id     VARCHAR(36),
    operator_name   VARCHAR(100),
    role_code       VARCHAR(50),
    action          VARCHAR(100) NOT NULL,
    resource_type   VARCHAR(100) NOT NULL,
    resource_id     VARCHAR(100),
    result          VARCHAR(20) NOT NULL,
    ip              VARCHAR(45),
    user_agent      VARCHAR(512),
    request_id      VARCHAR(100),
    error_message   TEXT,
    occurred_at     DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    KEY idx_admin_audit_logs_occurred_at (occurred_at),
    KEY idx_admin_audit_logs_operator_time (operator_id, occurred_at),
    KEY idx_admin_audit_logs_resource (resource_type, resource_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

package com.admanager.admin.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminSession {
    private String id;
    private String userId;
    private String tokenHash;
    private String ip;
    private String userAgent;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime revokedAt;
}

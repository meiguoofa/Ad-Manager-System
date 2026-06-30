package com.admanager.admin.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminUser {
    private String id;
    private String username;
    private String passwordHash;
    private String displayName;
    private String email;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
    private Integer loginFailCount;
    private LocalDateTime lockedUntil;
}

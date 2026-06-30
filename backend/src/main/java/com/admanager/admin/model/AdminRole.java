package com.admanager.admin.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminRole {
    private String id;
    private String roleCode;
    private String displayName;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

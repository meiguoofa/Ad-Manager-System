package com.admanager.admin.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminAuditLog {
    private String id;
    private String operatorId;
    private String operatorName;
    private String roleCode;
    private String action;
    private String resourceType;
    private String resourceId;
    private String result;
    private String ip;
    private String userAgent;
    private String requestId;
    private String errorMessage;
    private LocalDateTime occurredAt;
}

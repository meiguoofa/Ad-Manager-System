package com.admanager.admin.dto;

import java.time.LocalDateTime;

public record AuditLogItem(
    String id,
    String operatorId,
    String operatorName,
    String roleCode,
    String action,
    String resourceType,
    String resourceId,
    String result,
    String ip,
    String userAgent,
    String requestId,
    String errorMessage,
    LocalDateTime occurredAt
) {
}

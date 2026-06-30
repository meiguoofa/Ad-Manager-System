package com.admanager.admin.dto;

import java.time.LocalDateTime;
import java.util.List;

public record AdminProfileResponse(
    String id,
    String username,
    String displayName,
    String email,
    String status,
    List<String> roles,
    LocalDateTime lastLoginAt
) {
}

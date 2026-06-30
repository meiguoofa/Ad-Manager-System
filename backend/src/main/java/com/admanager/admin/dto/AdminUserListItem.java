package com.admanager.admin.dto;

import java.time.LocalDateTime;
import java.util.List;

public record AdminUserListItem(
    String id,
    String username,
    String displayName,
    String email,
    String status,
    LocalDateTime lastLoginAt,
    List<String> roles
) {
}

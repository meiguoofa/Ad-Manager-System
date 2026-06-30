package com.admanager.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminUserStatusUpdateRequest(
    @NotBlank String status
) {
}

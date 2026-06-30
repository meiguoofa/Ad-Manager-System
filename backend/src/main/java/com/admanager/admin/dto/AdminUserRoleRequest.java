package com.admanager.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record AdminUserRoleRequest(
    @NotBlank String roleCode
) {
}

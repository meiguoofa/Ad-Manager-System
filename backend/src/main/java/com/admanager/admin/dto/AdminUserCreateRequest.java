package com.admanager.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminUserCreateRequest(
    @NotBlank @Size(max = 64) String username,
    @NotBlank @Size(min = 8, max = 128) String password,
    @NotBlank @Size(max = 100) String displayName,
    @Size(max = 255) String email
) {
}

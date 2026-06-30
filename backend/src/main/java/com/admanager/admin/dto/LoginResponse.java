package com.admanager.admin.dto;

import java.time.LocalDateTime;

public record LoginResponse(
    String accessToken,
    String refreshToken,
    LocalDateTime accessTokenExpiresAt,
    LocalDateTime refreshTokenExpiresAt,
    AdminProfileResponse user
) {
}

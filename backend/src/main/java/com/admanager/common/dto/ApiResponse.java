package com.admanager.common.dto;

import java.time.OffsetDateTime;

/**
 * Standard API response wrapper for the formal admin backend contract.
 */
public record ApiResponse<T>(
    int code,
    String message,
    T data,
    String timestamp
) {

    public static <T> ApiResponse<T> success(T data) {
        return of(0, "success", data);
    }

    public static <T> ApiResponse<T> of(int code, String message, T data) {
        return new ApiResponse<>(code, message, data, OffsetDateTime.now().toString());
    }
}

package com.admanager.common.dto;

import java.util.List;

/**
 * Page response shape shared by formal admin backend list APIs.
 */
public record PageResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements
) {

    public static <T> PageResponse<T> of(List<T> content, int page, int size, long totalElements) {
        return new PageResponse<>(content, page, size, totalElements);
    }
}

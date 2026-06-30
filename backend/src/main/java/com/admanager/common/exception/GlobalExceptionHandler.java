package com.admanager.common.exception;

import com.admanager.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatus(ResponseStatusException ex, HttpServletRequest request) {
        log.warn("Business exception: {}", ex.getReason());
        if (isAdminApi(request)) {
            return ResponseEntity.status(ex.getStatusCode())
                .body(ApiResponse.of(ex.getStatusCode().value(), ex.getReason(), null));
        }
        return buildLegacyResponse(ex.getStatusCode().value(), ex.getReason());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .reduce((a, b) -> a + "; " + b)
            .orElse("参数校验失败");

        log.warn("Validation failed: {}", message);
        if (isAdminApi(request)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.of(HttpStatus.BAD_REQUEST.value(), message, null));
        }
        return buildLegacyResponse(HttpStatus.BAD_REQUEST.value(), message);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied: {}", ex.getMessage());
        if (isAdminApi(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.of(HttpStatus.FORBIDDEN.value(), "权限不足", null));
        }
        return buildLegacyResponse(HttpStatus.FORBIDDEN.value(), "权限不足");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Illegal argument: {}", ex.getMessage());
        if (isAdminApi(request)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), null));
        }
        return buildLegacyResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request) {
        log.warn("No resource found: {}", ex.getResourcePath());
        String message = "资源不存在";
        if (isAdminApi(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.of(HttpStatus.NOT_FOUND.value(), message, null));
        }
        return buildLegacyResponse(HttpStatus.NOT_FOUND.value(), message);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        log.warn("Method not supported: {} {}", request.getMethod(), request.getRequestURI());
        String message = "请求方法不支持";
        if (isAdminApi(request)) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.of(HttpStatus.METHOD_NOT_ALLOWED.value(), message, null));
        }
        return buildLegacyResponse(HttpStatus.METHOD_NOT_ALLOWED.value(), message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneral(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception", ex);
        String message = "服务器内部错误";
        if (isAdminApi(request)) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), message, null));
        }
        return buildLegacyResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
    }

    private boolean isAdminApi(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri != null && uri.startsWith("/api/v1/admin/");
    }

    private ResponseEntity<Map<String, Object>> buildLegacyResponse(int status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status);
        body.put("message", message);
        body.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(status).body(body);
    }
}

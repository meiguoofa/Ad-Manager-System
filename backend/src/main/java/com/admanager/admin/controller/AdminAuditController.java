package com.admanager.admin.controller;

import com.admanager.admin.dto.AuditLogItem;
import com.admanager.admin.dto.AuditFilterOptionsResponse;
import com.admanager.admin.service.AdminAuditService;
import com.admanager.common.dto.ApiResponse;
import com.admanager.common.dto.PageResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/admin/audit")
public class AdminAuditController {

    private final AdminAuditService adminAuditService;

    public AdminAuditController(AdminAuditService adminAuditService) {
        this.adminAuditService = adminAuditService;
    }

    @PreAuthorize("hasAnyRole('PRIMARY_SUPER_ADMIN','SUPER_ADMIN','AUDITOR')")
    @GetMapping("/logs")
    public ApiResponse<PageResponse<AuditLogItem>> logs(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(required = false) String action,
        @RequestParam(required = false) String operatorId,
        @RequestParam(required = false) String resourceType,
        @RequestParam(required = false) String resourceId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return ApiResponse.success(
            adminAuditService.list(page, size, action, operatorId, resourceType, resourceId, from, to)
        );
    }

    @PreAuthorize("hasAnyRole('PRIMARY_SUPER_ADMIN','SUPER_ADMIN','AUDITOR')")
    @GetMapping("/filter-options")
    public ApiResponse<AuditFilterOptionsResponse> filterOptions(
        @RequestParam(required = false) String operatorKeyword,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
        @RequestParam(required = false) Integer limit
    ) {
        return ApiResponse.success(adminAuditService.listFilterOptions(operatorKeyword, from, to, limit));
    }
}

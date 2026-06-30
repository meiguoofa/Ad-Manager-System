package com.admanager.admin.controller;

import com.admanager.admin.dto.AdminUserCreateRequest;
import com.admanager.admin.dto.AdminUserListItem;
import com.admanager.admin.dto.AdminResetPasswordRequest;
import com.admanager.admin.dto.AdminUserRoleRequest;
import com.admanager.admin.dto.AdminUserStatusUpdateRequest;
import com.admanager.admin.dto.RoleResponse;
import com.admanager.admin.security.AdminPrincipal;
import com.admanager.admin.service.AdminAuthService;
import com.admanager.admin.service.AdminConstants;
import com.admanager.admin.service.AdminUserService;
import com.admanager.common.dto.ApiResponse;
import com.admanager.common.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final AdminAuthService authService;

    public AdminUserController(AdminUserService adminUserService, AdminAuthService authService) {
        this.adminUserService = adminUserService;
        this.authService = authService;
    }

    @PreAuthorize("hasAnyRole('PRIMARY_SUPER_ADMIN','SUPER_ADMIN')")
    @GetMapping("/roles")
    public ApiResponse<List<RoleResponse>> roles() {
        return ApiResponse.success(adminUserService.listRoles());
    }

    @PreAuthorize("hasAnyRole('PRIMARY_SUPER_ADMIN','SUPER_ADMIN')")
    @GetMapping("/admin-users")
    public ApiResponse<PageResponse<AdminUserListItem>> listAdminUsers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String status
    ) {
        return ApiResponse.success(adminUserService.list(page, size, keyword, status));
    }

    @PreAuthorize("hasAnyRole('PRIMARY_SUPER_ADMIN','SUPER_ADMIN')")
    @PostMapping("/admin-users")
    public ApiResponse<AdminUserListItem> createAdminUser(@Valid @RequestBody AdminUserCreateRequest request) {
        AdminPrincipal principal = authService.requirePrincipal();
        return ApiResponse.success(adminUserService.create(
            request,
            principal.userId(),
            principal.username(),
            resolveOperatorRole(principal.roles())
        ));
    }

    @PreAuthorize("hasAnyRole('PRIMARY_SUPER_ADMIN','SUPER_ADMIN')")
    @PostMapping("/admin-users/{id}/status")
    public ApiResponse<AdminUserListItem> updateStatus(@PathVariable("id") String userId,
                                                       @Valid @RequestBody AdminUserStatusUpdateRequest request) {
        AdminPrincipal principal = authService.requirePrincipal();
        return ApiResponse.success(adminUserService.updateStatus(
            userId,
            request.status(),
            principal.userId(),
            principal.username(),
            principal.roles(),
            resolveOperatorRole(principal.roles())
        ));
    }

    @PreAuthorize("hasAnyRole('PRIMARY_SUPER_ADMIN','SUPER_ADMIN')")
    @PostMapping("/admin-users/{id}/roles/grant")
    public ApiResponse<Void> grantRole(@PathVariable("id") String userId,
                                       @Valid @RequestBody AdminUserRoleRequest request) {
        AdminPrincipal principal = authService.requirePrincipal();
        adminUserService.grantRole(
            userId,
            request.roleCode(),
            principal.userId(),
            principal.username(),
            principal.roles(),
            resolveOperatorRole(principal.roles())
        );
        return ApiResponse.success(null);
    }

    @PreAuthorize("hasAnyRole('PRIMARY_SUPER_ADMIN','SUPER_ADMIN')")
    @PostMapping("/admin-users/{id}/roles/revoke")
    public ApiResponse<Void> revokeRole(@PathVariable("id") String userId,
                                        @Valid @RequestBody AdminUserRoleRequest request) {
        AdminPrincipal principal = authService.requirePrincipal();
        adminUserService.revokeRole(
            userId,
            request.roleCode(),
            principal.userId(),
            principal.username(),
            principal.roles(),
            resolveOperatorRole(principal.roles())
        );
        return ApiResponse.success(null);
    }

    @PreAuthorize("hasAnyRole('PRIMARY_SUPER_ADMIN','SUPER_ADMIN')")
    @PostMapping("/admin-users/{id}/password/reset")
    public ApiResponse<Void> resetPassword(@PathVariable("id") String userId,
                                           @Valid @RequestBody AdminResetPasswordRequest request) {
        AdminPrincipal principal = authService.requirePrincipal();
        adminUserService.resetPassword(
            userId,
            request.newPassword(),
            principal.userId(),
            principal.username(),
            principal.roles(),
            resolveOperatorRole(principal.roles())
        );
        return ApiResponse.success(null);
    }

    private String resolveOperatorRole(List<String> roles) {
        if (roles.contains(AdminConstants.ROLE_PRIMARY_SUPER_ADMIN)) {
            return AdminConstants.ROLE_PRIMARY_SUPER_ADMIN;
        }
        if (roles.contains(AdminConstants.ROLE_SUPER_ADMIN)) {
            return AdminConstants.ROLE_SUPER_ADMIN;
        }
        return roles.isEmpty() ? null : roles.get(0);
    }
}

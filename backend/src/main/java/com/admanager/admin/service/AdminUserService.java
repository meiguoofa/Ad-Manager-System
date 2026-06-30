package com.admanager.admin.service;

import com.admanager.admin.dto.AdminUserCreateRequest;
import com.admanager.admin.dto.AdminUserListItem;
import com.admanager.admin.dto.RoleResponse;
import com.admanager.admin.mapper.AdminRoleMapper;
import com.admanager.admin.mapper.AdminSessionMapper;
import com.admanager.admin.mapper.AdminUserMapper;
import com.admanager.admin.mapper.AdminUserRoleMapper;
import com.admanager.admin.model.AdminRole;
import com.admanager.admin.model.AdminUser;
import com.admanager.common.dto.PageResponse;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class AdminUserService {

    private final AdminUserMapper adminUserMapper;
    private final AdminRoleMapper adminRoleMapper;
    private final AdminSessionMapper adminSessionMapper;
    private final AdminUserRoleMapper adminUserRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final AdminAuditService auditService;

    public AdminUserService(AdminUserMapper adminUserMapper,
                            AdminRoleMapper adminRoleMapper,
                            AdminSessionMapper adminSessionMapper,
                            AdminUserRoleMapper adminUserRoleMapper,
                            PasswordEncoder passwordEncoder,
                            AdminAuditService auditService) {
        this.adminUserMapper = adminUserMapper;
        this.adminRoleMapper = adminRoleMapper;
        this.adminSessionMapper = adminSessionMapper;
        this.adminUserRoleMapper = adminUserRoleMapper;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminUserListItem> list(int page, int size, String keyword, String status) {
        int offset = page * size;
        String normalizedStatus = status == null ? null : status.trim().toLowerCase(Locale.ROOT);
        List<AdminUserListItem> content = adminUserMapper.selectPaged(offset, size, keyword, normalizedStatus)
            .stream()
            .map(user -> new AdminUserListItem(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getEmail(),
                user.getStatus(),
                user.getLastLoginAt(),
                adminUserRoleMapper.selectRoleCodesByUserId(user.getId())
            ))
            .toList();
        long total = adminUserMapper.countPaged(keyword, normalizedStatus);
        return PageResponse.of(content, page, size, total);
    }

    @Transactional
    public AdminUserListItem create(AdminUserCreateRequest request, String operatorId, String operatorName, String roleCode) {
        AdminUser user = new AdminUser();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(request.username().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setDisplayName(request.displayName().trim());
        user.setEmail(request.email());
        user.setStatus(AdminConstants.STATUS_PENDING_INIT);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setLoginFailCount(0);

        try {
            adminUserMapper.insert(user);
        } catch (DuplicateKeyException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "用户名已存在");
        }

        auditService.audit(operatorId, operatorName, roleCode,
            "admin_user.create", "admin_users", user.getId(), "success", null, null, null, null);

        return new AdminUserListItem(
            user.getId(),
            user.getUsername(),
            user.getDisplayName(),
            user.getEmail(),
            user.getStatus(),
            user.getLastLoginAt(),
            List.of()
        );
    }

    @Transactional
    public AdminUserListItem updateStatus(String userId,
                                          String targetStatus,
                                          String operatorId,
                                          String operatorName,
                                          List<String> operatorRoles,
                                          String operatorRoleCode) {
        AdminUser user = requireUser(userId);
        String normalizedTarget = targetStatus.trim().toLowerCase(Locale.ROOT);

        denyIfSelfModify(userId, operatorId, operatorName, operatorRoleCode, "status");

        List<String> targetRoles = adminUserRoleMapper.selectRoleCodesByUserId(userId);
        boolean targetIsSuperRelationUser = hasSuperRelationRole(targetRoles);
        if (targetIsSuperRelationUser && !isPrimarySuperAdmin(operatorRoles)) {
            denyByPolicy(
                operatorId, operatorName, operatorRoleCode, "authz.denied.super_admin_relation",
                "admin_users", userId, "仅主超管可修改超管账号状态"
            );
        }

        if (!isAllowedTransition(user.getStatus(), normalizedTarget)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "非法状态流转: %s -> %s".formatted(user.getStatus(), normalizedTarget));
        }

        if (targetRoles.contains(AdminConstants.ROLE_PRIMARY_SUPER_ADMIN)
            && (AdminConstants.STATUS_SUSPENDED.equals(normalizedTarget) || AdminConstants.STATUS_DISABLED.equals(normalizedTarget))) {
            long primaryCount = adminUserRoleMapper.countUsersByRoleCode(AdminConstants.ROLE_PRIMARY_SUPER_ADMIN);
            if (primaryCount <= 1) {
                denyByConflict(
                    operatorId, operatorName, operatorRoleCode, "authz.denied.primary_unique",
                    "admin_users", userId, "禁止冻结或禁用唯一主超管"
                );
            }
        }

        boolean statusChanged = !user.getStatus().equals(normalizedTarget);
        adminUserMapper.updateStatus(userId, normalizedTarget, LocalDateTime.now());
        if (statusChanged) {
            revokeActiveSessionsByUser(userId);
        }
        AdminUser updated = requireUser(userId);

        auditService.audit(operatorId, operatorName, operatorRoleCode,
            "admin_user.status", "admin_users", userId, "success", null, null, null,
            user.getStatus() + "->" + normalizedTarget);

        return new AdminUserListItem(
            updated.getId(),
            updated.getUsername(),
            updated.getDisplayName(),
            updated.getEmail(),
            updated.getStatus(),
            updated.getLastLoginAt(),
            adminUserRoleMapper.selectRoleCodesByUserId(updated.getId())
        );
    }

    @Transactional
    public void grantRole(String userId,
                          String roleCode,
                          String operatorId,
                          String operatorName,
                          List<String> operatorRoles,
                          String operatorRoleCode) {
        AdminUser user = requireUser(userId);
        AdminRole role = requireRole(roleCode);

        denyIfSelfModify(userId, operatorId, operatorName, operatorRoleCode, "role");

        String normalizedRoleCode = role.getRoleCode();
        List<String> targetRoles = adminUserRoleMapper.selectRoleCodesByUserId(userId);
        if (isSuperRelationRole(normalizedRoleCode) && !isPrimarySuperAdmin(operatorRoles)) {
            denyByPolicy(
                operatorId, operatorName, operatorRoleCode, "authz.denied.super_admin_relation",
                "admin_user_roles", userId, "仅主超管可授予超管关系角色"
            );
        }

        if (AdminConstants.ROLE_PRIMARY_SUPER_ADMIN.equals(normalizedRoleCode)
            && !targetRoles.contains(AdminConstants.ROLE_PRIMARY_SUPER_ADMIN)) {
            long primaryCount = adminUserRoleMapper.countUsersByRoleCode(AdminConstants.ROLE_PRIMARY_SUPER_ADMIN);
            if (primaryCount >= 1) {
                denyByConflict(
                    operatorId, operatorName, operatorRoleCode, "authz.denied.primary_unique",
                    "admin_user_roles", userId, "主超管全局唯一，禁止授予第二个主超管"
                );
            }
        }

        if (isBusinessRole(normalizedRoleCode) && targetRoles.contains(AdminConstants.ROLE_PRIMARY_SUPER_ADMIN)) {
            denyByConflict(
                operatorId, operatorName, operatorRoleCode, "authz.denied.primary_unique",
                "admin_user_roles", userId, "主超管账号不可授予业务角色"
            );
        }

        List<String> cleanedRoles = new ArrayList<>();
        if (isSuperRelationRole(normalizedRoleCode)) {
            cleanedRoles.addAll(removeBusinessRolesForUser(
                userId,
                operatorId,
                operatorName,
                operatorRoleCode,
                "grant " + normalizedRoleCode
            ));
        } else if (isBusinessRole(normalizedRoleCode) && targetRoles.contains(AdminConstants.ROLE_SUPER_ADMIN)) {
            if (removeRoleByCode(userId, AdminConstants.ROLE_SUPER_ADMIN) > 0) {
                cleanedRoles.add(AdminConstants.ROLE_SUPER_ADMIN);
                auditService.audit(
                    operatorId,
                    operatorName,
                    operatorRoleCode,
                    "admin_user.role_conflict_auto_cleanup",
                    "admin_user_roles",
                    userId,
                    "success",
                    null,
                    null,
                    null,
                    "auto cleanup roles=" + cleanedRoles + ", context=grant " + normalizedRoleCode
                );
            }
        }

        try {
            adminUserRoleMapper.insert(user.getId(), role.getId(), operatorId, LocalDateTime.now());
        } catch (DuplicateKeyException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "角色已授权");
        }

        revokeActiveSessionsByUser(userId);

        auditService.audit(operatorId, operatorName, operatorRoleCode,
            "admin_user.role_grant", "admin_user_roles", userId, "success", null, null, null,
            "grant " + role.getRoleCode());
    }

    @Transactional
    public void revokeRole(String userId,
                           String roleCode,
                           String operatorId,
                           String operatorName,
                           List<String> operatorRoles,
                           String operatorRoleCode) {
        AdminUser user = requireUser(userId);
        AdminRole role = requireRole(roleCode);

        denyIfSelfModify(userId, operatorId, operatorName, operatorRoleCode, "role");

        String normalizedRoleCode = role.getRoleCode();
        List<String> targetRoles = adminUserRoleMapper.selectRoleCodesByUserId(userId);
        if (isSuperRelationRole(normalizedRoleCode) && !isPrimarySuperAdmin(operatorRoles)) {
            denyByPolicy(
                operatorId, operatorName, operatorRoleCode, "authz.denied.super_admin_relation",
                "admin_user_roles", userId, "仅主超管可回收超管关系角色"
            );
        }

        if (AdminConstants.ROLE_PRIMARY_SUPER_ADMIN.equals(normalizedRoleCode)
            && targetRoles.contains(AdminConstants.ROLE_PRIMARY_SUPER_ADMIN)) {
            long primaryCount = adminUserRoleMapper.countUsersByRoleCode(AdminConstants.ROLE_PRIMARY_SUPER_ADMIN);
            if (primaryCount <= 1) {
                denyByConflict(
                    operatorId, operatorName, operatorRoleCode, "authz.denied.primary_unique",
                    "admin_user_roles", userId, "禁止回收唯一主超管角色"
                );
            }
        }

        int affected = adminUserRoleMapper.deleteByUserAndRole(user.getId(), role.getId());
        if (affected == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "角色未授权");
        }

        revokeActiveSessionsByUser(userId);

        auditService.audit(operatorId, operatorName, operatorRoleCode,
            "admin_user.role_revoke", "admin_user_roles", userId, "success", null, null, null,
            "revoke " + role.getRoleCode());
    }

    @Transactional(noRollbackFor = ResponseStatusException.class)
    public void resetPassword(String userId,
                              String newPassword,
                              String operatorId,
                              String operatorName,
                              List<String> operatorRoles,
                              String operatorRoleCode) {
        AdminUser user = requireUser(userId);

        if (operatorId != null && operatorId.equals(userId)) {
            auditService.audit(
                operatorId,
                operatorName,
                operatorRoleCode,
                "admin_user.password_reset",
                "admin_users",
                userId,
                "failed",
                null,
                null,
                null,
                "self_reset_forbidden"
            );
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "禁止通过重置接口修改自己的密码");
        }

        List<String> targetRoles = adminUserRoleMapper.selectRoleCodesByUserId(userId);
        if (hasSuperRelationRole(targetRoles) && !isPrimarySuperAdmin(operatorRoles)) {
            auditService.audit(
                operatorId,
                operatorName,
                operatorRoleCode,
                "admin_user.password_reset",
                "admin_users",
                userId,
                "failed",
                null,
                null,
                null,
                "super_relation_reset_forbidden"
            );
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "仅主超管可重置超管关系账号密码");
        }

        if (passwordEncoder.matches(newPassword, user.getPasswordHash())) {
            auditService.audit(
                operatorId,
                operatorName,
                operatorRoleCode,
                "admin_user.password_reset",
                "admin_users",
                userId,
                "failed",
                null,
                null,
                null,
                "new_password_same_as_old"
            );
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "新密码不能与旧密码相同");
        }

        adminUserMapper.updatePasswordHash(
            userId,
            passwordEncoder.encode(newPassword),
            LocalDateTime.now()
        );
        revokeActiveSessionsByUser(userId);

        auditService.audit(
            operatorId,
            operatorName,
            operatorRoleCode,
            "admin_user.password_reset",
            "admin_users",
            userId,
            "success",
            null,
            null,
            null,
            "reset_by=" + operatorId
        );
    }

    @Transactional(readOnly = true)
    public List<String> getRoleCodes(String userId) {
        return adminUserRoleMapper.selectRoleCodesByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> listRoles() {
        return adminRoleMapper.selectAll().stream()
            .map(r -> new RoleResponse(r.getRoleCode(), r.getDisplayName(), r.getDescription()))
            .toList();
    }

    private void denyIfSelfModify(String userId,
                                  String operatorId,
                                  String operatorName,
                                  String operatorRoleCode,
                                  String modifyType) {
        if (operatorId != null && operatorId.equals(userId)) {
            denyByPolicy(
                operatorId, operatorName, operatorRoleCode, "authz.denied.self_modify",
                "admin_users", userId, "不可修改自身" + modifyType
            );
        }
    }

    private void denyByPolicy(String operatorId,
                              String operatorName,
                              String operatorRoleCode,
                              String action,
                              String resourceType,
                              String resourceId,
                              String errorMessage) {
        auditService.audit(operatorId, operatorName, operatorRoleCode,
            action, resourceType, resourceId, "failed", null, null, null, errorMessage);
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, errorMessage);
    }

    private void denyByConflict(String operatorId,
                                String operatorName,
                                String operatorRoleCode,
                                String action,
                                String resourceType,
                                String resourceId,
                                String errorMessage) {
        auditService.audit(operatorId, operatorName, operatorRoleCode,
            action, resourceType, resourceId, "failed", null, null, null, errorMessage);
        throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
    }

    private AdminUser requireUser(String userId) {
        AdminUser user = adminUserMapper.selectById(userId);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "管理员不存在");
        }
        return user;
    }

    private AdminRole requireRole(String roleCode) {
        AdminRole role = adminRoleMapper.selectByRoleCode(roleCode);
        if (role == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "角色不存在");
        }
        return role;
    }

    private boolean isAllowedTransition(String current, String target) {
        if (current.equals(target)) {
            return true;
        }

        if (AdminConstants.STATUS_PENDING_INIT.equals(current)
            && AdminConstants.STATUS_ACTIVE.equals(target)) {
            return true;
        }

        if (AdminConstants.STATUS_ACTIVE.equals(current)
            && (AdminConstants.STATUS_SUSPENDED.equals(target) || AdminConstants.STATUS_DISABLED.equals(target))) {
            return true;
        }

        if (AdminConstants.STATUS_SUSPENDED.equals(current)
            && (AdminConstants.STATUS_ACTIVE.equals(target) || AdminConstants.STATUS_DISABLED.equals(target))) {
            return true;
        }

        return false;
    }

    private boolean isPrimarySuperAdmin(List<String> roles) {
        return roles != null && roles.contains(AdminConstants.ROLE_PRIMARY_SUPER_ADMIN);
    }

    private boolean hasSuperRelationRole(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return false;
        }
        return roles.contains(AdminConstants.ROLE_SUPER_ADMIN)
            || roles.contains(AdminConstants.ROLE_PRIMARY_SUPER_ADMIN);
    }

    private boolean isSuperRelationRole(String roleCode) {
        return AdminConstants.ROLE_SUPER_ADMIN.equals(roleCode)
            || AdminConstants.ROLE_PRIMARY_SUPER_ADMIN.equals(roleCode);
    }

    private boolean isBusinessRole(String roleCode) {
        return AdminConstants.ROLE_OPERATOR.equals(roleCode)
            || AdminConstants.ROLE_SALES.equals(roleCode)
            || AdminConstants.ROLE_AUDITOR.equals(roleCode);
    }

    private List<String> removeBusinessRolesForUser(String userId,
                                                    String operatorId,
                                                    String operatorName,
                                                    String operatorRoleCode,
                                                    String context) {
        List<String> removed = new ArrayList<>();
        for (String roleCode : List.of(AdminConstants.ROLE_OPERATOR, AdminConstants.ROLE_SALES, AdminConstants.ROLE_AUDITOR)) {
            if (removeRoleByCode(userId, roleCode) > 0) {
                removed.add(roleCode);
            }
        }
        if (!removed.isEmpty()) {
            auditService.audit(
                operatorId,
                operatorName,
                operatorRoleCode,
                "admin_user.role_conflict_auto_cleanup",
                "admin_user_roles",
                userId,
                "success",
                null,
                null,
                null,
                "auto cleanup roles=" + removed + ", context=" + context
            );
        }
        return removed;
    }

    private int removeRoleByCode(String userId, String roleCode) {
        AdminRole role = adminRoleMapper.selectByRoleCode(roleCode);
        if (role == null) {
            return 0;
        }
        return adminUserRoleMapper.deleteByUserAndRole(userId, role.getId());
    }

    private void revokeActiveSessionsByUser(String userId) {
        LocalDateTime now = LocalDateTime.now();
        adminSessionMapper.revokeActiveByUserId(userId, now, now);
    }
}

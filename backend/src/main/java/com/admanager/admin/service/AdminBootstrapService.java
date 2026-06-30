package com.admanager.admin.service;

import com.admanager.admin.mapper.AdminRoleMapper;
import com.admanager.admin.mapper.AdminUserMapper;
import com.admanager.admin.mapper.AdminUserRoleMapper;
import com.admanager.admin.model.AdminRole;
import com.admanager.admin.model.AdminUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AdminBootstrapService {

    private final AdminUserMapper adminUserMapper;
    private final AdminRoleMapper adminRoleMapper;
    private final AdminUserRoleMapper adminUserRoleMapper;
    private final PasswordEncoder passwordEncoder;

    public AdminBootstrapService(AdminUserMapper adminUserMapper,
                                 AdminRoleMapper adminRoleMapper,
                                 AdminUserRoleMapper adminUserRoleMapper,
                                 PasswordEncoder passwordEncoder) {
        this.adminUserMapper = adminUserMapper;
        this.adminRoleMapper = adminRoleMapper;
        this.adminUserRoleMapper = adminUserRoleMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void initializeBootstrapAdmin(String rawUsername,
                                         String rawPassword,
                                         String displayName,
                                         String email) {
        String username = rawUsername.trim();
        AdminUser existing = adminUserMapper.selectByUsername(username);
        if (existing != null) {
            return;
        }

        AdminRole primarySuperAdminRole = adminRoleMapper.selectByRoleCode(AdminConstants.ROLE_PRIMARY_SUPER_ADMIN);
        if (primarySuperAdminRole == null) {
            return;
        }
        AdminRole superAdminRole = adminRoleMapper.selectByRoleCode(AdminConstants.ROLE_SUPER_ADMIN);

        LocalDateTime now = LocalDateTime.now();
        AdminUser user = new AdminUser();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setDisplayName(displayName);
        user.setEmail(email == null || email.isBlank() ? null : email.trim());
        user.setStatus(AdminConstants.STATUS_ACTIVE);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setLoginFailCount(0);

        adminUserMapper.insert(user);
        adminUserRoleMapper.insert(user.getId(), primarySuperAdminRole.getId(), null, now);
        if (superAdminRole != null) {
            adminUserRoleMapper.insert(user.getId(), superAdminRole.getId(), null, now);
        }
    }
}

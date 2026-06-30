package com.admanager.admin.service;

import com.admanager.admin.dto.AdminProfileResponse;
import com.admanager.admin.dto.ChangePasswordRequest;
import com.admanager.admin.dto.LoginRequest;
import com.admanager.admin.dto.LoginResponse;
import com.admanager.admin.dto.LogoutRequest;
import com.admanager.admin.dto.RefreshRequest;
import com.admanager.admin.mapper.AdminRoleMapper;
import com.admanager.admin.mapper.AdminSessionMapper;
import com.admanager.admin.mapper.AdminUserMapper;
import com.admanager.admin.model.AdminSession;
import com.admanager.admin.model.AdminUser;
import com.admanager.admin.security.AdminPrincipal;
import com.admanager.admin.security.JwtTokenProvider;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminAuthService {

    private static final Logger log = LoggerFactory.getLogger(AdminAuthService.class);

    private final AdminUserMapper adminUserMapper;
    private final AdminRoleMapper adminRoleMapper;
    private final AdminSessionMapper adminSessionMapper;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AdminAuditService auditService;

    @Value("${admanager.admin.auth.login-fail-threshold:5}")
    private int loginFailThreshold;

    @Value("${admanager.admin.auth.lock-minutes:30}")
    private int lockMinutes;

    public AdminAuthService(AdminUserMapper adminUserMapper,
                            AdminRoleMapper adminRoleMapper,
                            AdminSessionMapper adminSessionMapper,
                            JwtTokenProvider tokenProvider,
                            PasswordEncoder passwordEncoder,
                            AdminAuditService auditService) {
        this.adminUserMapper = adminUserMapper;
        this.adminRoleMapper = adminRoleMapper;
        this.adminSessionMapper = adminSessionMapper;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    @Transactional(noRollbackFor = ResponseStatusException.class)
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        AdminUser user = adminUserMapper.selectByUsername(request.username());
        if (user == null) {
            auditService.audit(null, request.username(), null, "auth.login", "admin_users", null,
                "failed", clientIp(httpRequest), userAgent(httpRequest), null, "user_not_found");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
        }

        if (isLocked(user)) {
            auditService.audit(user.getId(), user.getUsername(), null, "auth.login", "admin_users", user.getId(),
                "failed", clientIp(httpRequest), userAgent(httpRequest), null, "account_locked");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "账号已被锁定，请稍后重试");
        }

        if (!AdminConstants.STATUS_ACTIVE.equals(user.getStatus())) {
            auditService.audit(user.getId(), user.getUsername(), null, "auth.login", "admin_users", user.getId(),
                "failed", clientIp(httpRequest), userAgent(httpRequest), null, "account_not_active");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "账号不可登录");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            int failed = (user.getLoginFailCount() == null ? 0 : user.getLoginFailCount()) + 1;
            LocalDateTime lockedUntil = failed >= loginFailThreshold
                ? LocalDateTime.now().plusMinutes(lockMinutes)
                : null;
            adminUserMapper.updateLoginFailure(user.getId(), failed, lockedUntil, LocalDateTime.now());
            auditService.audit(user.getId(), user.getUsername(), null, "auth.login", "admin_users", user.getId(),
                "failed", clientIp(httpRequest), userAgent(httpRequest), null, "bad_credentials");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
        }

        List<String> roles = adminRoleMapper.selectByUserId(user.getId()).stream().map(r -> r.getRoleCode()).toList();
        if (roles.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "账号未分配角色");
        }

        LocalDateTime now = LocalDateTime.now();
        adminUserMapper.updateLoginSuccess(user.getId(), now, now);

        JwtTokenProvider.TokenIssueResult refresh = tokenProvider.issueRefreshToken(user.getId(), user.getUsername());

        AdminSession session = new AdminSession();
        session.setId(refresh.jti());
        session.setUserId(user.getId());
        session.setTokenHash(sha256(refresh.token()));
        session.setIp(clientIp(httpRequest));
        session.setUserAgent(userAgent(httpRequest));
        session.setCreatedAt(now);
        session.setExpiresAt(refresh.expiresAt());
        session.setRevokedAt(null);
        adminSessionMapper.insert(session);

        JwtTokenProvider.TokenIssueResult access = tokenProvider.issueAccessToken(
            user.getId(),
            user.getUsername(),
            roles,
            session.getId()
        );

        auditService.audit(user.getId(), user.getUsername(), firstRole(roles), "auth.login", "admin_sessions", refresh.jti(),
            "success", clientIp(httpRequest), userAgent(httpRequest), null, null);

        return new LoginResponse(
            access.token(),
            refresh.token(),
            access.expiresAt(),
            refresh.expiresAt(),
            toProfile(adminUserMapper.selectById(user.getId()), roles)
        );
    }

    @Transactional
    public LoginResponse refresh(RefreshRequest request, HttpServletRequest httpRequest) {
        String refreshToken = request.refreshToken();
        String jti;
        String userId;
        LocalDateTime now = LocalDateTime.now();
        String refreshTokenHash = sha256(refreshToken);
        try {
            if (!AdminConstants.TOKEN_TYPE_REFRESH.equals(tokenProvider.getTokenType(refreshToken))) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "刷新令牌无效");
            }
            jti = tokenProvider.getJti(refreshToken);
            userId = tokenProvider.getUserId(refreshToken);
        } catch (JwtException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "刷新令牌无效");
        }

        int revoked = adminSessionMapper.revokeActiveByIdAndHash(
            jti, refreshTokenHash, now, now);
        if (revoked == 0) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "会话已失效，请重新登录");
        }

        AdminUser user = adminUserMapper.selectById(userId);
        if (user == null || !AdminConstants.STATUS_ACTIVE.equals(user.getStatus())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "账号不可用，请重新登录");
        }

        List<String> roles = adminRoleMapper.selectByUserId(user.getId()).stream().map(r -> r.getRoleCode()).toList();
        if (roles.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "账号未分配角色");
        }

        JwtTokenProvider.TokenIssueResult refresh = tokenProvider.issueRefreshToken(user.getId(), user.getUsername());

        AdminSession newSession = new AdminSession();
        newSession.setId(refresh.jti());
        newSession.setUserId(user.getId());
        newSession.setTokenHash(sha256(refresh.token()));
        newSession.setIp(clientIp(httpRequest));
        newSession.setUserAgent(userAgent(httpRequest));
        newSession.setCreatedAt(LocalDateTime.now());
        newSession.setExpiresAt(refresh.expiresAt());
        adminSessionMapper.insert(newSession);

        JwtTokenProvider.TokenIssueResult access = tokenProvider.issueAccessToken(
            user.getId(),
            user.getUsername(),
            roles,
            newSession.getId()
        );

        auditService.audit(user.getId(), user.getUsername(), firstRole(roles), "auth.refresh", "admin_sessions", refresh.jti(),
            "success", clientIp(httpRequest), userAgent(httpRequest), null, null);

        return new LoginResponse(
            access.token(),
            refresh.token(),
            access.expiresAt(),
            refresh.expiresAt(),
            toProfile(user, roles)
        );
    }

    public void logout(LogoutRequest request, HttpServletRequest httpRequest) {
        String refreshToken = request.refreshToken();
        try {
            if (!AdminConstants.TOKEN_TYPE_REFRESH.equals(tokenProvider.getTokenType(refreshToken))) {
                auditLogoutEvent(
                    null,
                    null,
                    null,
                    "auth.logout",
                    "admin_sessions",
                    null,
                    "failed",
                    clientIp(httpRequest),
                    userAgent(httpRequest),
                    null,
                    "invalid_token_type"
                );
                return;
            }
            String jti = tokenProvider.getJti(refreshToken);
            String userId = tokenProvider.getUserId(refreshToken);
            LocalDateTime now = LocalDateTime.now();
            int revoked = adminSessionMapper.revokeActiveByIdAndHash(
                jti, sha256(refreshToken), now, now);
            if (revoked > 0) {
                AdminUser user = adminUserMapper.selectById(userId);
                auditLogoutEvent(userId, user == null ? null : user.getUsername(), null,
                    "auth.logout", "admin_sessions", jti, "success",
                    clientIp(httpRequest), userAgent(httpRequest), null, null);
            } else {
                auditLogoutEvent(userId, null, null,
                    "auth.logout", "admin_sessions", jti, "failed",
                    clientIp(httpRequest), userAgent(httpRequest), null, "session_not_found_or_revoked");
            }
        } catch (JwtException ex) {
            auditLogoutEvent(
                null,
                null,
                null,
                "auth.logout",
                "admin_sessions",
                null,
                "failed",
                clientIp(httpRequest),
                userAgent(httpRequest),
                null,
                "invalid_refresh_token"
            );
        } catch (Exception ex) {
            log.warn("logout failed unexpectedly", ex);
            // logout is best-effort and must remain idempotent
        }
    }

    @Transactional(noRollbackFor = ResponseStatusException.class)
    public void changePassword(ChangePasswordRequest request, HttpServletRequest httpRequest) {
        AdminPrincipal principal = requirePrincipal();
        AdminUser user = adminUserMapper.selectById(principal.userId());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "账号不存在");
        }

        String roleCode = firstRole(principal.roles());

        if (!passwordEncoder.matches(request.oldPassword(), user.getPasswordHash())) {
            auditService.audit(
                user.getId(),
                user.getUsername(),
                roleCode,
                "auth.password_change",
                "admin_users",
                user.getId(),
                "failed",
                clientIp(httpRequest),
                userAgent(httpRequest),
                null,
                "old_password_mismatch"
            );
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "旧密码错误");
        }

        if (passwordEncoder.matches(request.newPassword(), user.getPasswordHash())) {
            auditService.audit(
                user.getId(),
                user.getUsername(),
                roleCode,
                "auth.password_change",
                "admin_users",
                user.getId(),
                "failed",
                clientIp(httpRequest),
                userAgent(httpRequest),
                null,
                "new_password_same_as_old"
            );
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "新密码不能与旧密码相同");
        }

        adminUserMapper.updatePasswordHash(
            user.getId(),
            passwordEncoder.encode(request.newPassword()),
            LocalDateTime.now()
        );

        auditService.audit(
            user.getId(),
            user.getUsername(),
            roleCode,
            "auth.password_change",
            "admin_users",
            user.getId(),
            "success",
            clientIp(httpRequest),
            userAgent(httpRequest),
            null,
            null
        );
    }

    private void auditLogoutEvent(String operatorId,
                                  String operatorName,
                                  String roleCode,
                                  String action,
                                  String resourceType,
                                  String resourceId,
                                  String result,
                                  String ip,
                                  String userAgent,
                                  String requestId,
                                  String errorMessage) {
        try {
            auditService.audit(
                operatorId,
                operatorName,
                roleCode,
                action,
                resourceType,
                resourceId,
                result,
                ip,
                userAgent,
                requestId,
                errorMessage
            );
        } catch (RuntimeException ex) {
            log.error("AUDIT_WRITE_FAILED action={}, resourceType={}, resourceId={}, result={}",
                action, resourceType, resourceId, result, ex);
        }
    }

    @Transactional(readOnly = true)
    public AdminProfileResponse me() {
        AdminPrincipal principal = requirePrincipal();
        AdminUser user = adminUserMapper.selectById(principal.userId());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "账号不存在");
        }
        List<String> roles = adminRoleMapper.selectByUserId(user.getId()).stream().map(r -> r.getRoleCode()).toList();
        return toProfile(user, roles);
    }

    public AdminPrincipal requirePrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AdminPrincipal principal)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录");
        }
        return principal;
    }

    private AdminProfileResponse toProfile(AdminUser user, List<String> roles) {
        return new AdminProfileResponse(
            user.getId(),
            user.getUsername(),
            user.getDisplayName(),
            user.getEmail(),
            user.getStatus(),
            roles,
            user.getLastLoginAt()
        );
    }

    private boolean isLocked(AdminUser user) {
        return user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now());
    }

    private String firstRole(List<String> roles) {
        return roles.isEmpty() ? null : roles.get(0);
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String userAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to hash token", ex);
        }
    }
}

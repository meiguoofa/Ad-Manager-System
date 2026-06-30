package com.admanager.admin.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.admanager.common.dto.ApiResponse;
import com.admanager.admin.service.AdminAuditService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AdminAuditService auditService;

    public JwtAccessDeniedHandler(AdminAuditService auditService) {
        this.auditService = auditService;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String operatorId = null;
        String operatorName = null;
        String roleCode = null;
        if (authentication != null && authentication.getPrincipal() instanceof AdminPrincipal principal) {
            operatorId = principal.userId();
            operatorName = principal.username();
            roleCode = principal.roles().isEmpty() ? null : principal.roles().get(0);
        }

        auditService.audit(operatorId, operatorName, roleCode,
            "authz.denied", "api", request.getRequestURI(), "failed",
            request.getRemoteAddr(), request.getHeader("User-Agent"), null,
            accessDeniedException.getMessage());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), ApiResponse.of(40301, "权限不足", null));
    }
}

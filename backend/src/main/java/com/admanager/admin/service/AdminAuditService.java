package com.admanager.admin.service;

import com.admanager.admin.dto.AuditLogItem;
import com.admanager.admin.dto.AuditFilterOptionsResponse;
import com.admanager.admin.dto.AuditOperatorOption;
import com.admanager.admin.mapper.AdminAuditLogMapper;
import com.admanager.admin.model.AdminAuditLog;
import com.admanager.common.dto.PageResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AdminAuditService {

    private static final Logger log = LoggerFactory.getLogger(AdminAuditService.class);
    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    private static final int DEFAULT_FILTER_WINDOW_DAYS = 30;
    private static final int DEFAULT_FILTER_LIMIT = 100;
    private static final int MAX_FILTER_LIMIT = 100;

    private final AdminAuditLogMapper auditLogMapper;

    public AdminAuditService(AdminAuditLogMapper auditLogMapper) {
        this.auditLogMapper = auditLogMapper;
    }

    public void audit(String operatorId,
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
        ResolvedAuditContext context = resolveContext(ip, userAgent, requestId);
        AdminAuditLog auditLog = new AdminAuditLog();
        auditLog.setId(UUID.randomUUID().toString());
        auditLog.setOperatorId(operatorId);
        auditLog.setOperatorName(operatorName);
        auditLog.setRoleCode(roleCode);
        auditLog.setAction(action);
        auditLog.setResourceType(resourceType);
        auditLog.setResourceId(resourceId);
        auditLog.setResult(result);
        auditLog.setIp(context.ip());
        auditLog.setUserAgent(context.userAgent());
        auditLog.setRequestId(context.requestId());
        auditLog.setErrorMessage(errorMessage);
        auditLog.setOccurredAt(LocalDateTime.now());
        try {
            auditLogMapper.insert(auditLog);
        } catch (RuntimeException ex) {
            log.error(
                "AUDIT_WRITE_FAILED action={}, resourceType={}, resourceId={}, operatorId={}, result={}, requestId={}",
                action, resourceType, resourceId, operatorId, result, context.requestId(), ex
            );
            throw ex;
        }
    }

    @Transactional(readOnly = true)
    public PageResponse<AuditLogItem> list(int page,
                                           int size,
                                           String action,
                                           String operatorId,
                                           String resourceType,
                                           String resourceId,
                                           LocalDateTime from,
                                           LocalDateTime to) {
        if (from != null && to != null && from.isAfter(to)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "from must be before to");
        }
        int offset = page * size;
        List<AuditLogItem> content = auditLogMapper.selectPaged(
                offset, size, action, operatorId, resourceType, resourceId, from, to
            ).stream()
            .map(log -> new AuditLogItem(
                log.getId(),
                log.getOperatorId(),
                log.getOperatorName(),
                log.getRoleCode(),
                log.getAction(),
                log.getResourceType(),
                log.getResourceId(),
                log.getResult(),
                log.getIp(),
                log.getUserAgent(),
                log.getRequestId(),
                log.getErrorMessage(),
                log.getOccurredAt()
            ))
            .toList();
        long total = auditLogMapper.countPaged(action, operatorId, resourceType, resourceId, from, to);
        return PageResponse.of(content, page, size, total);
    }

    @Transactional(readOnly = true)
    public AuditFilterOptionsResponse listFilterOptions(String operatorKeyword,
                                                        LocalDateTime from,
                                                        LocalDateTime to,
                                                        Integer limit) {
        ResolvedTimeWindow window = resolveTimeWindow(from, to);
        int resolvedLimit = normalizeLimit(limit);
        String normalizedKeyword = trimToNull(operatorKeyword);

        List<AuditOperatorOption> operators = auditLogMapper.selectDistinctOperators(
                normalizedKeyword,
                window.from(),
                window.to(),
                resolvedLimit
            ).stream()
            .map(row -> new AuditOperatorOption(
                row.getOperatorId(),
                trimToNull(row.getOperatorName()) == null ? row.getOperatorId() : row.getOperatorName()
            ))
            .toList();

        List<String> actions = auditLogMapper.selectDistinctActions(window.from(), window.to(), resolvedLimit);
        List<String> resourceTypes = auditLogMapper.selectDistinctResourceTypes(window.from(), window.to(), resolvedLimit);

        return new AuditFilterOptionsResponse(operators, actions, resourceTypes);
    }

    private ResolvedAuditContext resolveContext(String ip, String userAgent, String requestId) {
        HttpServletRequest request = currentRequest();
        String resolvedIp = trimToNull(ip);
        if (resolvedIp == null) {
            resolvedIp = extractClientIp(request);
        }

        String resolvedUserAgent = trimToNull(userAgent);
        if (resolvedUserAgent == null && request != null) {
            resolvedUserAgent = trimToNull(request.getHeader("User-Agent"));
        }

        String resolvedRequestId = trimToNull(requestId);
        if (resolvedRequestId == null && request != null) {
            resolvedRequestId = trimToNull(request.getHeader(REQUEST_ID_HEADER));
        }
        if (resolvedRequestId == null) {
            resolvedRequestId = UUID.randomUUID().toString();
        }

        return new ResolvedAuditContext(resolvedIp, resolvedUserAgent, resolvedRequestId);
    }

    private HttpServletRequest currentRequest() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes)) {
            return null;
        }
        return attributes.getRequest();
    }

    private String extractClientIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String forwarded = trimToNull(request.getHeader("X-Forwarded-For"));
        if (forwarded != null) {
            return trimToNull(forwarded.split(",")[0]);
        }
        return trimToNull(request.getRemoteAddr());
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private int normalizeLimit(Integer limit) {
        if (limit == null) {
            return DEFAULT_FILTER_LIMIT;
        }
        if (limit <= 0) {
            return 1;
        }
        return Math.min(limit, MAX_FILTER_LIMIT);
    }

    private ResolvedTimeWindow resolveTimeWindow(LocalDateTime from, LocalDateTime to) {
        LocalDateTime resolvedTo = to == null ? LocalDateTime.now() : to;
        LocalDateTime resolvedFrom = from == null ? resolvedTo.minusDays(DEFAULT_FILTER_WINDOW_DAYS) : from;
        if (resolvedFrom.isAfter(resolvedTo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "from must be before to");
        }
        return new ResolvedTimeWindow(resolvedFrom, resolvedTo);
    }

    private record ResolvedAuditContext(String ip, String userAgent, String requestId) {
    }

    private record ResolvedTimeWindow(LocalDateTime from, LocalDateTime to) {
    }
}

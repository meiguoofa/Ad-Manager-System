package com.admanager.admin.dto;

import java.util.List;

public record AuditFilterOptionsResponse(
    List<AuditOperatorOption> operators,
    List<String> actions,
    List<String> resourceTypes
) {
}

package com.admanager.admin.security;

import java.util.List;

public record AdminPrincipal(
    String userId,
    String username,
    List<String> roles
) {
}

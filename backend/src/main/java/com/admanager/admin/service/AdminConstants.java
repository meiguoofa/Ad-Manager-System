package com.admanager.admin.service;

public final class AdminConstants {
    public static final String STATUS_PENDING_INIT = "pending_init";
    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_SUSPENDED = "suspended";
    public static final String STATUS_DISABLED = "disabled";

    public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";
    public static final String ROLE_PRIMARY_SUPER_ADMIN = "PRIMARY_SUPER_ADMIN";
    public static final String ROLE_OPERATOR = "OPERATOR";
    public static final String ROLE_SALES = "SALES";
    public static final String ROLE_AUDITOR = "AUDITOR";

    public static final String TOKEN_TYPE_ACCESS = "access";
    public static final String TOKEN_TYPE_REFRESH = "refresh";

    private AdminConstants() {
    }
}

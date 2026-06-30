package com.admanager.admin.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrapInitializer implements CommandLineRunner {

    @Value("${admanager.admin.auth.bootstrap.enabled:false}")
    private boolean enabled;

    @Value("${admanager.admin.auth.bootstrap.username:}")
    private String username;

    @Value("${admanager.admin.auth.bootstrap.password:}")
    private String password;

    @Value("${admanager.admin.auth.bootstrap.display-name:超级管理员}")
    private String displayName;

    @Value("${admanager.admin.auth.bootstrap.email:}")
    private String email;

    private final AdminBootstrapService adminBootstrapService;

    public AdminBootstrapInitializer(AdminBootstrapService adminBootstrapService) {
        this.adminBootstrapService = adminBootstrapService;
    }

    @Override
    public void run(String... args) {
        if (!enabled || username == null || username.isBlank() || password == null || password.isBlank()) {
            return;
        }
        adminBootstrapService.initializeBootstrapAdmin(username, password, displayName, email);
    }
}

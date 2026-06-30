package com.admanager.admin.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

@Component
public class AdminAuthConfigValidator {

    private static final String JWT_PLACEHOLDER_PREFIX = "CHANGE_ME_";

    private final Environment environment;

    @Value("${admanager.admin.auth.jwt-secret:}")
    private String jwtSecret;

    @Value("${admanager.admin.auth.bootstrap.enabled:false}")
    private boolean bootstrapEnabled;

    @Value("${admanager.admin.auth.bootstrap.username:}")
    private String bootstrapUsername;

    @Value("${admanager.admin.auth.bootstrap.password:}")
    private String bootstrapPassword;

    public AdminAuthConfigValidator(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void validate() {
        boolean testProfileActive = environment.acceptsProfiles(Profiles.of("test"));
        if (!testProfileActive) {
            if (isBlank(jwtSecret) || jwtSecret.startsWith(JWT_PLACEHOLDER_PREFIX)) {
                throw new IllegalStateException(
                    "admanager.admin.auth.jwt-secret is not configured. " +
                        "Please set QJCAM_ADMIN_JWT_SECRET via environment variables or .env in dev profile."
                );
            }
        }

        if (bootstrapEnabled && (isBlank(bootstrapUsername) || isBlank(bootstrapPassword))) {
            throw new IllegalStateException(
                "Bootstrap is enabled, but admanager.admin.auth.bootstrap.username/password is missing."
            );
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}

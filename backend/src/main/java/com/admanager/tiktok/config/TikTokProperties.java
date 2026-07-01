package com.admanager.tiktok.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "admanager.tiktok")
public class TikTokProperties {
    private String appId;
    private String secret;
    private String apiBaseUrl;
    private String authBaseUrl;
    private String redirectUri;
    private String frontendCallbackUrl;
}

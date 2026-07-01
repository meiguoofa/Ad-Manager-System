package com.admanager.tiktok.controller;

import com.admanager.common.dto.ApiResponse;
import com.admanager.tiktok.config.TikTokProperties;
import com.admanager.tiktok.service.TikTokOAuthService;
import com.admanager.tiktok.service.TikTokOAuthService.CallbackResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tiktok")
public class TikTokOAuthController {

    private final TikTokOAuthService oauthService;
    private final TikTokProperties properties;

    public TikTokOAuthController(TikTokOAuthService oauthService, TikTokProperties properties) {
        this.oauthService = oauthService;
        this.properties = properties;
    }

    @GetMapping("/auth-url")
    public ApiResponse<String> authUrl() {
        String state = UUID.randomUUID().toString();
        return ApiResponse.success(oauthService.buildAuthUrl(state));
    }

    @GetMapping("/callback")
    public ResponseEntity<Void> callback(@RequestParam("auth_code") String authCode,
                                         @RequestParam(value = "state", required = false) String state) {
        CallbackResult result = oauthService.handleCallback(authCode, state);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getFrontendCallbackUrl())
            .queryParam("status", result.success() ? "success" : "fail");
        if (result.message() != null) {
            builder.queryParam("message", result.message());
        }
        if (result.advertiserIds() != null) {
            builder.queryParam("advertiserIds", result.advertiserIds());
        }
        URI location = builder.encode().build().toUri();

        return ResponseEntity.status(HttpStatus.FOUND).location(location).build();
    }
}

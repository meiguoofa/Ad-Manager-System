package com.admanager.tiktok.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TikTokAuth {
    private String id;
    private String appId;
    private String authCode;
    private String state;
    private LocalDateTime callbackReceivedAt;
    private String advertiserIds;
    private String accessToken;
    private String refreshToken;
    private String scope;
    private String exchangeStatus;
    private String rawResponse;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

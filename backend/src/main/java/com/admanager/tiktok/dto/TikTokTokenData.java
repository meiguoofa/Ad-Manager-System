package com.admanager.tiktok.dto;

import java.util.List;

/**
 * TikTok /oauth2/access_token/ 返回体的 data 字段。
 */
public record TikTokTokenData(
    String accessToken,
    String refreshToken,
    List<String> advertiserIds,
    String scope
) {
}

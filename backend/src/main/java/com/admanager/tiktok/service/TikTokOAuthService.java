package com.admanager.tiktok.service;

import com.admanager.tiktok.config.TikTokProperties;
import com.admanager.tiktok.mapper.TikTokAuthMapper;
import com.admanager.tiktok.model.TikTokAuth;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class TikTokOAuthService {

    private final TikTokProperties properties;
    private final TikTokAuthMapper authMapper;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public TikTokOAuthService(TikTokProperties properties,
                              TikTokAuthMapper authMapper,
                              RestTemplate restTemplate,
                              ObjectMapper objectMapper) {
        this.properties = properties;
        this.authMapper = authMapper;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String buildAuthUrl(String state) {
        return UriComponentsBuilder.fromHttpUrl(properties.getAuthBaseUrl())
            .queryParam("app_id", properties.getAppId())
            .queryParam("state", state == null ? "" : state)
            .queryParam("redirect_uri", properties.getRedirectUri())
            .build()
            .toUriString();
    }

    /**
     * 处理 TikTok 回调:先落库回调参数,再用 auth_code 换 token 并回填结果。
     *
     * @return 授权结果(成功与否、advertiser_ids、错误信息)
     */
    public CallbackResult handleCallback(String authCode, String state) {
        TikTokAuth record = new TikTokAuth();
        record.setId(UUID.randomUUID().toString());
        record.setAppId(properties.getAppId());
        record.setAuthCode(authCode);
        record.setState(state);
        record.setCallbackReceivedAt(LocalDateTime.now());
        record.setExchangeStatus("pending");
        authMapper.insert(record);

        try {
            String responseBody = exchangeToken(authCode);
            record.setRawResponse(responseBody);

            JsonNode root = objectMapper.readTree(responseBody);
            int code = root.path("code").asInt(-1);
            if (code != 0) {
                String message = root.path("message").asText("TikTok 授权失败");
                record.setExchangeStatus("fail");
                authMapper.update(record);
                log.warn("TikTok token exchange failed: code={}, message={}", code, message);
                return new CallbackResult(false, message, null);
            }

            JsonNode data = root.path("data");
            record.setAccessToken(data.path("access_token").asText(null));
            record.setRefreshToken(data.path("refresh_token").asText(null));
            String advertiserIds = joinArray(data.path("advertiser_ids"));
            record.setAdvertiserIds(advertiserIds);
            record.setScope(joinArray(data.path("scope")));
            record.setExchangeStatus("success");
            authMapper.update(record);

            return new CallbackResult(true, "授权成功", advertiserIds);
        } catch (Exception ex) {
            record.setExchangeStatus("fail");
            if (record.getRawResponse() == null) {
                record.setRawResponse(ex.getMessage());
            }
            authMapper.update(record);
            log.error("TikTok token exchange error", ex);
            return new CallbackResult(false, "授权处理异常: " + ex.getMessage(), null);
        }
    }

    private String exchangeToken(String authCode) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("app_id", properties.getAppId());
        body.put("secret", properties.getSecret());
        body.put("auth_code", authCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        String url = properties.getApiBaseUrl() + "/open_api/v1.3/oauth2/access_token/";
        return restTemplate.postForObject(url, entity, String.class);
    }

    private String joinArray(JsonNode node) {
        if (node == null || !node.isArray() || node.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (JsonNode item : node) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(item.asText());
        }
        return sb.toString();
    }

    public record CallbackResult(boolean success, String message, String advertiserIds) {
    }
}

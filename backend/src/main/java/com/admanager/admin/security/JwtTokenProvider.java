package com.admanager.admin.security;

import com.admanager.admin.service.AdminConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    @Value("${admanager.admin.auth.jwt-secret}")
    private String jwtSecret;

    @Value("${admanager.admin.auth.access-token-ttl-seconds:14400}")
    private long accessTokenTtlSeconds;

    @Value("${admanager.admin.auth.refresh-token-ttl-seconds:604800}")
    private long refreshTokenTtlSeconds;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(sha256(jwtSecret));
    }

    public TokenIssueResult issueAccessToken(String userId, String username, List<String> roles, String sessionId) {
        String jti = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(accessTokenTtlSeconds);
        String token = Jwts.builder()
            .subject(username)
            .id(jti)
            .claim("userId", userId)
            .claim("roles", roles)
            .claim("sid", sessionId)
            .claim("typ", AdminConstants.TOKEN_TYPE_ACCESS)
            .issuedAt(new Date())
            .expiration(toDate(expiresAt))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
        return new TokenIssueResult(token, jti, expiresAt);
    }

    public TokenIssueResult issueRefreshToken(String userId, String username) {
        String jti = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(refreshTokenTtlSeconds);
        String token = Jwts.builder()
            .subject(username)
            .id(jti)
            .claim("userId", userId)
            .claim("typ", AdminConstants.TOKEN_TYPE_REFRESH)
            .issuedAt(new Date())
            .expiration(toDate(expiresAt))
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
        return new TokenIssueResult(token, jti, expiresAt);
    }

    public Claims parseClaims(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }

    public String getTokenType(String token) {
        return parseClaims(token).get("typ", String.class);
    }

    public String getUserId(String token) {
        return parseClaims(token).get("userId", String.class);
    }

    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public String getJti(String token) {
        return parseClaims(token).getId();
    }

    public String getSessionId(String token) {
        return parseClaims(token).get("sid", String.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        Object roles = parseClaims(token).get("roles");
        if (roles instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        return List.of();
    }

    private Date toDate(LocalDateTime time) {
        return Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
    }

    private byte[] sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to initialize JWT secret", ex);
        }
    }

    public record TokenIssueResult(String token, String jti, LocalDateTime expiresAt) {
    }
}

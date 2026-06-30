package com.admanager.admin.security;

import com.admanager.admin.mapper.AdminSessionMapper;
import com.admanager.admin.model.AdminSession;
import com.admanager.admin.service.AdminConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final AdminSessionMapper adminSessionMapper;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider,
                                   AdminSessionMapper adminSessionMapper) {
        this.tokenProvider = tokenProvider;
        this.adminSessionMapper = adminSessionMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!isAdminApiRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = resolveToken(request);

        if (StringUtils.hasText(token)) {
            try {
                Claims claims = tokenProvider.parseClaims(token);
                String tokenType = claims.get("typ", String.class);
                if (AdminConstants.TOKEN_TYPE_ACCESS.equals(tokenType)) {
                    String userId = claims.get("userId", String.class);
                    String username = claims.getSubject();
                    List<String> roles = tokenProvider.getRoles(token);
                    if (!isSessionActive(token, userId)) {
                        SecurityContextHolder.clearContext();
                        filterChain.doFilter(request, response);
                        return;
                    }
                    AdminPrincipal principal = new AdminPrincipal(userId, username, roles);
                    List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .toList();

                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(principal, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (JwtException ignored) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isSessionActive(String accessToken, String userId) {
        String sessionId = tokenProvider.getSessionId(accessToken);
        if (!StringUtils.hasText(sessionId)) {
            return false;
        }
        AdminSession session = adminSessionMapper.selectActiveById(sessionId, LocalDateTime.now());
        return session != null && userId.equals(session.getUserId());
    }

    private boolean isAdminApiRequest(HttpServletRequest request) {
        return request.getRequestURI() != null && request.getRequestURI().startsWith("/api/v1/admin/");
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}


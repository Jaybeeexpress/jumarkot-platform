package com.jumarkot.iam.infrastructure.security;

import com.jumarkot.iam.application.auth.TokenService;
import com.jumarkot.iam.domain.apikey.ApiKey;
import com.jumarkot.iam.domain.apikey.ApiKeyRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String API_KEY_HEADER = "X-API-Key";
    private static final String BEARER_PREFIX = "Bearer ";

    private final TokenService tokenService;
    private final ApiKeyRepository apiKeyRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String apiKeyHeader = request.getHeader(API_KEY_HEADER);
            if (StringUtils.hasText(apiKeyHeader)) {
                authenticateWithApiKey(apiKeyHeader);
            } else {
                String bearerToken = extractBearerToken(request);
                if (StringUtils.hasText(bearerToken)) {
                    authenticateWithJwt(bearerToken);
                }
            }
        } catch (Exception ex) {
            log.debug("Authentication failed: {}", ex.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateWithJwt(String token) {
        Claims claims = tokenService.validateAndParseClaims(token);
        if (!tokenService.isAccessToken(claims)) {
            log.debug("Token is not an access token");
            return;
        }
        UUID userId = tokenService.extractUserId(claims);
        UUID tenantId = tokenService.extractTenantId(claims);
        List<String> roles = tokenService.extractRoles(claims);

        List<SimpleGrantedAuthority> authorities = roles == null
                ? List.of()
                : roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        JwtPrincipal principal = new JwtPrincipal(userId, tenantId, roles != null ? roles : List.of());
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("JWT authentication set for user {} tenant {}", userId, tenantId);
    }

    private void authenticateWithApiKey(String rawKey) {
        String keyHash = sha256Hex(rawKey);
        Optional<ApiKey> optionalApiKey = apiKeyRepository.findByKeyHash(keyHash);

        if (optionalApiKey.isEmpty()) {
            log.debug("API key not found");
            return;
        }

        ApiKey apiKey = optionalApiKey.get();
        if (!apiKey.isUsable()) {
            log.debug("API key {} is not usable (status={}, expired={})", apiKey.getId(), apiKey.getStatus(), apiKey.isExpired());
            return;
        }

        apiKey.setLastUsedAt(Instant.now());
        apiKeyRepository.save(apiKey);

        List<SimpleGrantedAuthority> authorities = apiKey.getScopes().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("SERVICE_ACCOUNT"));

        JwtPrincipal principal = new JwtPrincipal(
                apiKey.getUserId(),
                apiKey.getTenantId(),
                apiKey.getScopes());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.debug("API key authentication set for user {} tenant {}", apiKey.getUserId(), apiKey.getTenantId());
    }

    private String extractBearerToken(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    public record JwtPrincipal(UUID userId, UUID tenantId, List<String> roles) {}
}

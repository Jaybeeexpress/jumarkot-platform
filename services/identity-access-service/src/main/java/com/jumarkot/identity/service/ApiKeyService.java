package com.jumarkot.identity.service;

import com.jumarkot.contracts.identity.ApiKeyCreateRequest;
import com.jumarkot.contracts.identity.ApiKeyResponse;
import com.jumarkot.identity.domain.ApiKey;
import com.jumarkot.identity.domain.User;
import com.jumarkot.identity.repository.ApiKeyRepository;
import com.jumarkot.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String KEY_PREFIX_HEADER = "jmk_";

    public ApiKeyResponse create(UUID tenantId, ApiKeyCreateRequest request, String creatorEmail) {
        byte[] rawBytes = new byte[32];
        SECURE_RANDOM.nextBytes(rawBytes);
        String rawKey = KEY_PREFIX_HEADER + Base64.getUrlEncoder().withoutPadding().encodeToString(rawBytes);
        String prefix = rawKey.substring(0, Math.min(12, rawKey.length()));
        String hash = passwordEncoder.encode(rawKey);

        Instant expiresAt = request.getExpiresInDays() != null
                ? Instant.now().plus(request.getExpiresInDays(), ChronoUnit.DAYS)
                : null;

        User createdBy = userRepository.findByEmail(creatorEmail).orElse(null);

        ApiKey apiKey = ApiKey.builder()
                .tenantId(tenantId)
                .name(request.getName())
                .keyPrefix(prefix)
                .keyHash(hash)
                .scopes(List.copyOf(request.getScopes()))
                .active(true)
                .expiresAt(expiresAt)
                .createdBy(createdBy)
                .build();

        apiKeyRepository.save(apiKey);

        return ApiKeyResponse.builder()
                .id(apiKey.getId().toString())
                .name(apiKey.getName())
                .rawKey(rawKey)
                .keyPrefix(prefix)
                .scopes(request.getScopes())
                .expiresAt(expiresAt)
                .createdAt(apiKey.getCreatedAt())
                .active(true)
                .build();
    }

    public List<ApiKeyResponse> listForTenant(UUID tenantId) {
        return apiKeyRepository.findByTenantIdAndActiveTrue(tenantId).stream()
                .map(k -> ApiKeyResponse.builder()
                        .id(k.getId().toString())
                        .name(k.getName())
                        .keyPrefix(k.getKeyPrefix())
                        .scopes(k.getScopes() != null ? new java.util.HashSet<>(k.getScopes()) : java.util.Set.of())
                        .expiresAt(k.getExpiresAt())
                        .createdAt(k.getCreatedAt())
                        .active(k.isActive())
                        .build())
                .collect(Collectors.toList());
    }

    public void revoke(UUID tenantId, UUID keyId) {
        apiKeyRepository.findById(keyId).ifPresent(k -> {
            if (k.getTenantId().equals(tenantId)) {
                k.setActive(false);
                apiKeyRepository.save(k);
            }
        });
    }
}

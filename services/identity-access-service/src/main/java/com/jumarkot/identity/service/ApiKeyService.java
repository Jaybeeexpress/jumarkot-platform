package com.jumarkot.identity.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumarkot.identity.domain.ApiKey;
import com.jumarkot.identity.repository.ApiKeyRepository;
import com.jumarkot.shared.auth.ApiKeyHasher;
import com.jumarkot.shared.auth.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class ApiKeyService {

    private static final Logger log = LoggerFactory.getLogger(ApiKeyService.class);
    private static final int RAW_KEY_BYTE_LENGTH = 32; // 256 bits → 43 base64url chars
    private static final Duration CACHE_TTL = Duration.ofDays(30);

    private final ApiKeyRepository repository;
    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;
    private final SecureRandom secureRandom = new SecureRandom();

    public ApiKeyService(ApiKeyRepository repository,
                         StringRedisTemplate redis,
                         ObjectMapper objectMapper) {
        this.repository = repository;
        this.redis = redis;
        this.objectMapper = objectMapper;
    }

    /**
     * Creates a new API key for the given tenant and environment.
     *
     * @return the raw key — this is the only time it is available in plaintext.
     */
    @Transactional
    public CreateApiKeyResult create(UUID tenantId, UUID environmentId,
                                     TenantContext.EnvironmentType environmentType,
                                     String name, List<String> scopes) {
        String rawKey = generateRawKey(environmentType);
        String keyHash = ApiKeyHasher.hash(rawKey);
        String prefix = rawKey.substring(0, rawKey.indexOf('_', 3) + 1);

        ApiKey key = new ApiKey(
                UUID.randomUUID(),
                tenantId,
                environmentType.name(),
                prefix,
                keyHash,
                name,
                scopes,
                true,
                null,
                null,
                OffsetDateTime.now(),
                null,
                null,
                null
        );

        repository.insert(key);
        publishToCache(rawKey, key, tenantId, environmentId, environmentType, scopes);

        log.info("API key created: id={} tenant={} env={}", key.id(), tenantId, environmentType);
        return new CreateApiKeyResult(key.id(), rawKey, prefix);
    }

    @Transactional
    public void revoke(UUID keyId, UUID tenantId, String reason) {
        ApiKey key = repository.findById(keyId, tenantId)
                .orElseThrow(() -> new ApiKeyNotFoundException("Key not found: " + keyId));

        repository.revoke(keyId, tenantId, reason);
        evictFromCache(key.keyHash());

        log.info("API key revoked: id={} tenant={}", keyId, tenantId);
    }

    public List<ApiKey> listForTenant(UUID tenantId) {
        return repository.findAllByTenantId(tenantId);
    }

    // ─── Private helpers ─────────────────────────────────────────────────────

    private String generateRawKey(TenantContext.EnvironmentType env) {
        byte[] bytes = new byte[RAW_KEY_BYTE_LENGTH];
        secureRandom.nextBytes(bytes);
        String suffix = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        String prefix = (env == TenantContext.EnvironmentType.PRODUCTION) ? "jk_live_" : "jk_test_";
        return prefix + suffix;
    }

    private void publishToCache(String rawKey, ApiKey key, UUID tenantId, UUID environmentId,
                                TenantContext.EnvironmentType environmentType, List<String> scopes) {
        TenantContext ctx = new TenantContext(
                tenantId, null, environmentId, environmentType, scopes, key.id());
        try {
            String json = objectMapper.writeValueAsString(ctx);
            redis.opsForValue().set(ApiKeyHasher.cacheKey(rawKey), json, CACHE_TTL);
        } catch (Exception e) {
            log.error("Failed to publish API key to Redis cache; key created but cache miss will occur", e);
        }
    }

    private void evictFromCache(String keyHash) {
        redis.delete(ApiKeyHasher.CACHE_KEY_PREFIX + keyHash);
    }

    public record CreateApiKeyResult(UUID keyId, String rawKey, String keyPrefix) {}
}

package com.jumarkot.shared.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Resolves an API key against the Redis cache populated by identity-access-service.
 *
 * <p>Cache entry format (JSON string at key {@code apikey:<sha256>}):
 * <pre>
 * {
 *   "tenantId":       "...",
 *   "tenantSlug":     "acme",
 *   "environmentId":  "...",
 *   "environmentType":"PRODUCTION",
 *   "scopes":         ["decisions:write","events:write"],
 *   "apiKeyId":       "..."
 * }
 * </pre>
 *
 * <p>Cache entries are written by identity-access-service when a key is created or activated,
 * and deleted when the key is revoked or expires.
 */
public class RedisApiKeyResolver implements ApiKeyResolver {

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    public RedisApiKeyResolver(StringRedisTemplate redis, ObjectMapper objectMapper) {
        this.redis = redis;
        this.objectMapper = objectMapper;
    }

    @Override
    public TenantContext resolve(String rawApiKey) throws InvalidApiKeyException {
        String cacheKey = ApiKeyHasher.cacheKey(rawApiKey);
        String json = redis.opsForValue().get(cacheKey);

        if (json == null || json.isBlank()) {
            throw new InvalidApiKeyException("API key not found or revoked");
        }

        try {
            return objectMapper.readValue(json, TenantContext.class);
        } catch (Exception e) {
            throw new InvalidApiKeyException("Malformed API key context in cache");
        }
    }
}

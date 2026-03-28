package com.jumarkot.shared.auth;

/**
 * Resolves a raw API key string into a validated TenantContext.
 * Implementations vary by service — typically Redis-backed with a DB fallback.
 */
public interface ApiKeyResolver {

    /**
     * @param rawApiKey the full key as presented in the request header
     * @return the associated TenantContext if the key is valid and active
     * @throws InvalidApiKeyException if the key is not found, revoked, or expired
     */
    TenantContext resolve(String rawApiKey) throws InvalidApiKeyException;
}

package com.jumarkot.shared.auth;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Deterministic, one-way hashing for API key storage and lookup.
 * We store SHA-256 of the full key — never the raw key.
 */
public final class ApiKeyHasher {

    public static final String CACHE_KEY_PREFIX = "apikey:";

    private ApiKeyHasher() {}

    /** Produces a lowercase hex SHA-256 digest of the raw key. */
    public static String hash(String rawApiKey) {
        return DigestUtils.sha256Hex(rawApiKey);
    }

    /** Returns the Redis cache key for a given raw API key. */
    public static String cacheKey(String rawApiKey) {
        return CACHE_KEY_PREFIX + hash(rawApiKey);
    }

    /**
     * Returns a masked display value for logging — preserves the prefix and
     * tail characters, obscures the middle.
     * e.g. "jk_live_abc123...xyz789" → "jk_live_abc...789"
     */
    public static String mask(String rawApiKey) {
        if (rawApiKey == null || rawApiKey.length() < 12) return "***";
        return rawApiKey.substring(0, Math.min(12, rawApiKey.length() - 4))
                + "..."
                + rawApiKey.substring(rawApiKey.length() - 4);
    }
}

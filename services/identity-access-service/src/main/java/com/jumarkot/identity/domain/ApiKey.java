package com.jumarkot.identity.domain;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Immutable domain record for an API key.
 * {@code keyHash} is a SHA-256 hex digest of the raw key — the raw key is never persisted.
 */
public record ApiKey(
        UUID id,
        UUID tenantId,
        String environmentType,   // "PRODUCTION" | "SANDBOX"
        String keyPrefix,         // "jk_live_" | "jk_test_"
        String keyHash,           // SHA-256 of raw key
        String name,
        List<String> scopes,
        boolean active,
        OffsetDateTime expiresAt,
        OffsetDateTime lastUsedAt,
        OffsetDateTime createdAt,
        UUID createdBy,
        OffsetDateTime revokedAt,
        String revokeReason
) {}

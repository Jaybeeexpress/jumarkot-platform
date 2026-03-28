package com.jumarkot.shared.auth;

import java.util.List;
import java.util.UUID;

/**
 * Immutable identity context extracted from a validated API key.
 * Propagated via TenantContextHolder for the lifetime of a request.
 */
public record TenantContext(
        UUID tenantId,
        String tenantSlug,

        /** The specific environment this key is scoped to. */
        UUID environmentId,
        EnvironmentType environmentType,

        /** The API key's granted scopes (e.g. "decisions:write", "events:write"). */
        List<String> scopes,

        /** Internal key ID — not the raw key. Use for audit logs only. */
        UUID apiKeyId
) {
    public boolean hasScope(String scope) {
        return scopes.contains("*") || scopes.contains(scope);
    }

    public boolean isSandbox() {
        return environmentType == EnvironmentType.SANDBOX;
    }

    public enum EnvironmentType {
        PRODUCTION,
        SANDBOX
    }
}

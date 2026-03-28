package com.jumarkot.tenant.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TenantEnvironment(
        UUID id,
        UUID tenantId,
        String name,            // e.g. "production", "sandbox"
        String type,            // PRODUCTION | SANDBOX
        String status,          // ACTIVE | SUSPENDED
        OffsetDateTime createdAt
) {}

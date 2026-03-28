package com.jumarkot.tenant.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

public record Tenant(
        UUID id,
        String slug,          // URL-safe unique identifier, e.g. "acme-fintech"
        String name,
        String status,        // ACTIVE | SUSPENDED | PENDING
        String plan,          // FREE | STARTER | GROWTH | ENTERPRISE
        String contactEmail,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}

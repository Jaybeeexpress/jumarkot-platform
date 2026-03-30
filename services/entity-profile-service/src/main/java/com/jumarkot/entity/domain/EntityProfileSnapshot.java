package com.jumarkot.entity.domain;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record EntityProfileSnapshot(
        UUID tenantId,
        String environmentType,
        String entityType,
        String entityId,
        String latestEventType,
        Instant latestEventAt,
        UUID lastEventId,
        String lastIdempotencyKey,
        Map<String, Object> latestProperties,
        Instant firstSeenAt,
        Instant lastSeenAt,
        Instant updatedAt
) {}

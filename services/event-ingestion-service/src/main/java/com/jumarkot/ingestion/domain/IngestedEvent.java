package com.jumarkot.ingestion.domain;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record IngestedEvent(
        UUID id,
        UUID tenantId,
        UUID environmentId,
        String environmentType,
        UUID apiKeyId,
        String idempotencyKey,
        String eventType,
        String entityId,
        String entityType,
        Instant occurredAt,
        Map<String, Object> properties,
        String ipAddress,
        String deviceId,
        String sessionId,
        String userAgent,
        String ingestionStatus,
        String deliveryStatus,
        Instant publishedAt,
        String lastDeliveryError,
        Instant createdAt
) {}
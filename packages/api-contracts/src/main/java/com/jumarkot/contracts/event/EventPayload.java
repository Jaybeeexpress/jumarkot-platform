package com.jumarkot.contracts.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.Map;

/**
 * Inbound event payload for POST /v1/events.
 * Tenants submit raw platform events for storage, enrichment, and async decisioning.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record EventPayload(

        @NotBlank @Size(max = 64)
        String idempotencyKey,

        @NotNull
        EventType eventType,

        @NotBlank @Size(max = 255)
        String entityId,

        @NotBlank @Size(max = 64)
        String entityType,

        /** ISO-8601 timestamp of when the event occurred in the source system. */
        Instant occurredAt,

        Map<String, Object> properties,

        String ipAddress,
        String deviceId,
        String sessionId,
        String userAgent
) {}

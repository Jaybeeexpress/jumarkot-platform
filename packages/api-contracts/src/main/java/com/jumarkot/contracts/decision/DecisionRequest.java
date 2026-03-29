package com.jumarkot.contracts.decision;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Map;

/**
 * Inbound payload for POST /v1/decisions.
 * Callers supply an idempotency key to prevent duplicate processing.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DecisionRequest(

        @NotBlank(message = "idempotencyKey is required")
        @Size(max = 64)
        String idempotencyKey,

        @NotBlank(message = "entityId is required")
        @Size(max = 255)
        String entityId,

        @NotBlank(message = "entityType is required")
        @Size(max = 64)
        String entityType,

        @NotBlank(message = "eventType is required")
        @Size(max = 64)
        String eventType,

        /** Flexible event-specific data. Keys follow snake_case convention. */
        Map<String, Object> payload,

        String ipAddress,
        String deviceId,
        String sessionId,
        String userAgent
) {}

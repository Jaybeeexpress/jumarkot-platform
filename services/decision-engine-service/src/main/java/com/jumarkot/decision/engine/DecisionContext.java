package com.jumarkot.decision.engine;

import java.util.Map;

/**
 * Immutable evaluation context built from an inbound DecisionRequest.
 * Passed to the rule evaluator to keep the engine stateless.
 */
public record DecisionContext(
        String entityId,
        String entityType,
        String eventType,
        Map<String, Object> payload,
        String ipAddress,
        String deviceId,
        String sessionId,
        String userAgent
) {}

package com.jumarkot.decision.engine;

import java.util.Map;

/**
 * Extracts a field value from a DecisionRequest context for use in condition evaluation.
 * Supports top-level request fields and dot-notation paths into the payload map.
 *
 * <p>Field path examples:
 * <ul>
 *   <li>{@code entityId} — top-level request field</li>
 *   <li>{@code eventType} — top-level request field</li>
 *   <li>{@code payload.amount} — key inside the payload map</li>
 *   <li>{@code payload.device_id} — key inside the payload map</li>
 * </ul>
 */
public class FieldExtractor {

    public Object extract(String fieldPath, DecisionContext ctx) {
        if (fieldPath == null || fieldPath.isBlank()) return null;

        if (fieldPath.startsWith("payload.")) {
            return extractFromPayload(fieldPath.substring(8), ctx.payload());
        }

        return switch (fieldPath) {
            case "entityId"    -> ctx.entityId();
            case "entityType"  -> ctx.entityType();
            case "eventType"   -> ctx.eventType();
            case "ipAddress"   -> ctx.ipAddress();
            case "deviceId"    -> ctx.deviceId();
            case "sessionId"   -> ctx.sessionId();
            case "userAgent"   -> ctx.userAgent();
            default            -> null;
        };
    }

    private Object extractFromPayload(String payloadPath, Map<String, Object> payload) {
        if (payload == null || payloadPath.isBlank()) return null;

        Object current = payload;
        for (String segment : payloadPath.split("\\.")) {
            if (!(current instanceof Map<?, ?> map)) {
                return null;
            }
            current = map.get(segment);
            if (current == null) {
                return null;
            }
        }
        return current;
    }
}

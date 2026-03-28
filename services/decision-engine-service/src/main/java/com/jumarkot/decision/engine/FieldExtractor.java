package com.jumarkot.decision.engine;

import com.jumarkot.rules.dto.RuleDto;

import java.util.Arrays;
import java.util.List;

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
            String key = fieldPath.substring(8);
            return ctx.payload() != null ? ctx.payload().get(key) : null;
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
}

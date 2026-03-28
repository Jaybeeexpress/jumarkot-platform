package com.jumarkot.rules.dto;

import com.jumarkot.rules.domain.ConditionLogic;
import com.jumarkot.rules.domain.RuleOperator;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Full rule representation returned to consumers (including the decision engine).
 * This is the contract used by the internal rule-loading API.
 */
public record RuleDto(
        UUID id,
        UUID tenantId,
        String environmentType,   // "PRODUCTION" | "SANDBOX"
        String name,
        String description,
        String category,          // e.g. "VELOCITY", "DEVICE", "AMOUNT"
        int priority,             // Higher = evaluated first
        String status,            // "ACTIVE" | "INACTIVE" | "DRAFT"
        List<RuleConditionDto> conditions,
        ConditionLogic conditionLogic,
        String action,            // "DECLINE" | "REVIEW" | "BLOCK" | "FLAG"
        int scoreAdjustment,      // How much this rule contributes to the risk score (0–100)
        String reasonCode,
        OffsetDateTime effectiveFrom,
        OffsetDateTime effectiveTo,
        int version
) {
    public record RuleConditionDto(
            /** Dot-notation field path, e.g. "payload.amount" or "ipAddress" */
            String field,
            RuleOperator operator,
            String value
    ) {}
}

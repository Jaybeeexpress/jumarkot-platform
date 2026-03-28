package com.jumarkot.decision.rules;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/** Internal rule contract consumed by decision-engine from rules-service API. */
public record RuleDto(
        UUID id,
        UUID tenantId,
        String environmentType,
        String name,
        String description,
        String category,
        int priority,
        String status,
        List<RuleConditionDto> conditions,
        ConditionLogic conditionLogic,
        String action,
        int scoreAdjustment,
        String reasonCode,
        OffsetDateTime effectiveFrom,
        OffsetDateTime effectiveTo,
        int version
) {
    public record RuleConditionDto(
            String field,
            RuleOperator operator,
            String value
    ) {}
}

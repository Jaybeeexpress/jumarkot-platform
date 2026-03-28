package com.jumarkot.contracts.decision;

/**
 * Summary of a rule that matched during decision evaluation.
 * Included in the decision response for explainability and audit.
 */
public record MatchedRule(
        String ruleId,
        String ruleName,
        int priority,
        String reasonCode,
        int scoreContribution
) {}

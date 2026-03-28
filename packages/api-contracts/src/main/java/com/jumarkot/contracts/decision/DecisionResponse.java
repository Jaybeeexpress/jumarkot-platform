package com.jumarkot.contracts.decision;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

/**
 * Structured decision response returned from POST /v1/decisions.
 * Consumers use {@code decision} and {@code recommendedAction} as the
 * primary action signals; {@code matchedRules} and {@code triggeredSignals}
 * provide audit-grade explainability.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DecisionResponse(

        /** Stable UUID for this decision — use in audit logs and case references. */
        String decisionId,

        /** Final decision outcome. */
        RiskDecision decision,

        /** Risk score 0–100. */
        int riskScore,

        /** Bucketed risk level derived from riskScore. */
        RiskLevel riskLevel,

        /** Rules whose conditions matched during evaluation, in priority order. */
        List<MatchedRule> matchedRules,

        /** Discrete signals that contributed to the score. */
        List<TriggeredSignal> triggeredSignals,

        /** Suggested action for the caller's system (e.g. ALLOW, CHALLENGE, BLOCK). */
        String recommendedAction,

        /**
         * Echoed from the request idempotency key.
         * Safe to log and correlate across systems.
         */
        String correlationId,

        Instant timestamp
) {}

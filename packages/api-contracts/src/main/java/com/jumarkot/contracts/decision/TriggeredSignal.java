package com.jumarkot.contracts.decision;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * A discrete risk signal emitted during rule evaluation.
 * Multiple signals compose the overall risk score.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TriggeredSignal(
        String signalType,
        String description,
        /** Severity 0–100 — used for display; score contribution tracked in MatchedRule. */
        int severity,
        /** The observed value that triggered the signal (redact PII if needed). */
        Object observedValue
) {}

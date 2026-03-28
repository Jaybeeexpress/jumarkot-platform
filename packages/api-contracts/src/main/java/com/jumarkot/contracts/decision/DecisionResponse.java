package com.jumarkot.contracts.decision;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.List;

/**
 * Response returned to the caller after evaluation.
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DecisionResponse {

    String decisionId;
    String idempotencyKey;
    String tenantId;
    String entityId;
    DecisionEventType eventType;

    /** Final decision outcome. */
    DecisionOutcome outcome;

    /** Risk score 0–1000 (higher = riskier). */
    int riskScore;

    /** Human-readable reason codes. */
    List<String> reasonCodes;

    /** Recommended action for the calling system. */
    String recommendedAction;

    Instant evaluatedAt;
    long processingTimeMs;
}

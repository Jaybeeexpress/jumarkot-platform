package com.jumarkot.contracts.decision;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/**
 * Response payload returned by the Jumarkot decision engine after evaluating a
 * {@link DecisionRequest}.
 *
 * <p>Callers should act on {@link #recommendation} as the primary outcome. The {@link #score}
 * provides a continuous risk signal (0–1000) for downstream analytics and thresholding.
 * {@link #signals} lists the individual rule or model findings that contributed to the decision.
 */
@Data
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DecisionResponse {

    /**
     * Globally unique identifier for this decision record. Stable across retries when
     * an idempotency key is supplied in the originating request.
     */
    private final String decisionId;

    /**
     * The entity ID from the originating {@link DecisionRequest}.
     */
    private final String entityId;

    /**
     * The event type from the originating {@link DecisionRequest}.
     */
    private final String eventType;

    /**
     * Composite risk score in the range [0, 1000].
     * <ul>
     *   <li>0–249 → low risk</li>
     *   <li>250–499 → medium risk</li>
     *   <li>500–749 → high risk</li>
     *   <li>750–1000 → critical risk</li>
     * </ul>
     */
    private final int score;

    /**
     * The engine's recommended action for the calling service.
     */
    private final DecisionRecommendation recommendation;

    /**
     * Ordered list of named signals (rule names, model labels, sanction-list matches, etc.)
     * that contributed to the decision. Useful for explainability, case notes, and audit trails.
     */
    private final List<DecisionSignal> signals;

    /**
     * UTC timestamp at which the decision was computed.
     */
    private final Instant decidedAt;

    /**
     * Version of the rule-set / model configuration that produced this decision.
     * Useful for reproducing and auditing decisions at a point in time.
     */
    private final String rulesetVersion;

    // ── Nested signal record ──────────────────────────────────────────────────

    /**
     * An individual contributing signal within a {@link DecisionResponse}.
     */
    @Data
    @Builder
    @Jacksonized
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DecisionSignal {

        /** Unique name of the rule, model, or list that fired. */
        private final String name;

        /** Human-readable description of what was detected. */
        private final String description;

        /** Severity level of this individual signal: LOW, MEDIUM, HIGH, CRITICAL. */
        private final String severity;

        /** Score contribution of this signal to the composite {@link DecisionResponse#score}. */
        private final int scoreContribution;

        /** Additional metadata key-value pairs specific to this signal. */
        private final java.util.Map<String, Object> metadata;
    }
}

package com.jumarkot.contracts.decision;

/**
 * Enumeration of possible recommendations produced by the Jumarkot decision engine.
 *
 * <p>Recommendations are ordered by severity:
 * <ol>
 *   <li>{@link #ALLOW} – lowest friction, entity passes all checks.</li>
 *   <li>{@link #CHALLENGE} – step-up authentication or friction required.</li>
 *   <li>{@link #REVIEW} – flagged for human case-management review.</li>
 *   <li>{@link #BLOCK} – highest severity, action must be denied immediately.</li>
 * </ol>
 */
public enum DecisionRecommendation {

    /**
     * Entity passed all risk, fraud, and compliance checks. The requested action
     * should be permitted without additional friction.
     */
    ALLOW,

    /**
     * Elevated risk detected. The caller should present a step-up challenge
     * (e.g. MFA, CAPTCHA, email OTP) before permitting the action.
     */
    CHALLENGE,

    /**
     * The entity or action has been flagged for manual review by a compliance
     * or fraud analyst. The action may be held pending investigation.
     */
    REVIEW,

    /**
     * The entity or action has been definitively blocked due to high-confidence
     * fraud, sanctions match, or critical policy violation.
     */
    BLOCK
}

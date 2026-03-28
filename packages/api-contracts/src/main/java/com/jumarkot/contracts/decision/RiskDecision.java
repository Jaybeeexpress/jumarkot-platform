package com.jumarkot.contracts.decision;

/** Final outcome of a risk decision evaluation. */
public enum RiskDecision {

    /** Request passes all controls. Proceed normally. */
    APPROVE,

    /** Request warrants additional validation before proceeding (e.g. step-up auth, manual review). */
    REVIEW,

    /** Request is declined. Do not proceed. Surface reason to user if appropriate. */
    DECLINE,

    /** Immediate block. Flag entity. Escalate for investigation. */
    BLOCK
}

package com.jumarkot.contracts.decision;

/** Score band derived from the numeric risk score (0–100). */
public enum RiskLevel {
    /** 0–25 */
    LOW,
    /** 26–50 */
    MEDIUM,
    /** 51–74 */
    HIGH,
    /** 75–100 */
    CRITICAL;

    public static RiskLevel fromScore(int score) {
        if (score <= 25) return LOW;
        if (score <= 50) return MEDIUM;
        if (score <= 74) return HIGH;
        return CRITICAL;
    }
}

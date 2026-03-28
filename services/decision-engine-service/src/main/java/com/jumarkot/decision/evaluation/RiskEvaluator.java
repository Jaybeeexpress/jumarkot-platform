package com.jumarkot.decision.evaluation;

import com.jumarkot.contracts.decision.DecisionOutcome;
import com.jumarkot.contracts.decision.DecisionRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Baseline risk evaluator — applies simple signal-based scoring.
 * Production implementation should load rules from rules-service.
 */
@Component
public class RiskEvaluator {

    public EvaluationResult evaluate(DecisionRequest request) {
        int score = 0;
        List<String> reasons = new ArrayList<>();
        Map<String, Object> ctx = request.getContext();

        // --- Simple baseline signals ---
        score += evaluateAmountSignal(ctx, reasons);
        score += evaluateCountrySignal(ctx, reasons);
        score += evaluateVelocitySignal(ctx, reasons);
        score += evaluateDeviceSignal(ctx, reasons);

        score = Math.min(score, 1000);

        DecisionOutcome outcome;
        String action;
        if (score >= 700) {
            outcome = DecisionOutcome.BLOCK;
            action  = "BLOCK_TRANSACTION";
        } else if (score >= 400) {
            outcome = DecisionOutcome.REVIEW;
            action  = "FLAG_FOR_REVIEW";
        } else if (score >= 200) {
            outcome = DecisionOutcome.CHALLENGE;
            action  = "STEP_UP_AUTH";
        } else {
            outcome = DecisionOutcome.ALLOW;
            action  = "PROCEED";
        }

        return EvaluationResult.builder()
                .outcome(outcome)
                .riskScore(score)
                .reasonCodes(reasons)
                .recommendedAction(action)
                .build();
    }

    private int evaluateAmountSignal(Map<String, Object> ctx, List<String> reasons) {
        Object amountObj = ctx.get("amount");
        if (amountObj == null) return 0;
        double amount = Double.parseDouble(amountObj.toString());
        if (amount > 50000) { reasons.add("HIGH_AMOUNT"); return 300; }
        if (amount > 10000) { reasons.add("ELEVATED_AMOUNT"); return 100; }
        return 0;
    }

    private int evaluateCountrySignal(Map<String, Object> ctx, List<String> reasons) {
        Object country = ctx.get("country");
        if (country == null) return 0;
        List<String> highRisk = List.of("AF", "KP", "IR", "SY", "YE");
        if (highRisk.contains(country.toString().toUpperCase())) {
            reasons.add("HIGH_RISK_COUNTRY");
            return 400;
        }
        return 0;
    }

    private int evaluateVelocitySignal(Map<String, Object> ctx, List<String> reasons) {
        Object velocityObj = ctx.get("txCountLast1h");
        if (velocityObj == null) return 0;
        int count = Integer.parseInt(velocityObj.toString());
        if (count > 20) { reasons.add("HIGH_VELOCITY"); return 350; }
        if (count > 10) { reasons.add("ELEVATED_VELOCITY"); return 150; }
        return 0;
    }

    private int evaluateDeviceSignal(Map<String, Object> ctx, List<String> reasons) {
        Object newDevice = ctx.get("newDevice");
        if (Boolean.TRUE.equals(newDevice)) {
            reasons.add("NEW_DEVICE");
            return 100;
        }
        return 0;
    }
}

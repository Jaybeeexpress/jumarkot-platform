package com.jumarkot.decision.evaluation;

import com.jumarkot.contracts.decision.DecisionOutcome;
import com.jumarkot.contracts.decision.DecisionRequest;
import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Result produced by the rule evaluation pipeline.
 */
@Value
@Builder
public class EvaluationResult {
    DecisionOutcome outcome;
    int riskScore;
    List<String> reasonCodes;
    String recommendedAction;
}

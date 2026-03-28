package com.jumarkot.decision.engine;

import com.jumarkot.contracts.decision.MatchedRule;
import com.jumarkot.contracts.decision.TriggeredSignal;
import com.jumarkot.decision.rules.RuleDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Evaluates a list of rules against a DecisionContext.
 * Returns only the rules whose conditions matched, in priority order.
 * The caller is responsible for aggregating scores.
 */
@Component
public class RuleEvaluator {

    private final FieldExtractor fieldExtractor = new FieldExtractor();
    private final ConditionOperatorEvaluator operatorEvaluator = new ConditionOperatorEvaluator();

    public EvaluationResult evaluate(List<RuleDto> rules, DecisionContext ctx) {
        List<MatchedRule> matchedRules = new ArrayList<>();
        List<TriggeredSignal> triggeredSignals = new ArrayList<>();
        int totalScore = 0;

        for (RuleDto rule : rules) {
            if (conditionsMatch(rule, ctx)) {
                matchedRules.add(new MatchedRule(
                        rule.id() != null ? rule.id().toString() : "n/a",
                        rule.name(),
                        rule.priority(),
                        rule.reasonCode(),
                        rule.scoreAdjustment()
                ));
                triggeredSignals.add(new TriggeredSignal(
                        rule.category(),
                        rule.name() + ": " + rule.reasonCode(),
                        rule.scoreAdjustment(),
                        null
                ));
                totalScore = Math.min(100, totalScore + rule.scoreAdjustment());
            }
        }

        return new EvaluationResult(matchedRules, triggeredSignals, totalScore);
    }

    private boolean conditionsMatch(RuleDto rule, DecisionContext ctx) {
        if (rule.conditions() == null || rule.conditions().isEmpty()) return false;

        List<Boolean> results = rule.conditions().stream()
                .map(condition -> {
                    Object fieldValue = fieldExtractor.extract(condition.field(), ctx);
                    return operatorEvaluator.evaluate(fieldValue, condition.operator(), condition.value());
                })
                .toList();

        return switch (rule.conditionLogic()) {
            case AND -> results.stream().allMatch(Boolean::booleanValue);
            case OR  -> results.stream().anyMatch(Boolean::booleanValue);
        };
    }

    public record EvaluationResult(
            List<MatchedRule> matchedRules,
            List<TriggeredSignal> triggeredSignals,
            int riskScore
    ) {}
}

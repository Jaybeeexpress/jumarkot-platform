package com.jumarkot.decision.engine;

import com.jumarkot.rules.domain.RuleOperator;

import java.util.Arrays;

/**
 * Applies a {@link RuleOperator} to a field value against a condition threshold.
 * All comparisons are null-safe. Numeric comparisons fall back to lexicographic
 * ordering when values cannot be parsed as doubles.
 */
public class ConditionOperatorEvaluator {

    public boolean evaluate(Object fieldValue, RuleOperator operator, String conditionValue) {
        if (fieldValue == null) {
            return operator == RuleOperator.NOT_EXISTS || operator == RuleOperator.NOT_EQUALS;
        }

        String fieldStr = String.valueOf(fieldValue);

        return switch (operator) {
            case EQUALS                -> fieldStr.equals(conditionValue);
            case NOT_EQUALS            -> !fieldStr.equals(conditionValue);
            case GREATER_THAN          -> compareNumericOrLex(fieldValue, conditionValue) > 0;
            case GREATER_THAN_OR_EQUAL -> compareNumericOrLex(fieldValue, conditionValue) >= 0;
            case LESS_THAN             -> compareNumericOrLex(fieldValue, conditionValue) < 0;
            case LESS_THAN_OR_EQUAL    -> compareNumericOrLex(fieldValue, conditionValue) <= 0;
            case CONTAINS              -> fieldStr.contains(conditionValue);
            case NOT_CONTAINS          -> !fieldStr.contains(conditionValue);
            case IN                    -> Arrays.asList(conditionValue.split(","))
                                                 .contains(fieldStr.trim());
            case NOT_IN                -> !Arrays.asList(conditionValue.split(","))
                                                  .contains(fieldStr.trim());
            case EXISTS                -> true;  // fieldValue != null already guarded above
            case NOT_EXISTS            -> false;
        };
    }

    private int compareNumericOrLex(Object fieldValue, String conditionValue) {
        try {
            double a = Double.parseDouble(String.valueOf(fieldValue));
            double b = Double.parseDouble(conditionValue);
            return Double.compare(a, b);
        } catch (NumberFormatException e) {
            return String.valueOf(fieldValue).compareTo(conditionValue);
        }
    }
}

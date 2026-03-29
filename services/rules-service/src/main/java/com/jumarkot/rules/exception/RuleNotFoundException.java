package com.jumarkot.rules.exception;

import java.util.UUID;

public class RuleNotFoundException extends RuntimeException {

    public RuleNotFoundException(UUID ruleId) {
        super("Rule not found: " + ruleId);
    }
}

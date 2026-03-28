package com.jumarkot.rules.api;

import com.jumarkot.contracts.common.ApiResponse;
import com.jumarkot.rules.dto.RuleDto;
import com.jumarkot.rules.repository.RuleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Internal rule-loading API consumed by decision-engine-service.
 * Not exposed through the public API gateway.
 * Protected by network policy — no external authentication required in local/staging.
 */
@RestController
@RequestMapping("/internal/v1/rules")
public class InternalRuleController {

    private final RuleRepository ruleRepository;

    public InternalRuleController(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    @GetMapping("/active")
    public List<RuleDto> getActiveRules(
            @RequestParam UUID tenantId,
            @RequestParam String environmentType) {
        return ruleRepository.findActiveByTenantAndEnvironment(tenantId, environmentType);
    }
}

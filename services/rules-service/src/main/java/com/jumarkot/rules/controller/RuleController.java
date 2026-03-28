package com.jumarkot.rules.controller;

import com.jumarkot.contracts.common.ApiResponse;
import com.jumarkot.rules.domain.Rule;
import com.jumarkot.rules.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/tenants/{tenantId}/rules")
@RequiredArgsConstructor
public class RuleController {

    private final RuleService ruleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Rule>>> list(
            @PathVariable UUID tenantId,
            @RequestParam(required = false) String eventType) {
        return ResponseEntity.ok(ApiResponse.ok(ruleService.getActiveRulesForTenant(tenantId, eventType)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Rule>> create(
            @PathVariable UUID tenantId,
            @RequestBody Rule rule) {
        rule.setTenantId(tenantId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(ruleService.create(rule)));
    }

    @PutMapping("/{ruleId}")
    public ResponseEntity<ApiResponse<Rule>> update(
            @PathVariable UUID tenantId,
            @PathVariable UUID ruleId,
            @RequestBody Rule rule) {
        return ResponseEntity.ok(ApiResponse.ok(ruleService.update(ruleId, rule)));
    }

    @PatchMapping("/{ruleId}/enable")
    public ResponseEntity<Void> enable(@PathVariable UUID tenantId, @PathVariable UUID ruleId) {
        ruleService.setEnabled(ruleId, true);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{ruleId}/disable")
    public ResponseEntity<Void> disable(@PathVariable UUID tenantId, @PathVariable UUID ruleId) {
        ruleService.setEnabled(ruleId, false);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{ruleId}")
    public ResponseEntity<Void> delete(@PathVariable UUID tenantId, @PathVariable UUID ruleId) {
        ruleService.delete(ruleId);
        return ResponseEntity.noContent().build();
    }
}

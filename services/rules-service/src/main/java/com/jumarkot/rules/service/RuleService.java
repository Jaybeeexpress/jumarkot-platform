package com.jumarkot.rules.service;

import com.jumarkot.rules.domain.Rule;
import com.jumarkot.rules.repository.RuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RuleService {

    private final RuleRepository ruleRepository;

    @Transactional(readOnly = true)
    public List<Rule> getActiveRulesForTenant(UUID tenantId, String eventType) {
        if (eventType != null) {
            return ruleRepository.findByTenantIdAndEventTypeAndEnabledTrueOrderByPriorityAsc(tenantId, eventType);
        }
        return ruleRepository.findByTenantIdAndEnabledTrueOrderByPriorityAsc(tenantId);
    }

    @Transactional
    public Rule create(Rule rule) {
        rule.setVersion(1);
        rule.setEnabled(true);
        return ruleRepository.save(rule);
    }

    @Transactional
    public Rule update(UUID id, Rule updated) {
        Rule existing = ruleRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Rule not found: " + id));
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setConditionExpr(updated.getConditionExpr());
        existing.setAction(updated.getAction());
        existing.setPriority(updated.getPriority());
        existing.setRiskScoreDelta(updated.getRiskScoreDelta());
        existing.setEffectiveFrom(updated.getEffectiveFrom());
        existing.setEffectiveUntil(updated.getEffectiveUntil());
        existing.setVersion(existing.getVersion() + 1);
        return ruleRepository.save(existing);
    }

    @Transactional
    public void setEnabled(UUID id, boolean enabled) {
        ruleRepository.findById(id).ifPresent(r -> {
            r.setEnabled(enabled);
            ruleRepository.save(r);
        });
    }

    @Transactional
    public void delete(UUID id) {
        ruleRepository.deleteById(id);
    }
}

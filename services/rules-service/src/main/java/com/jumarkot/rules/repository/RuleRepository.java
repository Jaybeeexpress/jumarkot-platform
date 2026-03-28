package com.jumarkot.rules.repository;

import com.jumarkot.rules.domain.Rule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RuleRepository extends JpaRepository<Rule, UUID> {
    List<Rule> findByTenantIdAndEnabledTrueOrderByPriorityAsc(UUID tenantId);
    List<Rule> findByTenantIdAndEventTypeAndEnabledTrueOrderByPriorityAsc(UUID tenantId, String eventType);
}

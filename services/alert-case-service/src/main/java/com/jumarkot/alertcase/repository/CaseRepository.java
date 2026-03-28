package com.jumarkot.alertcase.repository;

import com.jumarkot.alertcase.domain.Case;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CaseRepository extends JpaRepository<Case, UUID> {
    List<Case> findByTenantIdAndStatusOrderByCreatedAtDesc(UUID tenantId, String status);
    List<Case> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);
}

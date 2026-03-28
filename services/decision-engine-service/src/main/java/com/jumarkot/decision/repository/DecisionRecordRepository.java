package com.jumarkot.decision.repository;

import com.jumarkot.decision.domain.DecisionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DecisionRecordRepository extends JpaRepository<DecisionRecord, UUID> {
    Optional<DecisionRecord> findByIdempotencyKey(String idempotencyKey);
    List<DecisionRecord> findByTenantIdAndEntityIdOrderByEvaluatedAtDesc(UUID tenantId, String entityId);
    List<DecisionRecord> findByTenantIdOrderByEvaluatedAtDesc(UUID tenantId);
}

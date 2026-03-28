package com.jumarkot.alertcase.repository;

import com.jumarkot.alertcase.domain.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AlertRepository extends JpaRepository<Alert, UUID> {
    List<Alert> findByTenantIdAndStatusOrderByCreatedAtDesc(UUID tenantId, String status);
    List<Alert> findByTenantIdOrderByCreatedAtDesc(UUID tenantId);
}

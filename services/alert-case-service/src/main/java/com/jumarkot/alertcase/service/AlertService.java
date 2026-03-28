package com.jumarkot.alertcase.service;

import com.jumarkot.alertcase.domain.Alert;
import com.jumarkot.alertcase.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;

    @Transactional
    public Alert create(Alert alert) {
        alert.setStatus("OPEN");
        return alertRepository.save(alert);
    }

    @Transactional(readOnly = true)
    public List<Alert> list(UUID tenantId, String status) {
        if (status != null) {
            return alertRepository.findByTenantIdAndStatusOrderByCreatedAtDesc(tenantId, status);
        }
        return alertRepository.findByTenantIdOrderByCreatedAtDesc(tenantId);
    }

    @Transactional
    public Alert resolve(UUID tenantId, UUID alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Alert not found: " + alertId));
        alert.setStatus("RESOLVED");
        alert.setResolvedAt(Instant.now());
        return alertRepository.save(alert);
    }

    @Transactional
    public Alert assign(UUID tenantId, UUID alertId, UUID assigneeId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Alert not found: " + alertId));
        alert.setAssignedTo(assigneeId);
        return alertRepository.save(alert);
    }
}

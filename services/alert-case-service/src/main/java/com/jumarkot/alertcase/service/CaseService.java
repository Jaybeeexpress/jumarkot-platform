package com.jumarkot.alertcase.service;

import com.jumarkot.alertcase.domain.Case;
import com.jumarkot.alertcase.repository.CaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CaseService {

    private final CaseRepository caseRepository;

    @Transactional
    public Case create(Case kase) {
        kase.setStatus("OPEN");
        kase.setCaseNumber(generateCaseNumber());
        return caseRepository.save(kase);
    }

    @Transactional(readOnly = true)
    public List<Case> list(UUID tenantId, String status) {
        if (status != null) {
            return caseRepository.findByTenantIdAndStatusOrderByCreatedAtDesc(tenantId, status);
        }
        return caseRepository.findByTenantIdOrderByCreatedAtDesc(tenantId);
    }

    @Transactional
    public Case close(UUID tenantId, UUID caseId) {
        Case kase = caseRepository.findById(caseId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Case not found: " + caseId));
        kase.setStatus("CLOSED");
        kase.setClosedAt(Instant.now());
        return caseRepository.save(kase);
    }

    private String generateCaseNumber() {
        String year = String.valueOf(Instant.now().atZone(java.time.ZoneOffset.UTC).getYear());
        return "CASE-" + year + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

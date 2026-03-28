package com.jumarkot.tenant.service;

import com.jumarkot.contracts.tenant.TenantCreateRequest;
import com.jumarkot.contracts.tenant.TenantResponse;
import com.jumarkot.tenant.domain.Tenant;
import com.jumarkot.tenant.domain.TenantStatus;
import com.jumarkot.tenant.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;

    @Transactional
    public TenantResponse create(TenantCreateRequest request) {
        if (tenantRepository.existsBySlug(request.getSlug())) {
            throw new IllegalArgumentException("Tenant slug already exists: " + request.getSlug());
        }
        Tenant tenant = Tenant.builder()
                .name(request.getName())
                .slug(request.getSlug())
                .plan(request.getPlan() != null ? request.getPlan() : "STARTER")
                .region(request.getRegion() != null ? request.getRegion() : "us-east-1")
                .contactEmail(request.getContactEmail())
                .status(TenantStatus.ACTIVE)
                .build();
        tenantRepository.save(tenant);
        return toResponse(tenant);
    }

    @Transactional(readOnly = true)
    public TenantResponse getById(UUID id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Tenant not found: " + id));
        return toResponse(tenant);
    }

    @Transactional(readOnly = true)
    public List<TenantResponse> listAll() {
        return tenantRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void suspend(UUID id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Tenant not found: " + id));
        tenant.setStatus(TenantStatus.SUSPENDED);
        tenantRepository.save(tenant);
    }

    private TenantResponse toResponse(Tenant t) {
        return TenantResponse.builder()
                .id(t.getId().toString())
                .name(t.getName())
                .slug(t.getSlug())
                .plan(t.getPlan())
                .region(t.getRegion())
                .contactEmail(t.getContactEmail())
                .status(t.getStatus().name())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
    }
}

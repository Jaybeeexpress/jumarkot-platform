package com.jumarkot.tenant.application;

import com.jumarkot.contracts.common.PageResponse;
import com.jumarkot.contracts.tenant.TenantDto;
import com.jumarkot.tenant.domain.tenant.Tenant;
import com.jumarkot.tenant.domain.tenant.TenantRepository;
import com.jumarkot.tenant.domain.tenant.TenantStatus;
import com.jumarkot.tenant.web.dto.CreateTenantRequest;
import com.jumarkot.tenant.web.dto.UpdateTenantRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;

    @Transactional
    public TenantDto createTenant(CreateTenantRequest request) {
        if (tenantRepository.existsBySlug(request.getSlug())) {
            throw new IllegalArgumentException("Slug '" + request.getSlug() + "' is already taken");
        }
        // Derive a deterministic owner UUID from the provided email until a user-lookup service is wired in.
        UUID ownerId = UUID.nameUUIDFromBytes(request.getOwnerEmail().getBytes(StandardCharsets.UTF_8));
        Tenant tenant = Tenant.builder()
                .name(request.getName())
                .slug(request.getSlug())
                .plan(request.getPlan())
                .region(request.getRegion())
                .ownerId(ownerId)
                .status(TenantStatus.PENDING_SETUP)
                .build();
        tenant = tenantRepository.save(tenant);
        log.info("Created tenant id={} slug={}", tenant.getId(), tenant.getSlug());
        return toTenantDto(tenant);
    }

    @Transactional(readOnly = true)
    public TenantDto getTenant(UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new NoSuchElementException("Tenant " + tenantId + " not found"));
        return toTenantDto(tenant);
    }

    @Transactional
    public TenantDto updateTenant(UUID tenantId, UpdateTenantRequest request) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new NoSuchElementException("Tenant " + tenantId + " not found"));
        if (request.getName() != null) {
            tenant.setName(request.getName());
        }
        if (request.getPlan() != null) {
            tenant.setPlan(request.getPlan());
        }
        if (request.getStatus() != null) {
            tenant.setStatus(request.getStatus());
        }
        if (request.getRegion() != null) {
            tenant.setRegion(request.getRegion());
        }
        tenant = tenantRepository.save(tenant);
        log.info("Updated tenant id={}", tenant.getId());
        return toTenantDto(tenant);
    }

    @Transactional
    public TenantDto suspendTenant(UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new NoSuchElementException("Tenant " + tenantId + " not found"));
        tenant.setStatus(TenantStatus.SUSPENDED);
        tenant = tenantRepository.save(tenant);
        log.info("Suspended tenant id={}", tenantId);
        return toTenantDto(tenant);
    }

    @Transactional
    public void deleteTenant(UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new NoSuchElementException("Tenant " + tenantId + " not found"));
        tenant.setStatus(TenantStatus.DELETED);
        tenantRepository.save(tenant);
        log.info("Deleted (soft) tenant id={}", tenantId);
    }

    @Transactional(readOnly = true)
    public PageResponse<TenantDto> listTenants(Pageable pageable) {
        Page<Tenant> page = tenantRepository.findAll(pageable);
        List<TenantDto> data = page.getContent().stream()
                .map(this::toTenantDto)
                .toList();
        return PageResponse.<TenantDto>builder()
                .data(data)
                .totalElements(page.getTotalElements())
                .pageSize(page.getSize())
                .hasNext(page.hasNext())
                .build();
    }

    private TenantDto toTenantDto(Tenant tenant) {
        return TenantDto.builder()
                .tenantId(tenant.getId().toString())
                .slug(tenant.getSlug())
                .name(tenant.getName())
                .contactEmail(null)
                .status(mapStatus(tenant.getStatus()))
                .environments(null)
                .createdAt(tenant.getCreatedAt())
                .updatedAt(tenant.getUpdatedAt())
                .build();
    }

    private TenantDto.TenantStatus mapStatus(TenantStatus domainStatus) {
        return switch (domainStatus) {
            case PENDING_SETUP -> TenantDto.TenantStatus.PENDING_VERIFICATION;
            case ACTIVE -> TenantDto.TenantStatus.ACTIVE;
            case SUSPENDED -> TenantDto.TenantStatus.SUSPENDED;
            case DELETED -> TenantDto.TenantStatus.DEACTIVATED;
        };
    }
}

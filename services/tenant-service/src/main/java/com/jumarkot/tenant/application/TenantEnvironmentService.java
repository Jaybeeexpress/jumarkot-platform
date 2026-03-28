package com.jumarkot.tenant.application;

import com.jumarkot.contracts.tenant.EnvironmentDto;
import com.jumarkot.contracts.tenant.EnvironmentType;
import com.jumarkot.tenant.domain.environment.EnvironmentStatus;
import com.jumarkot.tenant.domain.environment.TenantEnvironment;
import com.jumarkot.tenant.domain.environment.TenantEnvironmentRepository;
import com.jumarkot.tenant.domain.tenant.TenantRepository;
import com.jumarkot.tenant.web.dto.CreateEnvironmentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantEnvironmentService {

    private final TenantRepository tenantRepository;
    private final TenantEnvironmentRepository environmentRepository;

    @Transactional
    public EnvironmentDto createEnvironment(UUID tenantId, CreateEnvironmentRequest request) {
        tenantRepository.findById(tenantId)
                .orElseThrow(() -> new NoSuchElementException("Tenant " + tenantId + " not found"));
        TenantEnvironment env = TenantEnvironment.builder()
                .tenantId(tenantId)
                .name(request.getName())
                .type(request.getType())
                .apiBaseUrl(request.getApiBaseUrl())
                .webhookUrl(request.getWebhookUrl())
                .status(EnvironmentStatus.ACTIVE)
                .build();
        env = environmentRepository.save(env);
        log.info("Created environment id={} tenant={}", env.getId(), tenantId);
        return toEnvironmentDto(env);
    }

    @Transactional(readOnly = true)
    public List<EnvironmentDto> listEnvironments(UUID tenantId) {
        return environmentRepository.findByTenantId(tenantId).stream()
                .map(this::toEnvironmentDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public EnvironmentDto getEnvironment(UUID tenantId, UUID envId) {
        TenantEnvironment env = environmentRepository.findByIdAndTenantId(envId, tenantId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Environment " + envId + " not found in tenant " + tenantId));
        return toEnvironmentDto(env);
    }

    @Transactional
    public EnvironmentDto updateEnvironment(UUID tenantId, UUID envId, CreateEnvironmentRequest request) {
        TenantEnvironment env = environmentRepository.findByIdAndTenantId(envId, tenantId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Environment " + envId + " not found in tenant " + tenantId));
        env.setName(request.getName());
        env.setApiBaseUrl(request.getApiBaseUrl());
        env.setWebhookUrl(request.getWebhookUrl());
        env = environmentRepository.save(env);
        log.info("Updated environment id={} tenant={}", envId, tenantId);
        return toEnvironmentDto(env);
    }

    private EnvironmentDto toEnvironmentDto(TenantEnvironment env) {
        return EnvironmentDto.builder()
                .environmentId(env.getId().toString())
                .name(env.getName())
                .type(mapEnvironmentType(env.getType()))
                .active(env.getStatus() == EnvironmentStatus.ACTIVE)
                .build();
    }

    private EnvironmentType mapEnvironmentType(
            com.jumarkot.tenant.domain.environment.EnvironmentType domainType) {
        return switch (domainType) {
            case PRODUCTION -> EnvironmentType.PRODUCTION;
            case SANDBOX, STAGING -> EnvironmentType.SANDBOX;
        };
    }
}

package com.jumarkot.tenant.service;

import com.jumarkot.tenant.domain.Tenant;
import com.jumarkot.tenant.domain.TenantEnvironment;
import com.jumarkot.tenant.repository.TenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TenantService {

    private final TenantRepository repository;

    public TenantService(TenantRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Tenant provision(String slug, String name, String plan, String contactEmail) {
        Tenant tenant = new Tenant(
                UUID.randomUUID(), slug, name, "ACTIVE", plan,
                contactEmail, null, null);

        repository.insertTenant(tenant);

        // Automatically provision sandbox and production environments for every new tenant
        provisionEnvironment(tenant.id(), "production", "PRODUCTION");
        provisionEnvironment(tenant.id(), "sandbox", "SANDBOX");

        return repository.findById(tenant.id()).orElseThrow();
    }

    public Tenant findById(UUID tenantId) {
        return repository.findById(tenantId)
                .orElseThrow(() -> new TenantNotFoundException("Tenant not found: " + tenantId));
    }

    public List<TenantEnvironment> getEnvironments(UUID tenantId) {
        return repository.findEnvironmentsByTenantId(tenantId);
    }

    public TenantEnvironment getEnvironment(UUID environmentId) {
        return repository.findEnvironmentById(environmentId)
                .orElseThrow(() -> new TenantNotFoundException("Environment not found: " + environmentId));
    }

    private void provisionEnvironment(UUID tenantId, String name, String type) {
        repository.insertEnvironment(new TenantEnvironment(
                UUID.randomUUID(), tenantId, name, type, "ACTIVE", null));
    }
}

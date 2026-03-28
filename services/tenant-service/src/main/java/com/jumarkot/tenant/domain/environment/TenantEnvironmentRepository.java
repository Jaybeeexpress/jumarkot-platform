package com.jumarkot.tenant.domain.environment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenantEnvironmentRepository {

    List<TenantEnvironment> findByTenantId(UUID tenantId);

    Optional<TenantEnvironment> findByIdAndTenantId(UUID id, UUID tenantId);

    TenantEnvironment save(TenantEnvironment env);

    boolean existsByTenantIdAndType(UUID tenantId, EnvironmentType type);
}

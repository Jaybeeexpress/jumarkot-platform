package com.jumarkot.tenant.infrastructure.persistence;

import com.jumarkot.tenant.domain.environment.EnvironmentType;
import com.jumarkot.tenant.domain.environment.TenantEnvironment;
import com.jumarkot.tenant.domain.environment.TenantEnvironmentRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantEnvironmentJpaRepository
        extends JpaRepository<TenantEnvironment, UUID>, TenantEnvironmentRepository {

    List<TenantEnvironment> findByTenantId(UUID tenantId);

    Optional<TenantEnvironment> findByIdAndTenantId(UUID id, UUID tenantId);

    boolean existsByTenantIdAndType(UUID tenantId, EnvironmentType type);
}

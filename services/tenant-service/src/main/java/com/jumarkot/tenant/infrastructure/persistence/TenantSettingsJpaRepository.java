package com.jumarkot.tenant.infrastructure.persistence;

import com.jumarkot.tenant.domain.settings.TenantSettings;
import com.jumarkot.tenant.domain.settings.TenantSettingsRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantSettingsJpaRepository
        extends JpaRepository<TenantSettings, UUID>, TenantSettingsRepository {

    Optional<TenantSettings> findByTenantId(UUID tenantId);
}

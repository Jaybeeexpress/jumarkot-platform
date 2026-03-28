package com.jumarkot.tenant.domain.settings;

import java.util.Optional;
import java.util.UUID;

public interface TenantSettingsRepository {

    Optional<TenantSettings> findByTenantId(UUID tenantId);

    TenantSettings save(TenantSettings settings);
}

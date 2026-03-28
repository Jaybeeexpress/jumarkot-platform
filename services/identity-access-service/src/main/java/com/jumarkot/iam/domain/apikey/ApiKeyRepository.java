package com.jumarkot.iam.domain.apikey;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApiKeyRepository {

    Optional<ApiKey> findById(UUID id);

    Optional<ApiKey> findByKeyHash(String keyHash);

    List<ApiKey> findByTenantIdAndUserId(UUID tenantId, UUID userId);

    List<ApiKey> findByTenantId(UUID tenantId);

    ApiKey save(ApiKey apiKey);
}

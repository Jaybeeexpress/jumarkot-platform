package com.jumarkot.iam.infrastructure.persistence;

import com.jumarkot.iam.domain.apikey.ApiKey;
import com.jumarkot.iam.domain.apikey.ApiKeyRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApiKeyJpaRepository extends JpaRepository<ApiKey, UUID>, ApiKeyRepository {

    Optional<ApiKey> findByKeyHash(String keyHash);

    List<ApiKey> findByTenantIdAndUserId(UUID tenantId, UUID userId);

    List<ApiKey> findByTenantId(UUID tenantId);
}

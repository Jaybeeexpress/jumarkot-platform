package com.jumarkot.identity.repository;

import com.jumarkot.identity.domain.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
    Optional<ApiKey> findByKeyHashAndActiveTrue(String keyHash);
    List<ApiKey> findByTenantIdAndActiveTrue(UUID tenantId);
    List<ApiKey> findByKeyPrefixAndActiveTrue(String keyPrefix);
}

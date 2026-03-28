package com.jumarkot.tenant.domain.tenant;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface TenantRepository {

    Optional<Tenant> findById(UUID id);

    Optional<Tenant> findBySlug(String slug);

    boolean existsBySlug(String slug);

    Page<Tenant> findAll(Pageable pageable);

    Tenant save(Tenant tenant);
}

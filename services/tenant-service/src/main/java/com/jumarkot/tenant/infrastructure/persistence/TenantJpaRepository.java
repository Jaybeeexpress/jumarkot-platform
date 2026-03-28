package com.jumarkot.tenant.infrastructure.persistence;

import com.jumarkot.tenant.domain.tenant.Tenant;
import com.jumarkot.tenant.domain.tenant.TenantRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantJpaRepository extends JpaRepository<Tenant, UUID>, TenantRepository {

    Optional<Tenant> findBySlug(String slug);

    boolean existsBySlug(String slug);
}

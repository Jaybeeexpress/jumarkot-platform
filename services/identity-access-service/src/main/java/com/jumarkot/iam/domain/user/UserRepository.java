package com.jumarkot.iam.domain.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    Optional<User> findById(UUID id);

    Optional<User> findByEmailAndTenantId(String email, UUID tenantId);

    List<User> findByTenantId(UUID tenantId);

    boolean existsByEmailAndTenantId(String email, UUID tenantId);

    User save(User user);
}

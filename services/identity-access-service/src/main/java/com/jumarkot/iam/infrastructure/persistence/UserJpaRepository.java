package com.jumarkot.iam.infrastructure.persistence;

import com.jumarkot.iam.domain.user.User;
import com.jumarkot.iam.domain.user.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<User, UUID>, UserRepository {

    Optional<User> findByEmailAndTenantId(String email, UUID tenantId);

    List<User> findByTenantId(UUID tenantId);

    boolean existsByEmailAndTenantId(String email, UUID tenantId);
}

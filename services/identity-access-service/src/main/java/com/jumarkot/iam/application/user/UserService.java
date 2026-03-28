package com.jumarkot.iam.application.user;

import com.jumarkot.iam.domain.user.User;
import com.jumarkot.iam.domain.user.UserRepository;
import com.jumarkot.iam.domain.user.UserRole;
import com.jumarkot.iam.domain.user.UserStatus;
import com.jumarkot.iam.web.user.CreateUserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(UUID tenantId, CreateUserRequest request) {
        if (userRepository.existsByEmailAndTenantId(request.getEmail(), tenantId)) {
            throw new IllegalArgumentException(
                    "A user with email '" + request.getEmail() + "' already exists in this tenant");
        }
        Set<UserRole> roles = (request.getRoles() != null && !request.getRoles().isEmpty())
                ? request.getRoles()
                : Set.of(UserRole.VIEWER);

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .tenantId(tenantId)
                .roles(roles)
                .status(UserStatus.ACTIVE)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();

        User saved = userRepository.save(user);
        log.info("Created user {} ({}) in tenant {}", saved.getId(), saved.getEmail(), tenantId);
        return saved;
    }

    @Transactional(readOnly = true)
    public User getUser(UUID tenantId, UUID userId) {
        return userRepository.findById(userId)
                .filter(u -> u.getTenantId().equals(tenantId))
                .orElseThrow(() -> new NoSuchElementException(
                        "User " + userId + " not found in tenant " + tenantId));
    }

    @Transactional(readOnly = true)
    public List<User> listUsers(UUID tenantId) {
        return userRepository.findByTenantId(tenantId);
    }

    @Transactional
    public User updateUser(UUID tenantId, UUID userId, CreateUserRequest request) {
        User user = getUser(tenantId, userId);
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            user.setRoles(request.getRoles());
        }
        return userRepository.save(user);
    }

    @Transactional
    public void deactivateUser(UUID tenantId, UUID userId) {
        User user = getUser(tenantId, userId);
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
        log.info("Deactivated user {} in tenant {}", userId, tenantId);
    }
}

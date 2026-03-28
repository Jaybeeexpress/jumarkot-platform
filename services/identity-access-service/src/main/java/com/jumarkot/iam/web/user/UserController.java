package com.jumarkot.iam.web.user;

import com.jumarkot.iam.application.user.UserService;
import com.jumarkot.iam.domain.user.User;
import com.jumarkot.iam.infrastructure.security.JwtAuthenticationFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management within a tenant")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "List all users in the authenticated tenant")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'ANALYST')")
    public ResponseEntity<List<User>> listUsers(Authentication authentication) {
        UUID tenantId = extractTenantId(authentication);
        return ResponseEntity.ok(userService.listUsers(tenantId));
    }

    @PostMapping
    @Operation(summary = "Create a new user in the authenticated tenant")
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    public ResponseEntity<User> createUser(
            @Valid @RequestBody CreateUserRequest request,
            Authentication authentication) {
        UUID tenantId = extractTenantId(authentication);
        User created = userService.createUser(tenantId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a user by ID")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'ANALYST')")
    public ResponseEntity<User> getUser(@PathVariable UUID id, Authentication authentication) {
        UUID tenantId = extractTenantId(authentication);
        return ResponseEntity.ok(userService.getUser(tenantId, id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a user")
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    public ResponseEntity<User> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody CreateUserRequest request,
            Authentication authentication) {
        UUID tenantId = extractTenantId(authentication);
        return ResponseEntity.ok(userService.updateUser(tenantId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate a user")
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    public ResponseEntity<Void> deactivateUser(@PathVariable UUID id, Authentication authentication) {
        UUID tenantId = extractTenantId(authentication);
        userService.deactivateUser(tenantId, id);
        return ResponseEntity.noContent().build();
    }

    private UUID extractTenantId(Authentication authentication) {
        if (authentication.getPrincipal() instanceof JwtAuthenticationFilter.JwtPrincipal principal) {
            return principal.tenantId();
        }
        throw new IllegalStateException("Cannot extract tenant from authentication");
    }
}

package com.jumarkot.tenant.web;

import com.jumarkot.contracts.tenant.EnvironmentDto;
import com.jumarkot.tenant.application.TenantEnvironmentService;
import com.jumarkot.tenant.web.dto.CreateEnvironmentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/tenants/{tenantId}/environments")
@RequiredArgsConstructor
@Tag(name = "Environments", description = "Tenant environment management")
public class TenantEnvironmentController {

    private final TenantEnvironmentService environmentService;

    @GetMapping
    @Operation(summary = "List environments for a tenant")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'ANALYST', 'SERVICE_ACCOUNT')")
    public ResponseEntity<List<EnvironmentDto>> listEnvironments(@PathVariable UUID tenantId) {
        return ResponseEntity.ok(environmentService.listEnvironments(tenantId));
    }

    @PostMapping
    @Operation(summary = "Create a new environment for a tenant")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'SERVICE_ACCOUNT')")
    public ResponseEntity<EnvironmentDto> createEnvironment(
            @PathVariable UUID tenantId,
            @Valid @RequestBody CreateEnvironmentRequest request) {
        EnvironmentDto created = environmentService.createEnvironment(tenantId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getEnvironmentId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{envId}")
    @Operation(summary = "Get an environment by ID")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'ANALYST', 'SERVICE_ACCOUNT')")
    public ResponseEntity<EnvironmentDto> getEnvironment(
            @PathVariable UUID tenantId,
            @PathVariable UUID envId) {
        return ResponseEntity.ok(environmentService.getEnvironment(tenantId, envId));
    }

    @PutMapping("/{envId}")
    @Operation(summary = "Update an environment")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'SERVICE_ACCOUNT')")
    public ResponseEntity<EnvironmentDto> updateEnvironment(
            @PathVariable UUID tenantId,
            @PathVariable UUID envId,
            @Valid @RequestBody CreateEnvironmentRequest request) {
        return ResponseEntity.ok(environmentService.updateEnvironment(tenantId, envId, request));
    }
}

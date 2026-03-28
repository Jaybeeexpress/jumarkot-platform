package com.jumarkot.tenant.web;

import com.jumarkot.contracts.common.PageResponse;
import com.jumarkot.contracts.tenant.TenantDto;
import com.jumarkot.tenant.application.TenantService;
import com.jumarkot.tenant.web.dto.CreateTenantRequest;
import com.jumarkot.tenant.web.dto.UpdateTenantRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/v1/tenants")
@RequiredArgsConstructor
@Tag(name = "Tenants", description = "Tenant lifecycle management")
public class TenantController {

    private final TenantService tenantService;

    @GetMapping
    @Operation(summary = "List all tenants")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'SERVICE_ACCOUNT')")
    public ResponseEntity<PageResponse<TenantDto>> listTenants(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(tenantService.listTenants(pageable));
    }

    @PostMapping
    @Operation(summary = "Create a new tenant")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'SERVICE_ACCOUNT')")
    public ResponseEntity<TenantDto> createTenant(@Valid @RequestBody CreateTenantRequest request) {
        TenantDto created = tenantService.createTenant(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getTenantId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a tenant by ID")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'ANALYST', 'SERVICE_ACCOUNT')")
    public ResponseEntity<TenantDto> getTenant(@PathVariable UUID id) {
        return ResponseEntity.ok(tenantService.getTenant(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a tenant")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'SERVICE_ACCOUNT')")
    public ResponseEntity<TenantDto> updateTenant(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTenantRequest request) {
        return ResponseEntity.ok(tenantService.updateTenant(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft-delete a tenant")
    @PreAuthorize("hasAuthority('SERVICE_ACCOUNT')")
    public ResponseEntity<Void> deleteTenant(@PathVariable UUID id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/suspend")
    @Operation(summary = "Suspend a tenant")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'SERVICE_ACCOUNT')")
    public ResponseEntity<TenantDto> suspendTenant(@PathVariable UUID id) {
        return ResponseEntity.ok(tenantService.suspendTenant(id));
    }
}

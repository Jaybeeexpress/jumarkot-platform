package com.jumarkot.tenant.controller;

import com.jumarkot.contracts.common.ApiResponse;
import com.jumarkot.contracts.tenant.TenantCreateRequest;
import com.jumarkot.contracts.tenant.TenantResponse;
import com.jumarkot.tenant.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @PostMapping
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<ApiResponse<TenantResponse>> create(@Valid @RequestBody TenantCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(tenantService.create(request)));
    }

    @GetMapping("/{tenantId}")
    public ResponseEntity<ApiResponse<TenantResponse>> get(@PathVariable UUID tenantId) {
        return ResponseEntity.ok(ApiResponse.ok(tenantService.getById(tenantId)));
    }

    @GetMapping
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<ApiResponse<List<TenantResponse>>> list() {
        return ResponseEntity.ok(ApiResponse.ok(tenantService.listAll()));
    }

    @PostMapping("/{tenantId}/suspend")
    @PreAuthorize("hasRole('PLATFORM_ADMIN')")
    public ResponseEntity<Void> suspend(@PathVariable UUID tenantId) {
        tenantService.suspend(tenantId);
        return ResponseEntity.noContent().build();
    }
}

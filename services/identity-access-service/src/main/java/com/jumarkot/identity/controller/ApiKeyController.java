package com.jumarkot.identity.controller;

import com.jumarkot.contracts.common.ApiResponse;
import com.jumarkot.contracts.identity.ApiKeyCreateRequest;
import com.jumarkot.contracts.identity.ApiKeyResponse;
import com.jumarkot.identity.service.ApiKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/tenants/{tenantId}/api-keys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TENANT_OWNER')")
    public ResponseEntity<ApiResponse<ApiKeyResponse>> create(
            @PathVariable UUID tenantId,
            @Valid @RequestBody ApiKeyCreateRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(apiKeyService.create(tenantId, request, principal.getUsername())));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TENANT_OWNER')")
    public ResponseEntity<ApiResponse<List<ApiKeyResponse>>> list(@PathVariable UUID tenantId) {
        return ResponseEntity.ok(ApiResponse.ok(apiKeyService.listForTenant(tenantId)));
    }

    @DeleteMapping("/{keyId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TENANT_OWNER')")
    public ResponseEntity<Void> revoke(@PathVariable UUID tenantId, @PathVariable UUID keyId) {
        apiKeyService.revoke(tenantId, keyId);
        return ResponseEntity.noContent().build();
    }
}

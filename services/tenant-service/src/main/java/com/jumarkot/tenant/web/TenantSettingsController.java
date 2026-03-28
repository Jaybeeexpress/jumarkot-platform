package com.jumarkot.tenant.web;

import com.jumarkot.tenant.application.TenantSettingsService;
import com.jumarkot.tenant.web.dto.TenantSettingsDto;
import com.jumarkot.tenant.web.dto.UpdateTenantSettingsRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/tenants/{tenantId}/settings")
@RequiredArgsConstructor
@Tag(name = "Tenant Settings", description = "Tenant-level configuration management")
public class TenantSettingsController {

    private final TenantSettingsService settingsService;

    @GetMapping
    @Operation(summary = "Get settings for a tenant")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'SERVICE_ACCOUNT')")
    public ResponseEntity<TenantSettingsDto> getSettings(@PathVariable UUID tenantId) {
        return ResponseEntity.ok(settingsService.getSettings(tenantId));
    }

    @PutMapping
    @Operation(summary = "Update settings for a tenant")
    @PreAuthorize("hasAnyAuthority('TENANT_ADMIN', 'SERVICE_ACCOUNT')")
    public ResponseEntity<TenantSettingsDto> updateSettings(
            @PathVariable UUID tenantId,
            @Valid @RequestBody UpdateTenantSettingsRequest request) {
        return ResponseEntity.ok(settingsService.updateSettings(tenantId, request));
    }

    @PostMapping("/rotate-webhook-secret")
    @Operation(summary = "Rotate the webhook signing secret for a tenant")
    @PreAuthorize("hasAuthority('TENANT_ADMIN')")
    public ResponseEntity<Map<String, String>> rotateWebhookSecret(@PathVariable UUID tenantId) {
        String newSecret = settingsService.rotateWebhookSecret(tenantId);
        return ResponseEntity.ok(Map.of("webhookSigningSecret", newSecret));
    }
}

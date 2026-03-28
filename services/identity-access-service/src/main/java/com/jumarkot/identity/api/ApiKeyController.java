package com.jumarkot.identity.api;

import com.jumarkot.contracts.common.ApiResponse;
import com.jumarkot.identity.domain.ApiKey;
import com.jumarkot.identity.service.ApiKeyService;
import com.jumarkot.shared.auth.TenantContext;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/api-keys")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    public ApiKeyController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CreateApiKeyResponse> create(@Valid @RequestBody CreateApiKeyRequest req) {
        ApiKeyService.CreateApiKeyResult result = apiKeyService.create(
                req.tenantId(),
                req.environmentId(),
                req.environmentType(),
                req.name(),
                req.scopes()
        );
        return ApiResponse.ok(new CreateApiKeyResponse(
                result.keyId(),
                result.rawKey(),     // Only returned here — impossible to recover later
                result.keyPrefix()
        ));
    }

    @DeleteMapping("/{keyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revoke(@PathVariable UUID keyId,
                       @RequestParam UUID tenantId,
                       @RequestParam(defaultValue = "Revoked via API") String reason) {
        apiKeyService.revoke(keyId, tenantId, reason);
    }

    @GetMapping
    public ApiResponse<List<ApiKeySummary>> list(@RequestParam UUID tenantId) {
        List<ApiKeySummary> summaries = apiKeyService.listForTenant(tenantId).stream()
                .map(this::toSummary)
                .toList();
        return ApiResponse.ok(summaries);
    }

    private ApiKeySummary toSummary(ApiKey k) {
        return new ApiKeySummary(k.id(), k.keyPrefix() + "***", k.name(),
                k.environmentType(), k.scopes(), k.active(), k.createdAt());
    }

    // ─── Request / Response records ──────────────────────────────────────────

    public record CreateApiKeyRequest(
            @NotNull UUID tenantId,
            @NotNull UUID environmentId,
            @NotNull TenantContext.EnvironmentType environmentType,
            @NotBlank @Size(max = 255) String name,
            @NotNull List<String> scopes
    ) {}

    public record CreateApiKeyResponse(
            UUID keyId,
            String key,           // Full raw key — show once, never again
            String keyPrefix
    ) {}

    public record ApiKeySummary(
            UUID id,
            String maskedKey,
            String name,
            String environmentType,
            List<String> scopes,
            boolean active,
            java.time.OffsetDateTime createdAt
    ) {}
}

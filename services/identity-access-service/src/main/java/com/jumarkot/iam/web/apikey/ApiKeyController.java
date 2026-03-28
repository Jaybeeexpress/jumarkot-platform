package com.jumarkot.iam.web.apikey;

import com.jumarkot.iam.application.apikey.ApiKeyService;
import com.jumarkot.iam.domain.apikey.ApiKey;
import com.jumarkot.iam.infrastructure.security.JwtAuthenticationFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/api-keys")
@RequiredArgsConstructor
@Tag(name = "API Keys", description = "API key management for service-to-service authentication")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @GetMapping
    @Operation(summary = "List API keys for the authenticated user's tenant")
    public ResponseEntity<List<ApiKey>> listApiKeys(Authentication authentication) {
        JwtAuthenticationFilter.JwtPrincipal principal = extractPrincipal(authentication);
        List<ApiKey> keys = apiKeyService.listApiKeys(principal.tenantId(), principal.userId());
        return ResponseEntity.ok(keys);
    }

    @PostMapping
    @Operation(summary = "Generate a new API key")
    public ResponseEntity<ApiKeyCreatedResponse> createApiKey(
            @Valid @RequestBody CreateApiKeyRequest request,
            Authentication authentication) {
        JwtAuthenticationFilter.JwtPrincipal principal = extractPrincipal(authentication);
        ApiKeyCreatedResponse response = apiKeyService.generateApiKey(principal.tenantId(), principal.userId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Revoke an API key")
    public ResponseEntity<Void> revokeApiKey(@PathVariable UUID id, Authentication authentication) {
        JwtAuthenticationFilter.JwtPrincipal principal = extractPrincipal(authentication);
        apiKeyService.revokeApiKey(principal.tenantId(), id);
        return ResponseEntity.noContent().build();
    }

    private JwtAuthenticationFilter.JwtPrincipal extractPrincipal(Authentication authentication) {
        if (authentication.getPrincipal() instanceof JwtAuthenticationFilter.JwtPrincipal principal) {
            return principal;
        }
        throw new IllegalStateException("Cannot extract principal from authentication");
    }
}

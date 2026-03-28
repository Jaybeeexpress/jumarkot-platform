package com.jumarkot.tenant.api;

import com.jumarkot.contracts.common.ApiResponse;
import com.jumarkot.tenant.domain.Tenant;
import com.jumarkot.tenant.domain.TenantEnvironment;
import com.jumarkot.tenant.service.TenantService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/tenants")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Tenant> provision(@Valid @RequestBody ProvisionTenantRequest req) {
        return ApiResponse.ok(
                tenantService.provision(req.slug(), req.name(), req.plan(), req.contactEmail()));
    }

    @GetMapping("/{tenantId}")
    public ApiResponse<Tenant> get(@PathVariable UUID tenantId) {
        return ApiResponse.ok(tenantService.findById(tenantId));
    }

    @GetMapping("/{tenantId}/environments")
    public ApiResponse<List<TenantEnvironment>> getEnvironments(@PathVariable UUID tenantId) {
        return ApiResponse.ok(tenantService.getEnvironments(tenantId));
    }

    public record ProvisionTenantRequest(
            @NotBlank @Pattern(regexp = "^[a-z0-9-]{3,63}$",
                    message = "slug must be 3-63 lowercase alphanumeric characters or hyphens")
            String slug,

            @NotBlank String name,

            @NotBlank String plan,

            @Email @NotBlank String contactEmail
    ) {}
}

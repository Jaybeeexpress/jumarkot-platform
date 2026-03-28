package com.jumarkot.tenant.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jumarkot.tenant.domain.tenant.TenantPlan;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateTenantRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Slug is required")
    @Size(max = 63, message = "Slug must not exceed 63 characters")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug may only contain lowercase letters, digits and hyphens")
    private String slug;

    @NotNull(message = "Plan is required")
    private TenantPlan plan;

    @NotBlank(message = "Region is required")
    private String region;

    @NotBlank(message = "Owner email is required")
    @Email(message = "Owner email must be a valid email address")
    private String ownerEmail;
}

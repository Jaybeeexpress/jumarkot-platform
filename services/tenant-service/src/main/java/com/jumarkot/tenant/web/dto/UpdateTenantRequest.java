package com.jumarkot.tenant.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jumarkot.tenant.domain.tenant.TenantPlan;
import com.jumarkot.tenant.domain.tenant.TenantStatus;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateTenantRequest {

    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    private TenantPlan plan;

    private TenantStatus status;

    private String region;
}

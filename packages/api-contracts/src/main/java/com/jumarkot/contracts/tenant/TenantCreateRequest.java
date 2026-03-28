package com.jumarkot.contracts.tenant;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TenantCreateRequest {
    @NotBlank
    String name;

    @NotBlank
    String slug;

    String plan;
    String region;
    String contactEmail;
}

package com.jumarkot.tenant.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TenantSettingsDto {

    private String tenantId;
    private String timezone;
    private String defaultCurrency;
    private BigDecimal riskThreshold;
    private Instant updatedAt;
}

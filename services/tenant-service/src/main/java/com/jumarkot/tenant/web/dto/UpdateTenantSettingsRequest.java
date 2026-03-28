package com.jumarkot.tenant.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

@Data
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateTenantSettingsRequest {

    @Size(max = 50, message = "Timezone must not exceed 50 characters")
    private String timezone;

    @Size(min = 3, max = 3, message = "Currency code must be exactly 3 characters")
    private String defaultCurrency;

    @DecimalMin(value = "0.0", message = "Risk threshold must be at least 0.0")
    @DecimalMax(value = "1.0", message = "Risk threshold must be at most 1.0")
    @Digits(integer = 1, fraction = 4, message = "Risk threshold must have at most 1 integer digit and 4 fraction digits")
    private BigDecimal riskThreshold;
}

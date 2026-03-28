package com.jumarkot.tenant.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jumarkot.tenant.domain.environment.EnvironmentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateEnvironmentRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Environment type is required")
    private EnvironmentType type;

    @Size(max = 500, message = "API base URL must not exceed 500 characters")
    private String apiBaseUrl;

    @Size(max = 500, message = "Webhook URL must not exceed 500 characters")
    private String webhookUrl;
}

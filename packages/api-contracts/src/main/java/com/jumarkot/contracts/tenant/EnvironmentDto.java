package com.jumarkot.contracts.tenant;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/**
 * Data transfer object representing a single tenant environment (e.g. production or sandbox).
 *
 * <p>Each environment has its own isolated event stream, decision history, API key set,
 * and rule configuration, while sharing the parent tenant's subscription and billing.
 */
@Data
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnvironmentDto {

    /**
     * Platform-assigned unique identifier for this environment (UUID v4).
     */
    @NotBlank(message = "environmentId must not be blank")
    private final String environmentId;

    /**
     * Human-readable display name for the environment, e.g. {@code "Production"},
     * {@code "Staging"}, {@code "QA Sandbox"}.
     */
    @NotBlank(message = "name must not be blank")
    @Size(max = 100, message = "name must not exceed 100 characters")
    private final String name;

    /**
     * Deployment tier of this environment.
     */
    @NotNull(message = "type must not be null")
    private final EnvironmentType type;

    /**
     * Whether this environment is currently active and able to accept traffic.
     * Inactive environments reject API requests with HTTP 403.
     */
    private final boolean active;
}

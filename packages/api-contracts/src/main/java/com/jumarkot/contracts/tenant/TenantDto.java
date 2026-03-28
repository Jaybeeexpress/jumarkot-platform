package com.jumarkot.contracts.tenant;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/**
 * Data transfer object representing a Jumarkot platform tenant.
 *
 * <p>A tenant is the top-level organisational unit in Jumarkot. Each tenant maps to a
 * single business customer and may contain multiple {@link EnvironmentDto environments}
 * (e.g. production + sandbox).
 */
@Data
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TenantDto {

    /**
     * Platform-assigned globally unique tenant identifier (UUID v4).
     */
    @NotBlank(message = "tenantId must not be blank")
    private final String tenantId;

    /**
     * Unique, URL-safe slug identifying the tenant across API paths and Kafka topics,
     * e.g. {@code "acme-bank"}, {@code "fintech-xyz"}.
     */
    @NotBlank(message = "slug must not be blank")
    @Size(max = 63, message = "slug must not exceed 63 characters")
    private final String slug;

    /**
     * Human-readable display name of the tenant organisation.
     */
    @NotBlank(message = "name must not be blank")
    @Size(max = 255, message = "name must not exceed 255 characters")
    private final String name;

    /**
     * Primary technical contact email address for the tenant.
     */
    @Email(message = "contactEmail must be a valid email address")
    private final String contactEmail;

    /**
     * Current lifecycle status of the tenant account.
     */
    @NotNull(message = "status must not be null")
    private final TenantStatus status;

    /**
     * Ordered list of environments belonging to this tenant.
     * Always contains at least one {@link EnvironmentType#PRODUCTION} environment.
     */
    @Valid
    private final List<EnvironmentDto> environments;

    /**
     * UTC timestamp at which the tenant account was created.
     */
    private final Instant createdAt;

    /**
     * UTC timestamp of the most recent modification to this tenant record.
     */
    private final Instant updatedAt;

    // ── Status enum ──────────────────────────────────────────────────────────

    /**
     * Lifecycle status of a tenant account.
     */
    public enum TenantStatus {
        /** Account is active and fully operational. */
        ACTIVE,
        /** Account is pending email/KYB verification. */
        PENDING_VERIFICATION,
        /** Account has been administratively suspended; API access is blocked. */
        SUSPENDED,
        /** Account has been permanently deactivated; data is in retention period. */
        DEACTIVATED
    }
}

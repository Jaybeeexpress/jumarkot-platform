package com.jumarkot.contracts.decision;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/**
 * Request payload for triggering a risk/fraud decision evaluation.
 *
 * <p>A decision request captures the event type, the entity under evaluation (e.g. a user,
 * account, or transaction), and arbitrary contextual signals. The platform routes this request
 * to the appropriate rule-sets and ML models based on {@code eventType} and the tenant's
 * active rule configuration.
 */
@Data
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DecisionRequest {

    /**
     * The type of event triggering the decision, e.g. {@code "LOGIN"}, {@code "PAYMENT"},
     * {@code "ONBOARDING"}. Must match a registered event type for the tenant.
     */
    @NotBlank(message = "eventType must not be blank")
    @Size(max = 100, message = "eventType must not exceed 100 characters")
    private final String eventType;

    /**
     * Stable identifier for the entity being evaluated (user ID, account ID, device ID, etc.).
     * Used for velocity checks and historical enrichment.
     */
    @NotBlank(message = "entityId must not be blank")
    @Size(max = 256, message = "entityId must not exceed 256 characters")
    private final String entityId;

    /**
     * The entity type qualifier, e.g. {@code "USER"}, {@code "ACCOUNT"}, {@code "DEVICE"}.
     * Helps the decision engine select the correct feature-set extractors.
     */
    @NotBlank(message = "entityType must not be blank")
    @Size(max = 64, message = "entityType must not exceed 64 characters")
    private final String entityType;

    /**
     * Free-form key-value context map carrying event-specific signals.
     * Values may be strings, numbers, booleans, or nested structures serialised as strings.
     * Maximum 500 entries; individual keys limited to 128 characters.
     */
    @NotNull(message = "context must not be null")
    @Size(max = 500, message = "context map must not exceed 500 entries")
    private final Map<String, Object> context;

    /**
     * Optional idempotency key supplied by the caller. When provided, the platform will
     * return the cached decision for duplicate requests received within the TTL window.
     */
    @Size(max = 128, message = "idempotencyKey must not exceed 128 characters")
    private final String idempotencyKey;
}

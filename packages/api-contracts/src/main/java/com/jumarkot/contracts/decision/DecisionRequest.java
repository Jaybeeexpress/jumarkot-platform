package com.jumarkot.contracts.decision;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Inbound request to the decision engine.
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DecisionRequest {

    /** Idempotency key supplied by the caller. */
    @NotBlank
    String idempotencyKey;

    /** Tenant that owns this request. */
    @NotBlank
    String tenantId;

    /** Entity (user / account / device) being evaluated. */
    @NotBlank
    String entityId;

    /** Type of event triggering the decision (e.g. PAYMENT, LOGIN, ONBOARDING). */
    @NotNull
    DecisionEventType eventType;

    /** Arbitrary context payload – schema varies per eventType. */
    @NotNull
    Map<String, Object> context;

    Instant occurredAt;
}

package com.jumarkot.contracts.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Map;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/**
 * Request payload for ingesting a raw event into the Jumarkot event pipeline.
 *
 * <p>Ingested events are validated against the registered JSON Schema for their
 * {@code eventType}, persisted durably, published to Kafka, and made available
 * to downstream processors (decision engine, rule evaluator, ML feature store).
 */
@Data
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IngestEventRequest {

    /**
     * The canonical event type identifier, e.g. {@code "transaction.completed"},
     * {@code "user.login"}, {@code "user.onboarding.initiated"}.
     * Must match a schema registered in the event-schemas catalog.
     */
    @NotBlank(message = "eventType must not be blank")
    @Size(max = 100, message = "eventType must not exceed 100 characters")
    private final String eventType;

    /**
     * Identifier of the entity that is the subject of this event
     * (user ID, account ID, device fingerprint, etc.).
     */
    @NotBlank(message = "entityId must not be blank")
    @Size(max = 256, message = "entityId must not exceed 256 characters")
    private final String entityId;

    /**
     * Entity type qualifier to disambiguate {@code entityId} across namespaces,
     * e.g. {@code "USER"}, {@code "ACCOUNT"}, {@code "MERCHANT"}.
     */
    @NotBlank(message = "entityType must not be blank")
    @Size(max = 64, message = "entityType must not exceed 64 characters")
    private final String entityType;

    /**
     * Client-supplied idempotency key. Duplicate ingest requests carrying the same key
     * within the deduplication window will be acknowledged but not re-processed.
     * Strongly recommended for at-least-once producers.
     */
    @Size(max = 128, message = "idempotencyKey must not exceed 128 characters")
    private final String idempotencyKey;

    /**
     * Event-specific payload conforming to the JSON Schema for {@code eventType}.
     * Must not be null; may be empty for event types that carry no domain payload.
     */
    @NotNull(message = "payload must not be null")
    private final Map<String, Object> payload;
}

package com.jumarkot.contracts.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/**
 * Response returned by the event ingestion endpoint after an event has been accepted.
 *
 * <p>A successful response guarantees that the event has been durably written to the
 * event store and published to the Kafka topic for downstream processing.
 */
@Data
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IngestEventResponse {

    /**
     * Platform-assigned globally unique identifier for the ingested event.
     * Format: {@code UUID v7} (time-ordered for efficient index locality).
     */
    private final String eventId;

    /**
     * Processing status of the ingested event.
     */
    private final IngestStatus status;

    /**
     * UTC timestamp at which the event was accepted and persisted by the ingestion service.
     */
    private final Instant receivedAt;

    /**
     * The idempotency key echoed back from the original request, if one was supplied.
     * Clients can use this to correlate duplicate submissions.
     */
    private final String idempotencyKey;

    // ── Status enum ──────────────────────────────────────────────────────────

    /**
     * Describes the outcome of the ingest operation.
     */
    public enum IngestStatus {

        /** Event was accepted, persisted, and published for the first time. */
        ACCEPTED,

        /** A duplicate was detected via idempotency key; the original event ID is returned. */
        DUPLICATE,

        /** Event was accepted but schema validation produced non-fatal warnings. */
        ACCEPTED_WITH_WARNINGS
    }
}

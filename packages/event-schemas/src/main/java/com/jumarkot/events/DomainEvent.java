package com.jumarkot.events;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.Map;

/**
 * Envelope wrapping all domain events published to Kafka.
 */
@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DomainEvent {

    /** Unique event identifier (UUID). */
    String eventId;

    /** Kafka topic this event targets. */
    String topic;

    /** Logical event type (e.g. "decision.completed"). */
    String eventType;

    /** Schema version of this event payload. */
    String schemaVersion;

    String tenantId;
    String entityId;

    /** Serialised event payload. */
    Map<String, Object> payload;

    Instant occurredAt;
    Instant publishedAt;
}

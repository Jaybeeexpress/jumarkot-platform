package com.jumarkot.ingestion.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "ingested_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngestedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "entity_id", nullable = false)
    private String entityId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "schema_version", nullable = false)
    private String schemaVersion;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> payload;

    @Column(nullable = false)
    private String status;

    @Column(name = "kafka_topic")
    private String kafkaTopic;

    @Column(name = "kafka_offset")
    private Long kafkaOffset;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @CreationTimestamp
    @Column(name = "ingested_at", nullable = false, updatable = false)
    private Instant ingestedAt;
}

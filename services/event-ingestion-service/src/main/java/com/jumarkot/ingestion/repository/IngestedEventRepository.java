package com.jumarkot.ingestion.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumarkot.ingestion.domain.IngestedEvent;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public class IngestedEventRepository {

    private final DSLContext dsl;
    private final ObjectMapper objectMapper;

    public IngestedEventRepository(DSLContext dsl, ObjectMapper objectMapper) {
        this.dsl = dsl;
        this.objectMapper = objectMapper;
    }

    public Optional<EventReceipt> findByTenantIdAndIdempotencyKey(UUID tenantId, String idempotencyKey) {
        return dsl.select()
                .from(DSL.table("ingested_events"))
                .where(DSL.field("tenant_id").eq(tenantId))
                .and(DSL.field("idempotency_key").eq(idempotencyKey))
                .fetchOptional(record -> new EventReceipt(
                        record.get("id", UUID.class),
                        record.get("ingestion_status", String.class),
                        record.get("created_at", Instant.class)
                ));
    }

    public void insert(IngestedEvent event) {
        try {
            dsl.insertInto(DSL.table("ingested_events"))
                    .set(DSL.field("id"), event.id())
                    .set(DSL.field("tenant_id"), event.tenantId())
                    .set(DSL.field("environment_id"), event.environmentId())
                    .set(DSL.field("environment_type"), event.environmentType())
                    .set(DSL.field("api_key_id"), event.apiKeyId())
                    .set(DSL.field("idempotency_key"), event.idempotencyKey())
                    .set(DSL.field("event_type"), event.eventType())
                    .set(DSL.field("entity_id"), event.entityId())
                    .set(DSL.field("entity_type"), event.entityType())
                    .set(DSL.field("occurred_at"), event.occurredAt())
                    .set(DSL.field("properties", JSONB.class),
                            JSONB.valueOf(objectMapper.writeValueAsString(event.properties() == null ? java.util.Map.of() : event.properties())))
                    .set(DSL.field("ip_address"), event.ipAddress())
                    .set(DSL.field("device_id"), event.deviceId())
                    .set(DSL.field("session_id"), event.sessionId())
                    .set(DSL.field("user_agent"), event.userAgent())
                    .set(DSL.field("ingestion_status"), event.ingestionStatus())
                    .set(DSL.field("created_at"), event.createdAt())
                    .execute();
        } catch (JsonProcessingException exception) {
            throw new RuntimeException("Failed to serialize event properties", exception);
        }
    }

    public record EventReceipt(UUID eventId, String status, Instant createdAt) {}
}
package com.jumarkot.entity.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumarkot.entity.domain.EntityProfileSnapshot;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.springframework.stereotype.Repository;

@Repository
public class EntityProfileRepository {

    private final DSLContext dsl;
    private final ObjectMapper objectMapper;

    public EntityProfileRepository(DSLContext dsl, ObjectMapper objectMapper) {
        this.dsl = dsl;
        this.objectMapper = objectMapper;
    }

    public void upsert(EntityProfileSnapshot snapshot) {
        try {
            dsl.execute(
                    """
                    INSERT INTO entity_profiles (
                      tenant_id,
                      environment_type,
                      entity_type,
                      entity_id,
                      latest_event_type,
                      latest_event_at,
                      last_event_id,
                      last_idempotency_key,
                      latest_properties,
                      event_count,
                      first_seen_at,
                      last_seen_at,
                      updated_at
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, CAST(? AS jsonb), ?, ?, ?, ?)
                    ON CONFLICT (tenant_id, entity_type, entity_id)
                    DO UPDATE SET
                      environment_type = EXCLUDED.environment_type,
                      latest_event_type = EXCLUDED.latest_event_type,
                      latest_event_at = EXCLUDED.latest_event_at,
                      last_event_id = EXCLUDED.last_event_id,
                      last_idempotency_key = EXCLUDED.last_idempotency_key,
                      latest_properties = EXCLUDED.latest_properties,
                      event_count = entity_profiles.event_count + 1,
                      first_seen_at = LEAST(entity_profiles.first_seen_at, EXCLUDED.first_seen_at),
                      last_seen_at = EXCLUDED.last_seen_at,
                      updated_at = EXCLUDED.updated_at
                    """,
                    snapshot.tenantId(),
                    snapshot.environmentType(),
                    snapshot.entityType(),
                    snapshot.entityId(),
                    snapshot.latestEventType(),
                    snapshot.latestEventAt(),
                    snapshot.lastEventId(),
                    snapshot.lastIdempotencyKey(),
                    objectMapper.writeValueAsString(snapshot.latestProperties() == null ? java.util.Map.of() : snapshot.latestProperties()),
                    1,
                    snapshot.firstSeenAt(),
                    snapshot.lastSeenAt(),
                    snapshot.updatedAt()
            );
        } catch (JsonProcessingException exception) {
            throw new RuntimeException("Failed to serialize entity profile properties", exception);
        }
    }
}

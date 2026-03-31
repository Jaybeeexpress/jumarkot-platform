package com.jumarkot.entity.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumarkot.entity.domain.EntityProfileSnapshot;
import com.jumarkot.entity.repository.EntityProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class EntityEventProjectionService {

    private static final Logger log = LoggerFactory.getLogger(EntityEventProjectionService.class);

    private final EntityProfileRepository repository;
    private final ObjectMapper objectMapper;

    public EntityEventProjectionService(EntityProfileRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public void project(String message) {
        try {
            IngestedEventMessage event = objectMapper.readValue(message, IngestedEventMessage.class);
            Instant eventTime = parseInstant(event.occurredAt(), event.acceptedAt());
            Instant acceptedAt = parseInstant(event.acceptedAt(), event.occurredAt());
            repository.upsert(new EntityProfileSnapshot(
                    UUID.fromString(event.tenantId()),
                    event.environmentType(),
                    event.entityType(),
                    event.entityId(),
                    event.eventName(),
                    eventTime,
                    UUID.fromString(event.eventId()),
                    event.idempotencyKey(),
                    event.properties() == null ? Map.of() : event.properties(),
                    acceptedAt,
                    acceptedAt,
                    Instant.now()
            ));
        } catch (IOException | IllegalArgumentException exception) {
            log.error("Failed to project ingested event into entity profile", exception);
        }
    }

    private Instant parseInstant(String primary, String fallback) {
        if (primary != null && !primary.isBlank()) {
            return Instant.parse(primary);
        }
        if (fallback != null && !fallback.isBlank()) {
            return Instant.parse(fallback);
        }
        return Instant.now();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record IngestedEventMessage(
            String eventId,
            String tenantId,
            String environmentType,
            String idempotencyKey,
            String eventName,
            String entityId,
            String entityType,
            String occurredAt,
            String acceptedAt,
            Map<String, Object> properties
    ) {}
}

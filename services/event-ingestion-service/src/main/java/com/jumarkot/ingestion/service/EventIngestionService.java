package com.jumarkot.ingestion.service;

import com.jumarkot.contracts.event.EventPayload;
import com.jumarkot.ingestion.domain.IngestedEvent;
import com.jumarkot.ingestion.repository.IngestedEventRepository;
import com.jumarkot.shared.auth.TenantContext;
import com.jumarkot.shared.auth.TenantContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class EventIngestionService {

    private final IngestedEventRepository repository;

    public EventIngestionService(IngestedEventRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public IngestionResult ingest(EventPayload payload) {
        TenantContext tenantContext = TenantContextHolder.require();

        return repository.findByTenantIdAndIdempotencyKey(tenantContext.tenantId(), payload.idempotencyKey())
                .map(existing -> new IngestionResult(
                        existing.eventId(),
                        existing.status(),
                        existing.createdAt(),
                        true
                ))
                .orElseGet(() -> createEvent(payload, tenantContext));
    }

    private IngestionResult createEvent(EventPayload payload, TenantContext tenantContext) {
        Instant now = Instant.now();
        IngestedEvent event = new IngestedEvent(
                UUID.randomUUID(),
                tenantContext.tenantId(),
                tenantContext.environmentId(),
                tenantContext.environmentType().name(),
                tenantContext.apiKeyId(),
                payload.idempotencyKey(),
                payload.eventType().name(),
                payload.entityId(),
                payload.entityType(),
                payload.occurredAt() == null ? now : payload.occurredAt(),
                payload.properties(),
                payload.ipAddress(),
                payload.deviceId(),
                payload.sessionId(),
                payload.userAgent(),
                "ACCEPTED",
                now
        );
        repository.insert(event);
        return new IngestionResult(event.id(), event.ingestionStatus(), event.createdAt(), false);
    }

    public record IngestionResult(UUID eventId, String status, Instant acceptedAt, boolean duplicate) {}
}
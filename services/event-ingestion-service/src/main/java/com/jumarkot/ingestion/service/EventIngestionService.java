package com.jumarkot.ingestion.service;

import com.jumarkot.contracts.event.EventPayload;
import com.jumarkot.ingestion.domain.IngestedEvent;
import com.jumarkot.ingestion.kafka.IngestionEventPublisher;
import com.jumarkot.ingestion.repository.IngestedEventRepository;
import com.jumarkot.shared.auth.TenantContext;
import com.jumarkot.shared.auth.TenantContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class EventIngestionService {

    private final IngestedEventRepository repository;
    private final IngestionEventPublisher ingestionEventPublisher;

    public EventIngestionService(IngestedEventRepository repository,
                                 IngestionEventPublisher ingestionEventPublisher) {
        this.repository = repository;
        this.ingestionEventPublisher = ingestionEventPublisher;
    }

    @Transactional
    public IngestionResult ingest(EventPayload payload) {
        TenantContext tenantContext = TenantContextHolder.require();

        return repository.findByTenantIdAndIdempotencyKey(tenantContext.tenantId(), payload.idempotencyKey())
                .map(existing -> new IngestionResult(
                        existing.eventId(),
                        existing.status(),
                    existing.deliveryStatus(),
                        existing.createdAt(),
                        true
                ))
                .orElseGet(() -> createEvent(payload, tenantContext));
    }

            @Transactional(readOnly = true)
            public List<RecentEvent> recent(int limit) {
            TenantContext tenantContext = TenantContextHolder.require();
            return repository.findRecentByTenantId(tenantContext.tenantId(), limit).stream()
                .map(event -> new RecentEvent(
                    event.eventId(),
                    event.idempotencyKey(),
                    event.eventType(),
                    event.entityId(),
                    event.entityType(),
                    event.ingestionStatus(),
                    event.deliveryStatus(),
                    event.createdAt(),
                    event.publishedAt()
                ))
                .toList();
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
                "PENDING",
                null,
                null,
                now
        );
        repository.insert(event);
            ingestionEventPublisher.publish(event);
            return new IngestionResult(event.id(), event.ingestionStatus(), event.deliveryStatus(), event.createdAt(), false);
    }

            public record IngestionResult(UUID eventId, String status, String deliveryStatus, Instant acceptedAt, boolean duplicate) {}

            public record RecentEvent(
                UUID eventId,
                String idempotencyKey,
                String eventType,
                String entityId,
                String entityType,
                String ingestionStatus,
                String deliveryStatus,
                Instant createdAt,
                Instant publishedAt
            ) {}
}
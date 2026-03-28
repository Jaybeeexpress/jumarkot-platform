package com.jumarkot.ingestion.service;

import com.jumarkot.contracts.decision.DecisionRequest;
import com.jumarkot.events.DomainEvent;
import com.jumarkot.events.ingestion.IngestionTopics;
import com.jumarkot.ingestion.domain.IngestedEvent;
import com.jumarkot.ingestion.repository.IngestedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventIngestionService {

    private final IngestedEventRepository eventRepository;
    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @Transactional
    public IngestedEvent ingest(DecisionRequest request) {
        // Idempotency check
        if (eventRepository.existsByIdempotencyKey(request.getIdempotencyKey())) {
            log.info("Duplicate event received, idempotencyKey={}", request.getIdempotencyKey());
            return eventRepository.findByIdempotencyKey(request.getIdempotencyKey()).orElseThrow();
        }

        IngestedEvent event = IngestedEvent.builder()
                .idempotencyKey(request.getIdempotencyKey())
                .tenantId(UUID.fromString(request.getTenantId()))
                .entityId(request.getEntityId())
                .eventType(request.getEventType().name())
                .schemaVersion("1.0")
                .payload(request.getContext())
                .status("ACCEPTED")
                .occurredAt(request.getOccurredAt() != null ? request.getOccurredAt() : Instant.now())
                .build();

        eventRepository.save(event);

        DomainEvent domainEvent = DomainEvent.builder()
                .eventId(event.getId().toString())
                .topic(IngestionTopics.RAW_EVENTS)
                .eventType(request.getEventType().name())
                .schemaVersion("1.0")
                .tenantId(request.getTenantId())
                .entityId(request.getEntityId())
                .payload(request.getContext())
                .occurredAt(event.getOccurredAt())
                .publishedAt(Instant.now())
                .build();

        kafkaTemplate.send(IngestionTopics.RAW_EVENTS, event.getId().toString(), domainEvent)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish event to Kafka, eventId={}", event.getId(), ex);
                    } else {
                        long offset = result.getRecordMetadata().offset();
                        event.setKafkaTopic(IngestionTopics.RAW_EVENTS);
                        event.setKafkaOffset(offset);
                        event.setStatus("PUBLISHED");
                        eventRepository.save(event);
                        log.info("Event published to Kafka, eventId={}, offset={}", event.getId(), offset);
                    }
                });

        return event;
    }
}

package com.jumarkot.ingestion.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumarkot.ingestion.domain.IngestedEvent;
import com.jumarkot.ingestion.repository.IngestedEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class IngestionEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(IngestionEventPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final IngestedEventRepository repository;
    private final String topic;

    public IngestionEventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                   ObjectMapper objectMapper,
                                   IngestedEventRepository repository,
                                   @Value("${jumarkot.ingestion.events-topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.repository = repository;
        this.topic = topic;
    }

    public void publish(IngestedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(toKafkaPayload(event));
            kafkaTemplate.send(topic, event.tenantId().toString(), payload)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            repository.markPublishFailed(event.id(), exception.getMessage());
                            log.error("Failed to publish ingested event: eventId={}", event.id(), exception);
                            return;
                        }

                        repository.markPublished(event.id(), Instant.now());
                    });
        } catch (JsonProcessingException exception) {
            repository.markPublishFailed(event.id(), exception.getMessage());
            log.error("Failed to serialize ingested event: eventId={}", event.id(), exception);
        }
    }

    private Map<String, Object> toKafkaPayload(IngestedEvent event) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("eventType", "event.ingested");
        payload.put("eventId", event.id().toString());
        payload.put("tenantId", event.tenantId().toString());
        payload.put("environmentId", event.environmentId().toString());
        payload.put("environmentType", event.environmentType());
        payload.put("apiKeyId", event.apiKeyId().toString());
        payload.put("idempotencyKey", event.idempotencyKey());
        payload.put("eventName", event.eventType());
        payload.put("entityId", event.entityId());
        payload.put("entityType", event.entityType());
        payload.put("occurredAt", event.occurredAt().toString());
        payload.put("properties", event.properties() == null ? Map.of() : event.properties());
        payload.put("ipAddress", event.ipAddress());
        payload.put("deviceId", event.deviceId());
        payload.put("sessionId", event.sessionId());
        payload.put("userAgent", event.userAgent());
        payload.put("acceptedAt", event.createdAt().toString());
        return payload;
    }
}
package com.jumarkot.events;

// Centralised Kafka topic names — single source of truth across all services
public final class KafkaTopics {
    public static final String DECISIONS_CREATED   = "decisions.created";
    public static final String EVENTS_INGESTED     = "events.ingested";
    public static final String ALERTS_TRIGGERED    = "alerts.triggered";
    public static final String ENTITY_UPDATED      = "entity.updated";

    private KafkaTopics() {}
}

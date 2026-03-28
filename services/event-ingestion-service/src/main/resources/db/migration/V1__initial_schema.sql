-- V1: event ingestion tracking

CREATE TABLE ingested_events (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    idempotency_key  TEXT NOT NULL UNIQUE,
    tenant_id        UUID NOT NULL,
    entity_id        TEXT NOT NULL,
    event_type       TEXT NOT NULL,
    schema_version   TEXT NOT NULL DEFAULT '1.0',
    payload          JSONB NOT NULL,
    status           TEXT NOT NULL DEFAULT 'ACCEPTED',
    kafka_topic      TEXT,
    kafka_offset     BIGINT,
    occurred_at      TIMESTAMPTZ NOT NULL,
    ingested_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_ingested_events_tenant_id       ON ingested_events (tenant_id);
CREATE INDEX idx_ingested_events_idempotency_key ON ingested_events (idempotency_key);
CREATE INDEX idx_ingested_events_entity_id       ON ingested_events (entity_id);

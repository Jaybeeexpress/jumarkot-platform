CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE ingested_events (
    id                UUID         NOT NULL DEFAULT gen_random_uuid(),
    tenant_id         UUID         NOT NULL,
    environment_id    UUID         NOT NULL,
    environment_type  VARCHAR(20)  NOT NULL CHECK (environment_type IN ('PRODUCTION', 'SANDBOX')),
    api_key_id        UUID         NOT NULL,
    idempotency_key   VARCHAR(64)  NOT NULL,
    event_type        VARCHAR(64)  NOT NULL,
    entity_id         VARCHAR(255) NOT NULL,
    entity_type       VARCHAR(64)  NOT NULL,
    occurred_at       TIMESTAMPTZ  NOT NULL,
    properties        JSONB        NOT NULL DEFAULT '{}'::jsonb,
    ip_address        VARCHAR(64),
    device_id         VARCHAR(255),
    session_id        VARCHAR(255),
    user_agent        TEXT,
    ingestion_status  VARCHAR(20)  NOT NULL DEFAULT 'ACCEPTED' CHECK (ingestion_status IN ('ACCEPTED')),
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT ingested_events_pkey PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uq_ingested_events_tenant_idempotency
    ON ingested_events (tenant_id, idempotency_key);

CREATE INDEX idx_ingested_events_tenant_created_at
    ON ingested_events (tenant_id, created_at DESC);

CREATE INDEX idx_ingested_events_event_type
    ON ingested_events (event_type);
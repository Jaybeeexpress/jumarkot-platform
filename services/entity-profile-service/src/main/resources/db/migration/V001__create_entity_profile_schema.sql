CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE entity_profiles (
    id                  UUID         NOT NULL DEFAULT gen_random_uuid(),
    tenant_id           UUID         NOT NULL,
    environment_type    VARCHAR(20)  NOT NULL CHECK (environment_type IN ('PRODUCTION', 'SANDBOX')),
    entity_type         VARCHAR(64)  NOT NULL,
    entity_id           VARCHAR(255) NOT NULL,
    latest_event_type   VARCHAR(64)  NOT NULL,
    latest_event_at     TIMESTAMPTZ  NOT NULL,
    last_event_id       UUID         NOT NULL,
    last_idempotency_key VARCHAR(64) NOT NULL,
    latest_properties   JSONB        NOT NULL DEFAULT '{}'::jsonb,
    event_count         INTEGER      NOT NULL DEFAULT 1,
    first_seen_at       TIMESTAMPTZ  NOT NULL,
    last_seen_at        TIMESTAMPTZ  NOT NULL,
    updated_at          TIMESTAMPTZ  NOT NULL,

    CONSTRAINT entity_profiles_pkey PRIMARY KEY (id),
    CONSTRAINT uq_entity_profiles_identity UNIQUE (tenant_id, entity_type, entity_id)
);

CREATE INDEX idx_entity_profiles_tenant_updated_at
    ON entity_profiles (tenant_id, updated_at DESC);

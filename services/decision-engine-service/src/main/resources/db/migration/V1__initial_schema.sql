-- V1: decision records

CREATE TABLE decisions (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    idempotency_key     TEXT NOT NULL UNIQUE,
    tenant_id           UUID NOT NULL,
    entity_id           TEXT NOT NULL,
    event_type          TEXT NOT NULL,
    outcome             TEXT NOT NULL,
    risk_score          INT NOT NULL,
    reason_codes        TEXT[] NOT NULL DEFAULT '{}',
    recommended_action  TEXT,
    context             JSONB NOT NULL DEFAULT '{}',
    processing_time_ms  BIGINT,
    evaluated_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_decisions_tenant_id      ON decisions (tenant_id);
CREATE INDEX idx_decisions_entity_id      ON decisions (entity_id);
CREATE INDEX idx_decisions_outcome        ON decisions (outcome);
CREATE INDEX idx_decisions_evaluated_at   ON decisions (evaluated_at DESC);

CREATE TABLE decision_reason_codes (
    decision_id UUID NOT NULL REFERENCES decisions(id) ON DELETE CASCADE,
    reason_code TEXT NOT NULL,
    PRIMARY KEY (decision_id, reason_code)
);

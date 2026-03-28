-- V1: rules schema

CREATE TABLE rules (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    name            TEXT NOT NULL,
    description     TEXT,
    event_type      TEXT NOT NULL,
    condition_expr  TEXT NOT NULL,
    action          TEXT NOT NULL,
    priority        INT NOT NULL DEFAULT 100,
    risk_score_delta INT NOT NULL DEFAULT 0,
    enabled         BOOLEAN NOT NULL DEFAULT true,
    version         INT NOT NULL DEFAULT 1,
    effective_from  TIMESTAMPTZ,
    effective_until TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_rules_tenant_id  ON rules (tenant_id);
CREATE INDEX idx_rules_event_type ON rules (event_type);
CREATE INDEX idx_rules_enabled    ON rules (enabled);
CREATE INDEX idx_rules_priority   ON rules (priority);

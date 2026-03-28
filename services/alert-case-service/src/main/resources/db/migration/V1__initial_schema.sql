-- V1: alerts and cases schema

CREATE TABLE alerts (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    entity_id       TEXT NOT NULL,
    decision_id     UUID,
    alert_type      TEXT NOT NULL,
    severity        TEXT NOT NULL DEFAULT 'MEDIUM',
    status          TEXT NOT NULL DEFAULT 'OPEN',
    title           TEXT NOT NULL,
    description     TEXT,
    assigned_to     UUID,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    resolved_at     TIMESTAMPTZ
);

CREATE INDEX idx_alerts_tenant_id ON alerts (tenant_id);
CREATE INDEX idx_alerts_status    ON alerts (status);
CREATE INDEX idx_alerts_entity_id ON alerts (entity_id);

CREATE TABLE cases (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL,
    case_number     TEXT NOT NULL UNIQUE,
    title           TEXT NOT NULL,
    description     TEXT,
    status          TEXT NOT NULL DEFAULT 'OPEN',
    priority        TEXT NOT NULL DEFAULT 'MEDIUM',
    assigned_to     UUID,
    created_by      UUID NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    closed_at       TIMESTAMPTZ
);

CREATE INDEX idx_cases_tenant_id  ON cases (tenant_id);
CREATE INDEX idx_cases_status     ON cases (status);
CREATE INDEX idx_cases_case_number ON cases (case_number);

CREATE TABLE case_alerts (
    case_id   UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    alert_id  UUID NOT NULL REFERENCES alerts(id) ON DELETE CASCADE,
    PRIMARY KEY (case_id, alert_id)
);

CREATE TABLE case_notes (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    case_id     UUID NOT NULL REFERENCES cases(id) ON DELETE CASCADE,
    content     TEXT NOT NULL,
    author_id   UUID NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_case_notes_case_id ON case_notes (case_id);

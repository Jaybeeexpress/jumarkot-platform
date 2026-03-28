CREATE TABLE decisions (
    id              UUID        PRIMARY KEY,
    tenant_id       UUID        NOT NULL,
    environment_type VARCHAR(20) NOT NULL,
    entity_id       VARCHAR(255) NOT NULL,
    entity_type     VARCHAR(64)  NOT NULL,
    event_type      VARCHAR(64)  NOT NULL,
    risk_score      SMALLINT     NOT NULL CHECK (risk_score BETWEEN 0 AND 100),
    risk_level      VARCHAR(20)  NOT NULL,
    decision        VARCHAR(20)  NOT NULL,
    recommended_action VARCHAR(50) NOT NULL,
    matched_rules   JSONB        NOT NULL DEFAULT '[]',
    triggered_signals JSONB      NOT NULL DEFAULT '[]',
    idempotency_key VARCHAR(64)  NOT NULL,
    correlation_id  VARCHAR(64),
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_decisions_tenant_entity  ON decisions (tenant_id, entity_id);
CREATE INDEX idx_decisions_tenant_created ON decisions (tenant_id, created_at DESC);
CREATE UNIQUE INDEX idx_decisions_idem    ON decisions (tenant_id, idempotency_key);

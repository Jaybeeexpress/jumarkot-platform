-- rules-service: V001 — Initial schema
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE rules (
    id               UUID         NOT NULL DEFAULT gen_random_uuid(),
    tenant_id        UUID         NOT NULL,
    environment_type VARCHAR(20)  NOT NULL CHECK (environment_type IN ('PRODUCTION', 'SANDBOX')),
    name             VARCHAR(255) NOT NULL,
    description      TEXT,
    category         VARCHAR(100) NOT NULL,
    priority         INTEGER      NOT NULL DEFAULT 100,
    status           VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE'
                                  CHECK (status IN ('ACTIVE','INACTIVE','DRAFT')),

    -- Conditions stored as JSON array of {field, operator, value}
    conditions       JSONB        NOT NULL DEFAULT '[]',
    condition_logic  VARCHAR(3)   NOT NULL DEFAULT 'AND' CHECK (condition_logic IN ('AND','OR')),

    action           VARCHAR(20)  NOT NULL CHECK (action IN ('DECLINE','REVIEW','BLOCK','FLAG')),
    score_adjustment INTEGER      NOT NULL DEFAULT 0 CHECK (score_adjustment BETWEEN 0 AND 100),
    reason_code      VARCHAR(100) NOT NULL,
    version          INTEGER      NOT NULL DEFAULT 1,

    effective_from   TIMESTAMPTZ,
    effective_to     TIMESTAMPTZ,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by       UUID,

    CONSTRAINT rules_pkey PRIMARY KEY (id)
);

CREATE INDEX idx_rules_tenant_env  ON rules (tenant_id, environment_type);
CREATE INDEX idx_rules_status      ON rules (status) WHERE status = 'ACTIVE';
CREATE INDEX idx_rules_priority    ON rules (priority DESC);

-- Version history — immutable snapshots on each rule update
CREATE TABLE rule_versions (
    id           UUID        NOT NULL DEFAULT gen_random_uuid(),
    rule_id      UUID        NOT NULL REFERENCES rules (id) ON DELETE CASCADE,
    tenant_id    UUID        NOT NULL,
    version      INTEGER     NOT NULL,
    snapshot     JSONB       NOT NULL,           -- full rule JSON at this version
    changed_by   UUID,
    changed_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT rule_versions_pkey   PRIMARY KEY (id),
    CONSTRAINT rule_versions_unique UNIQUE (rule_id, version)
);

CREATE INDEX idx_rule_versions_rule_id ON rule_versions (rule_id);

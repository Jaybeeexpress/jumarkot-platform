CREATE TABLE IF NOT EXISTS tenant_environments (
    id           UUID         NOT NULL DEFAULT gen_random_uuid(),
    tenant_id    UUID         NOT NULL,
    name         VARCHAR(100) NOT NULL,
    type         VARCHAR(20)  NOT NULL,
    api_base_url VARCHAR(500),
    webhook_url  VARCHAR(500),
    status       VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT pk_tenant_environments PRIMARY KEY (id),
    CONSTRAINT fk_tenant_environments_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE,
    CONSTRAINT chk_tenant_environments_type CHECK (type IN ('PRODUCTION', 'SANDBOX', 'STAGING')),
    CONSTRAINT chk_tenant_environments_status CHECK (status IN ('ACTIVE', 'SUSPENDED'))
);

CREATE INDEX IF NOT EXISTS idx_tenant_environments_tenant_id ON tenant_environments (tenant_id);
CREATE INDEX IF NOT EXISTS idx_tenant_environments_tenant_type ON tenant_environments (tenant_id, type);

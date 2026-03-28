CREATE TABLE IF NOT EXISTS tenant_settings (
    tenant_id              UUID          NOT NULL,
    timezone               VARCHAR(50)   NOT NULL DEFAULT 'UTC',
    default_currency       VARCHAR(3)    NOT NULL DEFAULT 'USD',
    risk_threshold         NUMERIC(5, 4) NOT NULL DEFAULT 0.7000,
    webhook_signing_secret VARCHAR(64)   NOT NULL,
    updated_at             TIMESTAMPTZ   NOT NULL DEFAULT now(),

    CONSTRAINT pk_tenant_settings PRIMARY KEY (tenant_id),
    CONSTRAINT fk_tenant_settings_tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE,
    CONSTRAINT chk_risk_threshold CHECK (risk_threshold >= 0.0 AND risk_threshold <= 1.0)
);

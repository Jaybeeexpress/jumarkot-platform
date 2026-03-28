-- tenant-service: V001 — Initial schema
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE tenants (
    id            UUID         NOT NULL DEFAULT gen_random_uuid(),
    slug          VARCHAR(63)  NOT NULL,
    name          VARCHAR(255) NOT NULL,
    status        VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE'
                               CHECK (status IN ('ACTIVE','SUSPENDED','PENDING')),
    plan          VARCHAR(50)  NOT NULL DEFAULT 'FREE',
    contact_email VARCHAR(255) NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT tenants_pkey         PRIMARY KEY (id),
    CONSTRAINT tenants_slug_unique  UNIQUE (slug)
);

CREATE TABLE tenant_environments (
    id         UUID        NOT NULL DEFAULT gen_random_uuid(),
    tenant_id  UUID        NOT NULL REFERENCES tenants (id) ON DELETE CASCADE,
    name       VARCHAR(50) NOT NULL,
    type       VARCHAR(20) NOT NULL CHECK (type IN ('PRODUCTION','SANDBOX')),
    status     VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE','SUSPENDED')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT tenant_environments_pkey   PRIMARY KEY (id),
    CONSTRAINT tenant_environments_unique UNIQUE (tenant_id, type)
);

CREATE INDEX idx_tenant_environments_tenant_id ON tenant_environments (tenant_id);

CREATE TABLE tenant_settings (
    tenant_id  UUID         NOT NULL REFERENCES tenants (id) ON DELETE CASCADE,
    key        VARCHAR(255) NOT NULL,
    value      TEXT,
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT tenant_settings_pkey PRIMARY KEY (tenant_id, key)
);

-- Update updated_at automatically
CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tenants_updated_at
    BEFORE UPDATE ON tenants
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();

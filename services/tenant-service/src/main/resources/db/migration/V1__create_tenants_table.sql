CREATE TABLE IF NOT EXISTS tenants (
    id         UUID         NOT NULL DEFAULT gen_random_uuid(),
    name       VARCHAR(255) NOT NULL,
    slug       VARCHAR(63)  NOT NULL,
    status     VARCHAR(20)  NOT NULL DEFAULT 'PENDING_SETUP',
    plan       VARCHAR(20)  NOT NULL DEFAULT 'FREE',
    region     VARCHAR(50)  NOT NULL,
    owner_id   UUID         NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT pk_tenants PRIMARY KEY (id),
    CONSTRAINT uq_tenants_slug UNIQUE (slug),
    CONSTRAINT chk_tenants_status CHECK (status IN ('PENDING_SETUP', 'ACTIVE', 'SUSPENDED', 'DELETED')),
    CONSTRAINT chk_tenants_plan CHECK (plan IN ('FREE', 'STARTER', 'GROWTH', 'ENTERPRISE'))
);

CREATE INDEX IF NOT EXISTS idx_tenants_slug ON tenants (slug);
CREATE INDEX IF NOT EXISTS idx_tenants_status ON tenants (status);
CREATE INDEX IF NOT EXISTS idx_tenants_owner_id ON tenants (owner_id);

CREATE TABLE IF NOT EXISTS api_keys (
    id           UUID         NOT NULL DEFAULT gen_random_uuid(),
    key_hash     CHAR(64)     NOT NULL,
    prefix       VARCHAR(20)  NOT NULL,
    tenant_id    UUID         NOT NULL,
    user_id      UUID         NOT NULL,
    name         VARCHAR(100) NOT NULL,
    expires_at   TIMESTAMPTZ,
    last_used_at TIMESTAMPTZ,
    status       VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT pk_api_keys PRIMARY KEY (id),
    CONSTRAINT uq_api_keys_hash UNIQUE (key_hash),
    CONSTRAINT chk_api_keys_status CHECK (status IN ('ACTIVE', 'REVOKED', 'EXPIRED')),
    CONSTRAINT fk_api_keys_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS api_key_scopes (
    api_key_id UUID         NOT NULL,
    scope      VARCHAR(100) NOT NULL,

    CONSTRAINT pk_api_key_scopes PRIMARY KEY (api_key_id, scope),
    CONSTRAINT fk_api_key_scopes_key FOREIGN KEY (api_key_id) REFERENCES api_keys (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_api_keys_key_hash ON api_keys (key_hash);
CREATE INDEX IF NOT EXISTS idx_api_keys_tenant_id ON api_keys (tenant_id);
CREATE INDEX IF NOT EXISTS idx_api_keys_user_id ON api_keys (user_id);
CREATE INDEX IF NOT EXISTS idx_api_keys_tenant_status ON api_keys (tenant_id, status);

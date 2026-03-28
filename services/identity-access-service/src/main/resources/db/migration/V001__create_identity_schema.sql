-- identity-access-service: V001 — Initial schema
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ──────────────────────────────────────────────────────────────
-- API Keys
-- The raw key is NEVER stored. Only the SHA-256 hash is persisted.
-- ──────────────────────────────────────────────────────────────
CREATE TABLE api_keys (
    id               UUID         NOT NULL DEFAULT gen_random_uuid(),
    tenant_id        UUID         NOT NULL,
    environment_type VARCHAR(20)  NOT NULL CHECK (environment_type IN ('PRODUCTION', 'SANDBOX')),
    key_prefix       VARCHAR(20)  NOT NULL,
    key_hash         VARCHAR(64)  NOT NULL,             -- SHA-256 hex, unique
    name             VARCHAR(255) NOT NULL,
    scopes           TEXT[]       NOT NULL DEFAULT '{}',
    active           BOOLEAN      NOT NULL DEFAULT TRUE,
    expires_at       TIMESTAMPTZ,
    last_used_at     TIMESTAMPTZ,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by       UUID,
    revoked_at       TIMESTAMPTZ,
    revoke_reason    VARCHAR(500),

    CONSTRAINT api_keys_pkey        PRIMARY KEY (id),
    CONSTRAINT api_keys_hash_unique UNIQUE (key_hash)
);

CREATE INDEX idx_api_keys_tenant_id  ON api_keys (tenant_id);
CREATE INDEX idx_api_keys_active     ON api_keys (active) WHERE active = TRUE;

-- ──────────────────────────────────────────────────────────────
-- Users
-- ──────────────────────────────────────────────────────────────
CREATE TABLE users (
    id               UUID         NOT NULL DEFAULT gen_random_uuid(),
    tenant_id        UUID         NOT NULL,
    email            VARCHAR(255) NOT NULL,
    password_hash    VARCHAR(255) NOT NULL,
    full_name        VARCHAR(255),
    active           BOOLEAN      NOT NULL DEFAULT TRUE,
    email_verified   BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT users_pkey           PRIMARY KEY (id),
    CONSTRAINT users_email_unique   UNIQUE (tenant_id, email)
);

CREATE INDEX idx_users_tenant_id ON users (tenant_id);

-- ──────────────────────────────────────────────────────────────
-- Roles
-- NULL tenant_id = system-level role available to all tenants
-- ──────────────────────────────────────────────────────────────
CREATE TABLE roles (
    id          UUID         NOT NULL DEFAULT gen_random_uuid(),
    tenant_id   UUID,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    permissions TEXT[]       NOT NULL DEFAULT '{}',
    system_role BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT roles_pkey PRIMARY KEY (id)
);

CREATE INDEX idx_roles_tenant_id ON roles (tenant_id);

CREATE TABLE user_roles (
    user_id    UUID        NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    role_id    UUID        NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    granted_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    granted_by UUID,

    CONSTRAINT user_roles_pkey PRIMARY KEY (user_id, role_id)
);

-- ──────────────────────────────────────────────────────────────
-- Seed system roles
-- ──────────────────────────────────────────────────────────────
INSERT INTO roles (id, name, description, permissions, system_role) VALUES
    ('00000000-0000-0000-0000-000000000001', 'ADMIN',     'Full platform access',               ARRAY['*'],                                               TRUE),
    ('00000000-0000-0000-0000-000000000002', 'ANALYST',   'Read and action cases and alerts',   ARRAY['cases:read','cases:action','alerts:read','rules:read'], TRUE),
    ('00000000-0000-0000-0000-000000000003', 'DEVELOPER', 'API integration access',             ARRAY['events:write','decisions:write','api-keys:read'],   TRUE),
    ('00000000-0000-0000-0000-000000000004', 'VIEWER',    'Read-only access across the platform', ARRAY['*:read'],                                        TRUE);

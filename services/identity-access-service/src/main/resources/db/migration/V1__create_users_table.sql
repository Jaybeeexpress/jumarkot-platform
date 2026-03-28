CREATE TABLE IF NOT EXISTS users (
    id          UUID         NOT NULL DEFAULT gen_random_uuid(),
    email       VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    tenant_id   UUID         NOT NULL,
    status      VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    first_name  VARCHAR(100),
    last_name   VARCHAR(100),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_email_tenant UNIQUE (email, tenant_id),
    CONSTRAINT chk_users_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'LOCKED'))
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id UUID        NOT NULL,
    role    VARCHAR(50) NOT NULL,

    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT chk_user_roles_role CHECK (role IN ('TENANT_ADMIN', 'ANALYST', 'VIEWER', 'SERVICE_ACCOUNT'))
);

CREATE INDEX IF NOT EXISTS idx_users_tenant_id ON users (tenant_id);
CREATE INDEX IF NOT EXISTS idx_users_email ON users (email);
CREATE INDEX IF NOT EXISTS idx_users_tenant_status ON users (tenant_id, status);

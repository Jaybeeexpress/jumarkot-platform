package com.jumarkot.identity.repository;

import com.jumarkot.identity.domain.ApiKey;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ApiKeyRepository {

    private final DSLContext dsl;

    public ApiKeyRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public void insert(ApiKey key) {
        dsl.insertInto(DSL.table("api_keys"))
                .set(DSL.field("id"),               key.id())
                .set(DSL.field("tenant_id"),        key.tenantId())
                .set(DSL.field("environment_type"), key.environmentType())
                .set(DSL.field("key_prefix"),       key.keyPrefix())
                .set(DSL.field("key_hash"),         key.keyHash())
                .set(DSL.field("name"),             key.name())
                .set(DSL.field("scopes"),           scopesToArray(key.scopes()))
                .set(DSL.field("active"),           key.active())
                .set(DSL.field("expires_at"),       key.expiresAt())
                .set(DSL.field("created_by"),       key.createdBy())
                .execute();
    }

    public Optional<ApiKey> findByKeyHash(String keyHash) {
        return dsl.select()
                .from(DSL.table("api_keys"))
                .where(DSL.field("key_hash").eq(keyHash))
                .and(DSL.field("active").eq(true))
                .fetchOptional(this::mapRow);
    }

    public Optional<ApiKey> findById(UUID id, UUID tenantId) {
        return dsl.select()
                .from(DSL.table("api_keys"))
                .where(DSL.field("id").eq(id))
                .and(DSL.field("tenant_id").eq(tenantId))
                .fetchOptional(this::mapRow);
    }

    public List<ApiKey> findAllByTenantId(UUID tenantId) {
        return dsl.select()
                .from(DSL.table("api_keys"))
                .where(DSL.field("tenant_id").eq(tenantId))
                .orderBy(DSL.field("created_at").desc())
                .fetch(this::mapRow);
    }

    public void revoke(UUID id, UUID tenantId, String reason) {
        dsl.update(DSL.table("api_keys"))
                .set(DSL.field("active"), false)
                .set(DSL.field("revoked_at"), DSL.currentOffsetDateTime())
                .set(DSL.field("revoke_reason"), reason)
                .where(DSL.field("id").eq(id))
                .and(DSL.field("tenant_id").eq(tenantId))
                .execute();
    }

    public void updateLastUsed(String keyHash) {
        dsl.update(DSL.table("api_keys"))
                .set(DSL.field("last_used_at"), DSL.currentOffsetDateTime())
                .where(DSL.field("key_hash").eq(keyHash))
                .execute();
    }

    private ApiKey mapRow(org.jooq.Record r) {
        String[] scopesArr = {};
        Object rawScopes = r.get("scopes");
        if (rawScopes instanceof java.sql.Array arr) {
            try {
                scopesArr = (String[]) arr.getArray();
            } catch (Exception ignored) {}
        }
        return new ApiKey(
                r.get("id", UUID.class),
                r.get("tenant_id", UUID.class),
                r.get("environment_type", String.class),
                r.get("key_prefix", String.class),
                r.get("key_hash", String.class),
                r.get("name", String.class),
                Arrays.asList(scopesArr),
                r.get("active", Boolean.class),
                r.get("expires_at", java.time.OffsetDateTime.class),
                r.get("last_used_at", java.time.OffsetDateTime.class),
                r.get("created_at", java.time.OffsetDateTime.class),
                r.get("created_by", UUID.class),
                r.get("revoked_at", java.time.OffsetDateTime.class),
                r.get("revoke_reason", String.class)
        );
    }

    private Object scopesToArray(List<String> scopes) {
        // jOOQ passes this through to the PostgreSQL TEXT[] column
        return scopes.toArray(new String[0]);
    }
}

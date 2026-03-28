package com.jumarkot.tenant.repository;

import com.jumarkot.tenant.domain.Tenant;
import com.jumarkot.tenant.domain.TenantEnvironment;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TenantRepository {

    private final DSLContext dsl;

    public TenantRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public void insertTenant(Tenant t) {
        dsl.insertInto(DSL.table("tenants"))
                .set(DSL.field("id"),            t.id())
                .set(DSL.field("slug"),          t.slug())
                .set(DSL.field("name"),          t.name())
                .set(DSL.field("status"),        t.status())
                .set(DSL.field("plan"),          t.plan())
                .set(DSL.field("contact_email"), t.contactEmail())
                .execute();
    }

    public Optional<Tenant> findById(UUID id) {
        return dsl.select()
                .from(DSL.table("tenants"))
                .where(DSL.field("id").eq(id))
                .fetchOptional(this::mapTenant);
    }

    public Optional<Tenant> findBySlug(String slug) {
        return dsl.select()
                .from(DSL.table("tenants"))
                .where(DSL.field("slug").eq(slug))
                .fetchOptional(this::mapTenant);
    }

    public void insertEnvironment(TenantEnvironment env) {
        dsl.insertInto(DSL.table("tenant_environments"))
                .set(DSL.field("id"),        env.id())
                .set(DSL.field("tenant_id"), env.tenantId())
                .set(DSL.field("name"),      env.name())
                .set(DSL.field("type"),      env.type())
                .set(DSL.field("status"),    env.status())
                .execute();
    }

    public List<TenantEnvironment> findEnvironmentsByTenantId(UUID tenantId) {
        return dsl.select()
                .from(DSL.table("tenant_environments"))
                .where(DSL.field("tenant_id").eq(tenantId))
                .fetch(this::mapEnvironment);
    }

    public Optional<TenantEnvironment> findEnvironmentById(UUID environmentId) {
        return dsl.select()
                .from(DSL.table("tenant_environments"))
                .where(DSL.field("id").eq(environmentId))
                .fetchOptional(this::mapEnvironment);
    }

    private Tenant mapTenant(org.jooq.Record r) {
        return new Tenant(
                r.get("id", UUID.class),
                r.get("slug", String.class),
                r.get("name", String.class),
                r.get("status", String.class),
                r.get("plan", String.class),
                r.get("contact_email", String.class),
                r.get("created_at", java.time.OffsetDateTime.class),
                r.get("updated_at", java.time.OffsetDateTime.class)
        );
    }

    private TenantEnvironment mapEnvironment(org.jooq.Record r) {
        return new TenantEnvironment(
                r.get("id", UUID.class),
                r.get("tenant_id", UUID.class),
                r.get("name", String.class),
                r.get("type", String.class),
                r.get("status", String.class),
                r.get("created_at", java.time.OffsetDateTime.class)
        );
    }
}

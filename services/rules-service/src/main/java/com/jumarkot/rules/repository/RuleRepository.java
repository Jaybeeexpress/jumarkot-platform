package com.jumarkot.rules.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumarkot.rules.domain.ConditionLogic;
import com.jumarkot.rules.domain.RuleOperator;
import com.jumarkot.rules.dto.RuleDto;
import com.jumarkot.rules.exception.RuleNotFoundException;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RuleRepository {

    private final DSLContext dsl;
    private final ObjectMapper objectMapper;

    public RuleRepository(DSLContext dsl, ObjectMapper objectMapper) {
        this.dsl = dsl;
        this.objectMapper = objectMapper;
    }

    public UUID insert(RuleDto rule) {
        UUID id = UUID.randomUUID();
        dsl.insertInto(DSL.table("rules"))
                .set(DSL.field("id"),               id)
                .set(DSL.field("tenant_id"),        rule.tenantId())
                .set(DSL.field("environment_type"), rule.environmentType())
                .set(DSL.field("name"),             rule.name())
                .set(DSL.field("description"),      rule.description())
                .set(DSL.field("category"),         rule.category())
                .set(DSL.field("priority"),         rule.priority())
                .set(DSL.field("status"),           "ACTIVE")
                .set(DSL.field("conditions", JSONB.class), JSONB.valueOf(serializeConditions(rule.conditions())))
                .set(DSL.field("condition_logic"),  rule.conditionLogic().name())
                .set(DSL.field("action"),           rule.action())
                .set(DSL.field("score_adjustment"), rule.scoreAdjustment())
                .set(DSL.field("reason_code"),      rule.reasonCode())
                .set(DSL.field("effective_from"),   rule.effectiveFrom())
                .set(DSL.field("effective_to"),     rule.effectiveTo())
                .set(DSL.field("version"),          1)
                .execute();
        return id;
    }

    public List<RuleDto> findActiveByTenantAndEnvironment(UUID tenantId, String environmentType) {
        OffsetDateTime now = OffsetDateTime.now();
        return dsl.select()
                .from(DSL.table("rules"))
                .where(DSL.field("tenant_id").eq(tenantId))
                .and(DSL.field("environment_type").eq(environmentType))
                .and(DSL.field("status").eq("ACTIVE"))
                .and(DSL.field("effective_from").isNull()
                        .or(DSL.field("effective_from").le(now)))
                .and(DSL.field("effective_to").isNull()
                        .or(DSL.field("effective_to").gt(now)))
                .orderBy(DSL.field("priority").desc())
                .fetch(this::mapRow);
    }

    public Optional<RuleDto> findById(UUID id, UUID tenantId) {
        return dsl.select()
                .from(DSL.table("rules"))
                .where(DSL.field("id").eq(id))
                .and(DSL.field("tenant_id").eq(tenantId))
                .fetchOptional(this::mapRow);
    }

    public void updateStatus(UUID id, UUID tenantId, String status) {
        int rows = dsl.update(DSL.table("rules"))
                .set(DSL.field("status"), status)
                .set(DSL.field("updated_at"), DSL.currentOffsetDateTime())
                .where(DSL.field("id").eq(id))
                .and(DSL.field("tenant_id").eq(tenantId))
                .execute();
        if (rows == 0) {
            throw new RuleNotFoundException(id);
        }
    }

    private RuleDto mapRow(org.jooq.Record r) {
        // Read the jsonb column via JSONB type to get the raw JSON string
        JSONB conditionsJsonb = r.get("conditions", JSONB.class);
        String conditionsJson = conditionsJsonb != null ? conditionsJsonb.data() : "[]";
        List<RuleDto.RuleConditionDto> conditions = deserializeConditions(conditionsJson);
        return new RuleDto(
                r.get("id", UUID.class),
                r.get("tenant_id", UUID.class),
                r.get("environment_type", String.class),
                r.get("name", String.class),
                r.get("description", String.class),
                r.get("category", String.class),
                r.get("priority", Integer.class),
                r.get("status", String.class),
                conditions,
                ConditionLogic.valueOf(r.get("condition_logic", String.class)),
                r.get("action", String.class),
                r.get("score_adjustment", Integer.class),
                r.get("reason_code", String.class),
                r.get("effective_from", OffsetDateTime.class),
                r.get("effective_to", OffsetDateTime.class),
                r.get("version", Integer.class)
        );
    }

    private String serializeConditions(List<RuleDto.RuleConditionDto> conditions) {
        try {
            return objectMapper.writeValueAsString(conditions);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize rule conditions", e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<RuleDto.RuleConditionDto> deserializeConditions(String json) {
        try {
            return objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(
                            List.class, RuleDto.RuleConditionDto.class));
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
}

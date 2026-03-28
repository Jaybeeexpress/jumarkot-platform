package com.jumarkot.decision.repository;

import com.jumarkot.contracts.decision.DecisionRequest;
import com.jumarkot.contracts.decision.DecisionResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public class DecisionRepository {

    private final DSLContext dsl;
    private final ObjectMapper objectMapper;

    public DecisionRepository(DSLContext dsl, ObjectMapper objectMapper) {
        this.dsl = dsl;
        this.objectMapper = objectMapper;
    }

    public void save(String decisionId,
                     String tenantId,
                     String environmentType,
                     DecisionRequest req,
                     DecisionResponse resp,
                     Instant createdAt) {
        try {
            dsl.insertInto(DSL.table("decisions"))
                    .set(DSL.field("id"), UUID.fromString(decisionId))
                    .set(DSL.field("tenant_id"), UUID.fromString(tenantId))
                    .set(DSL.field("environment_type"), environmentType)
                    .set(DSL.field("entity_id"), req.entityId())
                    .set(DSL.field("entity_type"), req.entityType())
                    .set(DSL.field("event_type"), req.eventType())
                    .set(DSL.field("risk_score"), resp.riskScore())
                    .set(DSL.field("risk_level"), resp.riskLevel().name())
                    .set(DSL.field("decision"), resp.decision().name())
                    .set(DSL.field("recommended_action"), resp.recommendedAction())
                    .set(DSL.field("matched_rules"), DSL.val(objectMapper.writeValueAsString(resp.matchedRules())))
                    .set(DSL.field("triggered_signals"), DSL.val(objectMapper.writeValueAsString(resp.triggeredSignals())))
                    .set(DSL.field("idempotency_key"), req.idempotencyKey())
                    .set(DSL.field("correlation_id"), resp.correlationId())
                    .set(DSL.field("created_at"), createdAt)
                    .execute();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize decision data", e);
        }
    }

    public Optional<DecisionResponse> findById(UUID id) {
        return dsl.select()
                .from(DSL.table("decisions"))
                .where(DSL.field("id").eq(id))
                .fetchOptional(r -> new DecisionResponse(
                        r.get("id", UUID.class).toString(),
                        com.jumarkot.contracts.decision.RiskDecision.valueOf(r.get("decision", String.class)),
                        r.get("risk_score", Integer.class),
                        com.jumarkot.contracts.decision.RiskLevel.valueOf(r.get("risk_level", String.class)),
                        java.util.List.of(),
                        java.util.List.of(),
                        r.get("recommended_action", String.class),
                        r.get("correlation_id", String.class),
                        r.get("created_at", Instant.class)
                ));
    }
}

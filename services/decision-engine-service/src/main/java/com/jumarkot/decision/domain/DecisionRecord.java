package com.jumarkot.decision.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "decisions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DecisionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "entity_id", nullable = false)
    private String entityId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(nullable = false)
    private String outcome;

    @Column(name = "risk_score", nullable = false)
    private int riskScore;

    @ElementCollection
    @CollectionTable(name = "decision_reason_codes", joinColumns = @JoinColumn(name = "decision_id"))
    @Column(name = "reason_code")
    private List<String> reasonCodes;

    @Column(name = "recommended_action")
    private String recommendedAction;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> context;

    @Column(name = "processing_time_ms")
    private Long processingTimeMs;

    @Column(name = "evaluated_at", nullable = false)
    private Instant evaluatedAt;
}

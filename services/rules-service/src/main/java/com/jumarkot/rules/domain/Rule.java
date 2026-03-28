package com.jumarkot.rules.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "condition_expr", nullable = false)
    private String conditionExpr;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private int priority;

    @Column(name = "risk_score_delta", nullable = false)
    private int riskScoreDelta;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false)
    private int version;

    @Column(name = "effective_from")
    private Instant effectiveFrom;

    @Column(name = "effective_until")
    private Instant effectiveUntil;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}

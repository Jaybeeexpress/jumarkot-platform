package com.jumarkot.decision.service;

import com.jumarkot.contracts.decision.DecisionRequest;
import com.jumarkot.contracts.decision.DecisionResponse;
import com.jumarkot.contracts.decision.RiskDecision;
import com.jumarkot.contracts.decision.RiskLevel;
import com.jumarkot.decision.client.RulesClient;
import com.jumarkot.decision.engine.RuleEvaluator;
import com.jumarkot.decision.kafka.DecisionEventPublisher;
import com.jumarkot.decision.repository.DecisionRepository;
import com.jumarkot.shared.auth.TenantContext;
import com.jumarkot.shared.auth.TenantContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DecisionServiceRegressionTest {

    @Mock
    private RulesClient rulesClient;

    @Mock
    private RuleEvaluator ruleEvaluator;

    @Mock
    private DecisionRepository decisionRepository;

    @Mock
    private DecisionEventPublisher eventPublisher;

    @Mock
    private StringRedisTemplate redis;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private DecisionService decisionService;
    private TenantContext tenantContext;

    @BeforeEach
    void setUp() {
        decisionService = new DecisionService(rulesClient, ruleEvaluator, decisionRepository, eventPublisher, redis);
        when(redis.opsForValue()).thenReturn(valueOperations);

        tenantContext = new TenantContext(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "acme",
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                TenantContext.EnvironmentType.PRODUCTION,
                List.of("*"),
                UUID.fromString("33333333-3333-3333-3333-333333333333")
        );
        TenantContextHolder.set(tenantContext);
    }

    @AfterEach
    void tearDown() {
        TenantContextHolder.clear();
    }

    @Test
    void decide_idempotentReplay_returnsCachedDecisionWithoutReEvaluation() {
        DecisionRequest request = request("idem-001", 1200);
        String idemKey = "decision:idem:" + tenantContext.tenantId() + ":" + request.idempotencyKey();
        String existingDecisionId = "44444444-4444-4444-4444-444444444444";

        DecisionResponse cached = new DecisionResponse(
                existingDecisionId,
                RiskDecision.REVIEW,
                42,
                RiskLevel.MEDIUM,
                List.of(),
                List.of(),
                "CHALLENGE",
                request.idempotencyKey(),
                Instant.now()
        );

        when(valueOperations.get(idemKey)).thenReturn(existingDecisionId);
        when(decisionRepository.findById(UUID.fromString(existingDecisionId))).thenReturn(Optional.of(cached));

        DecisionResponse result = decisionService.decide(request);

        assertEquals(cached.decisionId(), result.decisionId());
        assertEquals(cached.decision(), result.decision());
        verify(decisionRepository).findById(UUID.fromString(existingDecisionId));
        verify(rulesClient, never()).getActiveRules(any(), anyString());
        verify(ruleEvaluator, never()).evaluate(any(), any());
        verify(decisionRepository, never()).save(anyString(), anyString(), anyString(), any(), any(), any());
        verify(eventPublisher, never()).publish(anyString(), anyString(), any(), anyInt(), anyString());
    }

    @Test
    void decide_noMatchingRules_defaultsToApproveLowAndAllow() {
        DecisionRequest request = request("idem-002", 100);

        when(valueOperations.get(anyString())).thenReturn(null);
        when(rulesClient.getActiveRules(tenantContext.tenantId(), "PRODUCTION")).thenReturn(List.of());
        when(ruleEvaluator.evaluate(any(), any())).thenReturn(new RuleEvaluator.EvaluationResult(List.of(), List.of(), 0));

        DecisionResponse result = decisionService.decide(request);

        assertNotNull(result.decisionId());
        assertEquals(RiskDecision.APPROVE, result.decision());
        assertEquals(RiskLevel.LOW, result.riskLevel());
        assertEquals(0, result.riskScore());
        assertEquals("ALLOW", result.recommendedAction());
        assertEquals(request.idempotencyKey(), result.correlationId());
        assertTrue(result.matchedRules().isEmpty());
        assertTrue(result.triggeredSignals().isEmpty());

        verify(decisionRepository).save(
                anyString(),
                eq(tenantContext.tenantId().toString()),
                eq("PRODUCTION"),
                eq(request),
                any(DecisionResponse.class),
                any(Instant.class)
        );

        ArgumentCaptor<String> idemKeyCaptor = ArgumentCaptor.forClass(String.class);
        verify(valueOperations).set(idemKeyCaptor.capture(), anyString(), eq(java.time.Duration.ofHours(24)));
        assertTrue(idemKeyCaptor.getValue().startsWith("decision:idem:" + tenantContext.tenantId()));

        verify(eventPublisher).publish(
                eq(tenantContext.tenantId().toString()),
                anyString(),
                eq(RiskDecision.APPROVE),
                eq(0),
                eq(request.entityId())
        );
    }

    private DecisionRequest request(String idempotencyKey, int amount) {
        return new DecisionRequest(
                idempotencyKey,
                "entity-123",
                "ACCOUNT",
                "PAYMENT",
                Map.of("amount", amount),
                "127.0.0.1",
                "device-1",
                "session-1",
                "JUnit"
        );
    }
}

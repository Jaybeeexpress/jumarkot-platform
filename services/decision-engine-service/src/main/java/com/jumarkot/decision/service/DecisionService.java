package com.jumarkot.decision.service;

import com.jumarkot.contracts.decision.*;
import com.jumarkot.decision.client.RulesClient;
import com.jumarkot.decision.engine.DecisionContext;
import com.jumarkot.decision.engine.RuleEvaluator;
import com.jumarkot.decision.kafka.DecisionEventPublisher;
import com.jumarkot.decision.repository.DecisionRepository;
import com.jumarkot.rules.dto.RuleDto;
import com.jumarkot.shared.auth.TenantContext;
import com.jumarkot.shared.auth.TenantContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class DecisionService {

    private static final Logger log = LoggerFactory.getLogger(DecisionService.class);
    private static final Duration IDEMPOTENCY_TTL = Duration.ofHours(24);
    private static final String IDEM_PREFIX = "decision:idem:";

    private final RulesClient rulesClient;
    private final RuleEvaluator ruleEvaluator;
    private final DecisionRepository decisionRepository;
    private final DecisionEventPublisher eventPublisher;
    private final StringRedisTemplate redis;

    public DecisionService(RulesClient rulesClient,
                           RuleEvaluator ruleEvaluator,
                           DecisionRepository decisionRepository,
                           DecisionEventPublisher eventPublisher,
                           StringRedisTemplate redis) {
        this.rulesClient = rulesClient;
        this.ruleEvaluator = ruleEvaluator;
        this.decisionRepository = decisionRepository;
        this.eventPublisher = eventPublisher;
        this.redis = redis;
    }

    public DecisionResponse decide(DecisionRequest request) {
        TenantContext tenant = TenantContextHolder.require();

        // ── Idempotency check ──────────────────────────────────────────────────
        String idemKey = IDEM_PREFIX + tenant.tenantId() + ":" + request.idempotencyKey();
        String existingId = redis.opsForValue().get(idemKey);
        if (existingId != null) {
            log.debug("Idempotent request, returning cached decision: key={}", request.idempotencyKey());
            return decisionRepository.findById(UUID.fromString(existingId))
                    .orElseThrow(() -> new IllegalStateException("Cached decision not found: " + existingId));
        }

        // ── Load active rules ─────────────────────────────────────────────────
        List<RuleDto> rules = rulesClient.getActiveRules(
                tenant.tenantId(), tenant.environmentType().name());

        // ── Evaluate ──────────────────────────────────────────────────────────
        DecisionContext ctx = new DecisionContext(
                request.entityId(), request.entityType(), request.eventType(),
                request.payload(), request.ipAddress(), request.deviceId(),
                request.sessionId(), request.userAgent());

        RuleEvaluator.EvaluationResult evaluation = ruleEvaluator.evaluate(rules, ctx);

        // ── Map score → outcome ────────────────────────────────────────────────
        int score = evaluation.riskScore();
        RiskLevel riskLevel = RiskLevel.fromScore(score);
        RiskDecision decision = resolveDecision(evaluation, riskLevel);
        String recommendedAction = resolveRecommendedAction(decision);

        String decisionId = UUID.randomUUID().toString();
        Instant now = Instant.now();

        DecisionResponse response = new DecisionResponse(
                decisionId,
                decision,
                score,
                riskLevel,
                evaluation.matchedRules(),
                evaluation.triggeredSignals(),
                recommendedAction,
                request.idempotencyKey(),
                now
        );

        // ── Persist ───────────────────────────────────────────────────────────
        decisionRepository.save(decisionId, tenant.tenantId(), tenant.environmentType().name(),
                request, response, now);

        // ── Cache idempotency key ─────────────────────────────────────────────
        redis.opsForValue().set(idemKey, decisionId, IDEMPOTENCY_TTL);

        // ── Publish async event (non-blocking) ───────────────────────────────
        eventPublisher.publish(tenant.tenantId(), decisionId, decision, score, request.entityId());

        log.info("Decision: id={} tenant={} entity={} score={} decision={}",
                decisionId, tenant.tenantId(), request.entityId(), score, decision);

        return response;
    }

    private RiskDecision resolveDecision(RuleEvaluator.EvaluationResult eval, RiskLevel level) {
        // If any matched rule explicitly demands a BLOCK or DECLINE, honour it
        for (var matched : eval.matchedRules()) {
            // In future: we can look up the rule action from the matched rule record
        }
        return switch (level) {
            case LOW      -> RiskDecision.APPROVE;
            case MEDIUM   -> RiskDecision.REVIEW;
            case HIGH     -> RiskDecision.REVIEW;
            case CRITICAL -> RiskDecision.DECLINE;
        };
    }

    private String resolveRecommendedAction(RiskDecision decision) {
        return switch (decision) {
            case APPROVE  -> "ALLOW";
            case REVIEW   -> "CHALLENGE";
            case DECLINE  -> "REJECT";
            case BLOCK    -> "BLOCK_AND_ESCALATE";
        };
    }
}

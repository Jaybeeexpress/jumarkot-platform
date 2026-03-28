package com.jumarkot.decision.service;

import com.jumarkot.contracts.decision.DecisionRequest;
import com.jumarkot.contracts.decision.DecisionResponse;
import com.jumarkot.decision.domain.DecisionRecord;
import com.jumarkot.decision.evaluation.EvaluationResult;
import com.jumarkot.decision.evaluation.RiskEvaluator;
import com.jumarkot.decision.repository.DecisionRecordRepository;
import com.jumarkot.events.DomainEvent;
import com.jumarkot.events.decision.DecisionTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DecisionService {

    private final DecisionRecordRepository decisionRepository;
    private final RiskEvaluator riskEvaluator;
    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @Transactional
    public DecisionResponse decide(DecisionRequest request) {
        // Idempotency
        return decisionRepository.findByIdempotencyKey(request.getIdempotencyKey())
                .map(this::toResponse)
                .orElseGet(() -> evaluate(request));
    }

    private DecisionResponse evaluate(DecisionRequest request) {
        long start = System.currentTimeMillis();

        EvaluationResult result = riskEvaluator.evaluate(request);

        long processingMs = System.currentTimeMillis() - start;

        DecisionRecord record = DecisionRecord.builder()
                .idempotencyKey(request.getIdempotencyKey())
                .tenantId(UUID.fromString(request.getTenantId()))
                .entityId(request.getEntityId())
                .eventType(request.getEventType().name())
                .outcome(result.getOutcome().name())
                .riskScore(result.getRiskScore())
                .reasonCodes(result.getReasonCodes())
                .recommendedAction(result.getRecommendedAction())
                .context(request.getContext())
                .processingTimeMs(processingMs)
                .evaluatedAt(Instant.now())
                .build();

        decisionRepository.save(record);

        publishDecisionEvent(record, request);

        log.info("Decision completed tenantId={} entityId={} outcome={} score={}",
                request.getTenantId(), request.getEntityId(),
                result.getOutcome(), result.getRiskScore());

        return toResponse(record);
    }

    private void publishDecisionEvent(DecisionRecord record, DecisionRequest request) {
        DomainEvent event = DomainEvent.builder()
                .eventId(record.getId().toString())
                .topic(DecisionTopics.DECISION_COMPLETED)
                .eventType("decision.completed")
                .schemaVersion("1.0")
                .tenantId(request.getTenantId())
                .entityId(request.getEntityId())
                .payload(Map.of(
                        "decisionId", record.getId().toString(),
                        "outcome", record.getOutcome(),
                        "riskScore", record.getRiskScore()
                ))
                .occurredAt(record.getEvaluatedAt())
                .publishedAt(Instant.now())
                .build();

        kafkaTemplate.send(DecisionTopics.DECISION_COMPLETED, record.getId().toString(), event);
    }

    private DecisionResponse toResponse(DecisionRecord r) {
        return DecisionResponse.builder()
                .decisionId(r.getId().toString())
                .idempotencyKey(r.getIdempotencyKey())
                .tenantId(r.getTenantId().toString())
                .entityId(r.getEntityId())
                .eventType(com.jumarkot.contracts.decision.DecisionEventType.valueOf(r.getEventType()))
                .outcome(com.jumarkot.contracts.decision.DecisionOutcome.valueOf(r.getOutcome()))
                .riskScore(r.getRiskScore())
                .reasonCodes(r.getReasonCodes())
                .recommendedAction(r.getRecommendedAction())
                .evaluatedAt(r.getEvaluatedAt())
                .processingTimeMs(r.getProcessingTimeMs() != null ? r.getProcessingTimeMs() : 0L)
                .build();
    }
}

package com.jumarkot.decision.kafka;

import com.jumarkot.contracts.decision.RiskDecision;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
public class DecisionEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(DecisionEventPublisher.class);
    private static final String TOPIC = "decisions.created";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public DecisionEventPublisher(KafkaTemplate<String, String> kafkaTemplate,
                                  ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publish(String tenantId, String decisionId, RiskDecision decision,
                        int riskScore, String entityId) {
        Map<String, Object> event = Map.of(
                "eventType", "decision.created",
                "tenantId", tenantId,
                "decisionId", decisionId,
                "decision", decision.name(),
                "riskScore", riskScore,
                "entityId", entityId,
                "timestamp", Instant.now().toString()
        );
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, tenantId, payload)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish decision.created event: decisionId={}", decisionId, ex);
                        }
                    });
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize decision event: decisionId={}", decisionId, e);
        }
    }
}

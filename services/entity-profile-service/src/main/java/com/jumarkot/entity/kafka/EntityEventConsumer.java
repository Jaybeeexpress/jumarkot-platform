package com.jumarkot.entity.kafka;

import com.jumarkot.entity.service.EntityEventProjectionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EntityEventConsumer {

    private final EntityEventProjectionService projectionService;

    public EntityEventConsumer(EntityEventProjectionService projectionService) {
        this.projectionService = projectionService;
    }

    @KafkaListener(topics = "${jumarkot.entity-profile.events-topic}")
    public void consume(String payload) {
        projectionService.project(payload);
    }
}

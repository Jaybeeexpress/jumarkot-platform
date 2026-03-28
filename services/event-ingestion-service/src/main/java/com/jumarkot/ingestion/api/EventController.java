package com.jumarkot.ingestion.api;

import com.jumarkot.contracts.common.ApiResponse;
import com.jumarkot.contracts.event.EventPayload;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/events")
public class EventController {

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> ingest(@Valid @RequestBody EventPayload event) {
        // Phase 1 stub: validate + acknowledge, Kafka fan-out added in Phase 2
        String eventId = UUID.randomUUID().toString();
        return ResponseEntity.accepted()
                .body(ApiResponse.ok(Map.of("eventId", eventId, "status", "ACCEPTED")));
    }
}

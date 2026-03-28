package com.jumarkot.ingestion.controller;

import com.jumarkot.contracts.common.ApiResponse;
import com.jumarkot.contracts.decision.DecisionRequest;
import com.jumarkot.ingestion.domain.IngestedEvent;
import com.jumarkot.ingestion.service.EventIngestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/events")
@RequiredArgsConstructor
public class EventIngestionController {

    private final EventIngestionService ingestionService;

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> ingest(
            @Valid @RequestBody DecisionRequest request) {
        IngestedEvent event = ingestionService.ingest(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponse.ok(Map.of(
                        "eventId", event.getId().toString(),
                        "status", event.getStatus()
                )));
    }
}

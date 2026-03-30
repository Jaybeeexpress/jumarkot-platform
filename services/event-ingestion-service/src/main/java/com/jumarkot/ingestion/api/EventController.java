package com.jumarkot.ingestion.api;

import com.jumarkot.contracts.common.ApiResponse;
import com.jumarkot.contracts.event.EventPayload;
import com.jumarkot.ingestion.service.EventIngestionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/v1/events")
@Validated
public class EventController {

    private final EventIngestionService eventIngestionService;

    public EventController(EventIngestionService eventIngestionService) {
        this.eventIngestionService = eventIngestionService;
    }

    @GetMapping
    public ApiResponse<List<RecentEventResponse>> recent(
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) int limit) {
        return ApiResponse.ok(eventIngestionService.recent(limit).stream()
                .map(event -> new RecentEventResponse(
                        event.eventId().toString(),
                        event.eventType(),
                        event.entityId(),
                        event.entityType(),
                        event.ingestionStatus(),
                        event.deliveryStatus(),
                        event.createdAt(),
                        event.publishedAt(),
                        event.idempotencyKey()
                ))
                .toList());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EventIngestResponse>> ingest(@Valid @RequestBody EventPayload event) {
        EventIngestionService.IngestionResult result = eventIngestionService.ingest(event);
        ApiResponse<EventIngestResponse> response = ApiResponse.ok(new EventIngestResponse(
                result.eventId().toString(),
                result.status(),
                result.acceptedAt(),
                result.duplicate()
        ));

        return result.duplicate()
                ? ResponseEntity.ok(response)
                : ResponseEntity.accepted().body(response);
    }

    public record EventIngestResponse(
            String eventId,
            String status,
            Instant acceptedAt,
            boolean duplicate
    ) {}

        public record RecentEventResponse(
            String eventId,
            String eventType,
            String entityId,
            String entityType,
            String ingestionStatus,
            String deliveryStatus,
            Instant acceptedAt,
            Instant publishedAt,
            String idempotencyKey
        ) {}
}

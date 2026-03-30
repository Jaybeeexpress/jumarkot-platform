package com.jumarkot.ingestion.api;

import com.jumarkot.contracts.common.ApiResponse;
import com.jumarkot.contracts.event.EventPayload;
import com.jumarkot.ingestion.service.EventIngestionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/v1/events")
public class EventController {

    private final EventIngestionService eventIngestionService;

    public EventController(EventIngestionService eventIngestionService) {
        this.eventIngestionService = eventIngestionService;
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
}

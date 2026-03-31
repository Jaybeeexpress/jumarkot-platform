package com.jumarkot.ingestion.service;

import com.jumarkot.contracts.event.EventPayload;
import com.jumarkot.contracts.event.EventType;
import com.jumarkot.ingestion.domain.IngestedEvent;
import com.jumarkot.ingestion.kafka.IngestionEventPublisher;
import com.jumarkot.ingestion.repository.IngestedEventRepository;
import com.jumarkot.shared.auth.TenantContext;
import com.jumarkot.shared.auth.TenantContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventIngestionServiceTest {

    @Mock
    private IngestedEventRepository repository;

    @Mock
    private IngestionEventPublisher ingestionEventPublisher;

    @InjectMocks
    private EventIngestionService service;

    @AfterEach
    void clearTenantContext() {
        TenantContextHolder.clear();
    }

    @Test
    void insertsNewEventWhenIdempotencyKeyIsNew() {
        TenantContextHolder.set(tenantContext());
        EventPayload payload = payload();

        when(repository.findByTenantIdAndIdempotencyKey(tenantContext().tenantId(), payload.idempotencyKey()))
                .thenReturn(Optional.empty());

        EventIngestionService.IngestionResult result = service.ingest(payload);

        ArgumentCaptor<IngestedEvent> captor = ArgumentCaptor.forClass(IngestedEvent.class);
        verify(repository).insert(captor.capture());

        IngestedEvent inserted = captor.getValue();
        assertThat(result.duplicate()).isFalse();
        assertThat(result.status()).isEqualTo("ACCEPTED");
        assertThat(result.deliveryStatus()).isEqualTo("PENDING");
        assertThat(inserted.tenantId()).isEqualTo(tenantContext().tenantId());
        assertThat(inserted.idempotencyKey()).isEqualTo(payload.idempotencyKey());
        assertThat(inserted.eventType()).isEqualTo(payload.eventType().name());
        verify(ingestionEventPublisher).publish(inserted);
    }

    @Test
    void returnsExistingEventWhenIdempotencyKeyAlreadyExists() {
        TenantContextHolder.set(tenantContext());
        EventPayload payload = payload();
        UUID existingId = UUID.fromString("55555555-5555-5555-5555-555555555555");
        Instant createdAt = Instant.parse("2026-03-30T02:15:00Z");

        when(repository.findByTenantIdAndIdempotencyKey(tenantContext().tenantId(), payload.idempotencyKey()))
            .thenReturn(Optional.of(new IngestedEventRepository.EventReceipt(existingId, "ACCEPTED", "PUBLISHED", Instant.parse("2026-03-30T02:15:10Z"), createdAt)));

        EventIngestionService.IngestionResult result = service.ingest(payload);

        assertThat(result.duplicate()).isTrue();
        assertThat(result.eventId()).isEqualTo(existingId);
        assertThat(result.acceptedAt()).isEqualTo(createdAt);
        assertThat(result.deliveryStatus()).isEqualTo("PUBLISHED");
        verify(repository, never()).insert(org.mockito.ArgumentMatchers.any());
        verify(ingestionEventPublisher, never()).publish(org.mockito.ArgumentMatchers.any());
        }

        @Test
        void returnsRecentEventsForCurrentTenant() {
        TenantContextHolder.set(tenantContext());
        Instant createdAt = Instant.parse("2026-03-30T02:15:00Z");

        when(repository.findRecentByTenantId(tenantContext().tenantId(), 5))
            .thenReturn(java.util.List.of(new IngestedEventRepository.RecentEventRecord(
                UUID.fromString("55555555-5555-5555-5555-555555555555"),
                "evt-unit-001",
                "LOGIN_ATTEMPT",
                "user-123",
                "USER",
                "ACCEPTED",
                "PUBLISHED",
                createdAt,
                createdAt.plusSeconds(1)
            )));

        java.util.List<EventIngestionService.RecentEvent> events = service.recent(5);

        assertThat(events).hasSize(1);
        assertThat(events.get(0).deliveryStatus()).isEqualTo("PUBLISHED");
        assertThat(events.get(0).idempotencyKey()).isEqualTo("evt-unit-001");
    }

    private TenantContext tenantContext() {
        return new TenantContext(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                null,
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                TenantContext.EnvironmentType.SANDBOX,
                java.util.List.of("events:write"),
                UUID.fromString("33333333-3333-3333-3333-333333333333")
        );
    }

    private EventPayload payload() {
        return new EventPayload(
                "evt-unit-001",
                EventType.LOGIN_ATTEMPT,
                "user-123",
                "USER",
                Instant.parse("2026-03-30T02:00:00Z"),
                Map.of("channel", "web"),
                "127.0.0.1",
                "device-abc",
                "session-123",
                "JUnit"
        );
    }
}
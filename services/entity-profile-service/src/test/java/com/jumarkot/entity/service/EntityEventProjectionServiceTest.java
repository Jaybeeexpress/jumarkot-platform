package com.jumarkot.entity.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumarkot.entity.domain.EntityProfileSnapshot;
import com.jumarkot.entity.repository.EntityProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EntityEventProjectionServiceTest {

    @Mock
    private EntityProfileRepository repository;

    private EntityEventProjectionService projectionService;

    @BeforeEach
    void setUp() {
        projectionService = new EntityEventProjectionService(repository, new ObjectMapper());
    }

    @Test
    void project_upsertsEntityProfileSnapshotForValidEvent() {
        projectionService.project("""
                {
                  "eventId": "11111111-1111-1111-1111-111111111111",
                  "tenantId": "22222222-2222-2222-2222-222222222222",
                  "environmentType": "SANDBOX",
                  "idempotencyKey": "evt-123",
                  "eventName": "LOGIN_ATTEMPT",
                  "entityId": "user-123",
                  "entityType": "USER",
                  "occurredAt": "2026-03-30T02:00:00Z",
                  "acceptedAt": "2026-03-30T02:00:02Z",
                  "properties": {
                    "channel": "web"
                  }
                }
                """);

        ArgumentCaptor<EntityProfileSnapshot> captor = ArgumentCaptor.forClass(EntityProfileSnapshot.class);
        verify(repository).upsert(captor.capture());

        EntityProfileSnapshot snapshot = captor.getValue();
        assertThat(snapshot.entityId()).isEqualTo("user-123");
        assertThat(snapshot.latestEventType()).isEqualTo("LOGIN_ATTEMPT");
        assertThat(snapshot.environmentType()).isEqualTo("SANDBOX");
        assertThat(snapshot.latestProperties()).containsEntry("channel", "web");
    }

    @Test
    void project_ignoresInvalidPayload() {
        projectionService.project("{ invalid-json }");

        verify(repository, never()).upsert(org.mockito.ArgumentMatchers.any());
    }
}

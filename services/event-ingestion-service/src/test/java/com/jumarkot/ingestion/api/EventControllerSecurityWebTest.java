package com.jumarkot.ingestion.api;

import com.jumarkot.ingestion.config.SecurityConfig;
import com.jumarkot.ingestion.service.EventIngestionService;
import com.jumarkot.shared.auth.ApiKeyResolver;
import com.jumarkot.shared.auth.InvalidApiKeyException;
import com.jumarkot.shared.auth.TenantContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EventController.class)
@Import(SecurityConfig.class)
class EventControllerSecurityWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ApiKeyResolver apiKeyResolver;

    @MockitoBean
    private EventIngestionService eventIngestionService;

    @Test
    void invalidApiKey_returnsForbiddenWithErrorEnvelope() throws Exception {
        when(apiKeyResolver.resolve(anyString())).thenThrow(new InvalidApiKeyException("invalid"));

        mockMvc.perform(post("/v1/events")
                        .header("X-API-Key", "bad-key")
                        .header("X-Request-Id", "req-events-invalid-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("INVALID_API_KEY"))
                .andExpect(jsonPath("$.message").value("Invalid API key"))
                .andExpect(jsonPath("$.requestId").value("req-events-invalid-001"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(eventIngestionService, never()).ingest(any());
    }

    @Test
    void missingApiKey_returnsForbiddenWithErrorEnvelope() throws Exception {
        mockMvc.perform(post("/v1/events")
                        .header("X-Request-Id", "req-events-missing-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("API_KEY_REQUIRED"))
                .andExpect(jsonPath("$.message").value("API key is required"))
                .andExpect(jsonPath("$.requestId").value("req-events-missing-001"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(eventIngestionService, never()).ingest(any());
    }

    @Test
    void missingEventsScope_returnsForbiddenWithErrorEnvelope() throws Exception {
        when(apiKeyResolver.resolve(anyString())).thenReturn(new TenantContext(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                null,
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                TenantContext.EnvironmentType.SANDBOX,
                List.of("decisions:write"),
                UUID.fromString("33333333-3333-3333-3333-333333333333")
        ));

        mockMvc.perform(post("/v1/events")
                        .header("X-API-Key", "jk_test_scope_mismatch")
                        .header("X-Request-Id", "req-events-scope-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("API_KEY_REQUIRED"))
                .andExpect(jsonPath("$.message").value("API key is required"))
                .andExpect(jsonPath("$.requestId").value("req-events-scope-001"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(eventIngestionService, never()).ingest(any());
    }

    @Test
    void recentEvents_requiresEventsWriteScope() throws Exception {
        when(apiKeyResolver.resolve(anyString())).thenReturn(new TenantContext(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                null,
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                TenantContext.EnvironmentType.SANDBOX,
                List.of("decisions:write"),
                UUID.fromString("33333333-3333-3333-3333-333333333333")
        ));

        mockMvc.perform(get("/v1/events")
                        .param("limit", "5")
                        .header("X-API-Key", "jk_test_scope_mismatch")
                        .header("X-Request-Id", "req-events-recent-scope-001"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("API_KEY_REQUIRED"));
    }

    private String validPayload() {
        return """
                {
                  "idempotencyKey": "evt-web-001",
                  "eventType": "LOGIN_ATTEMPT",
                  "entityId": "user-123",
                  "entityType": "USER",
                  "occurredAt": "2026-03-30T02:00:00Z",
                  "properties": {
                    "riskBand": "medium"
                  },
                  "ipAddress": "127.0.0.1",
                  "deviceId": "device-abc",
                  "sessionId": "session-123",
                  "userAgent": "JUnit"
                }
                """;
    }
}
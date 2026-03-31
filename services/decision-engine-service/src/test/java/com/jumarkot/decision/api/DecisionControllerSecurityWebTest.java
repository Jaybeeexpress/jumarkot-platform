package com.jumarkot.decision.api;

import com.jumarkot.decision.client.RulesClient;
import com.jumarkot.decision.service.DecisionService;
import com.jumarkot.shared.auth.ApiKeyResolver;
import com.jumarkot.shared.auth.InvalidApiKeyException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DecisionControllerSecurityWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ApiKeyResolver apiKeyResolver;

    @MockitoBean
    private DecisionService decisionService;

    @MockitoBean
    private RulesClient rulesClient;

    @MockitoBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void invalidApiKey_returnsForbidden_andSkipsDecisionService() throws Exception {
        when(apiKeyResolver.resolve(anyString())).thenThrow(new InvalidApiKeyException("invalid"));

        String requestBody = """
                {
                  "idempotencyKey": "webtest-001",
                  "entityId": "entity-1",
                  "entityType": "ACCOUNT",
                  "eventType": "PAYMENT",
                  "payload": {
                    "amount": 100
                  }
                }
                """;

        mockMvc.perform(post("/v1/decisions")
                        .header("X-API-Key", "bad-key")
            .header("X-Request-Id", "req-decision-invalid-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
          .andExpect(status().isForbidden())
          .andExpect(jsonPath("$.success").value(false))
          .andExpect(jsonPath("$.errorCode").value("INVALID_API_KEY"))
          .andExpect(jsonPath("$.message").value("Invalid API key"))
          .andExpect(jsonPath("$.requestId").value("req-decision-invalid-001"))
          .andExpect(jsonPath("$.timestamp").exists());

        verify(decisionService, never()).decide(org.mockito.ArgumentMatchers.any());
          }

          @Test
          void missingApiKey_returnsForbiddenWithErrorEnvelope() throws Exception {
        String requestBody = """
          {
            \"idempotencyKey\": \"webtest-002\",
            \"entityId\": \"entity-1\",
            \"entityType\": \"ACCOUNT\",
            \"eventType\": \"PAYMENT\",
            \"payload\": {
              \"amount\": 100
            }
          }
          """;

        mockMvc.perform(post("/v1/decisions")
            .header("X-Request-Id", "req-decision-missing-001")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
          .andExpect(status().isForbidden())
          .andExpect(jsonPath("$.success").value(false))
          .andExpect(jsonPath("$.errorCode").value("API_KEY_REQUIRED"))
          .andExpect(jsonPath("$.message").value("API key is required"))
          .andExpect(jsonPath("$.requestId").value("req-decision-missing-001"))
          .andExpect(jsonPath("$.timestamp").exists());

        verify(decisionService, never()).decide(org.mockito.ArgumentMatchers.any());
    }
}

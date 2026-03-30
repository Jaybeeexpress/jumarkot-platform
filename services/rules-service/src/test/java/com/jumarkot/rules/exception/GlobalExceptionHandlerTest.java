package com.jumarkot.rules.exception;

import com.jumarkot.rules.api.RuleController;
import com.jumarkot.rules.repository.RuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private RuleRepository ruleRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new RuleController(ruleRepository))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void missingRequiredFields_returns400_withValidationErrorAndFieldErrors() throws Exception {
        mockMvc.perform(post("/v1/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                                                .content("""
                                                                {
                                                                    \"tenantId\": \"11111111-1111-1111-1111-111111111111\",
                                                                    \"environmentType\": \"SANDBOX\",
                                                                    \"name\": \"\",
                                                                    \"category\": \"RISK\",
                                                                    \"priority\": 10,
                                                                    \"conditions\": [],
                                                                    \"conditionLogic\": \"AND\",
                                                                    \"action\": \"ALLOW\",
                                                                    \"scoreAdjustment\": 5,
                                                                    \"reasonCode\": \"R1\"
                                                                }
                                                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.fieldErrors").isArray())
                .andExpect(jsonPath("$.fieldErrors[0].field").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void malformedJson_returns400_withInvalidRequest() throws Exception {
        mockMvc.perform(post("/v1/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("not-valid-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value("Malformed or missing request body"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void updateStatus_ruleNotFound_returns404_withRuleNotFoundCode() throws Exception {
        UUID ruleId = UUID.randomUUID();
        doThrow(new RuleNotFoundException(ruleId))
                .when(ruleRepository).updateStatus(any(UUID.class), any(UUID.class), anyString());

        mockMvc.perform(put("/v1/rules/{ruleId}/status", ruleId)
                        .param("tenantId", UUID.randomUUID().toString())
                        .param("status", "INACTIVE"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("RULE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Rule not found: " + ruleId))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void requestIdHeader_isForwardedIntoErrorResponse() throws Exception {
        mockMvc.perform(post("/v1/rules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .header("X-Request-Id", "req-xyz-789"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.requestId").value("req-xyz-789"));
    }
}

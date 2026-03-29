package com.jumarkot.identity.exception;

import com.jumarkot.identity.api.ApiKeyController;
import com.jumarkot.identity.service.ApiKeyNotFoundException;
import com.jumarkot.identity.service.ApiKeyService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private ApiKeyService apiKeyService;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ApiKeyController(apiKeyService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void missingRequiredFields_returns400_withValidationErrorAndFieldErrors() throws Exception {
        mockMvc.perform(post("/v1/api-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
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
        mockMvc.perform(post("/v1/api-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("not-valid-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value("Malformed or missing request body"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void revoke_keyNotFound_returns404_withApiKeyNotFoundCode() throws Exception {
        UUID keyId = UUID.randomUUID();
        doThrow(new ApiKeyNotFoundException("Key not found: " + keyId))
                .when(apiKeyService).revoke(any(UUID.class), any(UUID.class), anyString());

        mockMvc.perform(delete("/v1/api-keys/{keyId}", keyId)
                        .param("tenantId", UUID.randomUUID().toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("API_KEY_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Key not found: " + keyId))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void requestIdHeader_isForwardedIntoErrorResponse() throws Exception {
        mockMvc.perform(post("/v1/api-keys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .header("X-Request-Id", "req-iam-001"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.requestId").value("req-iam-001"));
    }
}

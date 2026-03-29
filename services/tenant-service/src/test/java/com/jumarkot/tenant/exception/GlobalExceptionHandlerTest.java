package com.jumarkot.tenant.exception;

import com.jumarkot.tenant.api.TenantController;
import com.jumarkot.tenant.service.TenantNotFoundException;
import com.jumarkot.tenant.service.TenantService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private TenantService tenantService;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TenantController(tenantService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void missingRequiredFields_returns400_withValidationErrorAndFieldErrors() throws Exception {
        mockMvc.perform(post("/v1/tenants")
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
        mockMvc.perform(post("/v1/tenants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("not-valid-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value("Malformed or missing request body"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void getTenant_notFound_returns404_withTenantNotFoundCode() throws Exception {
        UUID tenantId = UUID.randomUUID();
        when(tenantService.findById(any(UUID.class)))
                .thenThrow(new TenantNotFoundException("Tenant not found: " + tenantId));

        mockMvc.perform(get("/v1/tenants/{tenantId}", tenantId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("TENANT_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Tenant not found: " + tenantId))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void requestIdHeader_isForwardedIntoErrorResponse() throws Exception {
        mockMvc.perform(post("/v1/tenants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                        .header("X-Request-Id", "req-tenant-999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.requestId").value("req-tenant-999"));
    }
}

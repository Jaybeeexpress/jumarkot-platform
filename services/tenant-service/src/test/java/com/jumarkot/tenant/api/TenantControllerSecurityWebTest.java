package com.jumarkot.tenant.api;

import com.jumarkot.tenant.config.SecurityConfig;
import com.jumarkot.tenant.service.TenantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TenantController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
        "spring.security.user.name=admin",
        "spring.security.user.password=changeme"
})
class TenantControllerSecurityWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TenantService tenantService;

    @Test
    void missingBasicAuth_returnsUnauthorizedWithErrorEnvelope() throws Exception {
        mockMvc.perform(post("/v1/tenants")
                        .header("X-Request-Id", "req-tenant-auth-missing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"Jumarkot\""))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("BASIC_AUTH_REQUIRED"))
                .andExpect(jsonPath("$.message").value("Basic authentication is required"))
                .andExpect(jsonPath("$.requestId").value("req-tenant-auth-missing"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(tenantService, never()).provision(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void invalidBasicAuth_returnsUnauthorizedWithErrorEnvelope() throws Exception {
        mockMvc.perform(post("/v1/tenants")
                                                .header(HttpHeaders.AUTHORIZATION, basicAuth("admin", "wrong-password"))
                        .header("X-Request-Id", "req-tenant-auth-invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"Jumarkot\""))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("INVALID_BASIC_CREDENTIALS"))
                .andExpect(jsonPath("$.message").value("Invalid username or password"))
                .andExpect(jsonPath("$.requestId").value("req-tenant-auth-invalid"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(tenantService, never()).provision(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

        private String basicAuth(String username, String password) {
                String value = username + ":" + password;
                return "Basic " + Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
        }
}
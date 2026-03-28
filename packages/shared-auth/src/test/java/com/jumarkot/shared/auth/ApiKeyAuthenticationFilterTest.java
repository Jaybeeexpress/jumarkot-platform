package com.jumarkot.shared.auth;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApiKeyAuthenticationFilterTest {

    @AfterEach
    void cleanup() {
        TenantContextHolder.clear();
        SecurityContextHolder.clearContext();
    }

    @Test
    void invalidApiKey_doesNotAuthenticate_andContinuesFilterChain() throws Exception {
        ApiKeyResolver resolver = mock(ApiKeyResolver.class);
        when(resolver.resolve("bad-key")).thenThrow(new InvalidApiKeyException("invalid"));

        ApiKeyAuthenticationFilter filter = new ApiKeyAuthenticationFilter(resolver);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-API-Key", "bad-key");
        MockHttpServletResponse response = new MockHttpServletResponse();

        AtomicBoolean chainCalled = new AtomicBoolean(false);
        FilterChain chain = (req, res) -> {
            chainCalled.set(true);
            assertNull(SecurityContextHolder.getContext().getAuthentication());
            assertFalse(TenantContextHolder.isPresent());
        };

        filter.doFilter(request, response, chain);

        assertTrue(chainCalled.get());
        assertFalse(TenantContextHolder.isPresent());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertFalse(response.isCommitted());
    }

    @Test
    void validApiKey_setsAuthentication_andClearsThreadLocalAfterChain() throws Exception {
        TenantContext tenantContext = new TenantContext(
                UUID.randomUUID(),
                "acme",
                UUID.randomUUID(),
                TenantContext.EnvironmentType.PRODUCTION,
                List.of("*"),
                UUID.randomUUID()
        );

        ApiKeyResolver resolver = mock(ApiKeyResolver.class);
        when(resolver.resolve("good-key")).thenReturn(tenantContext);

        ApiKeyAuthenticationFilter filter = new ApiKeyAuthenticationFilter(resolver);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-API-Key", "good-key");
        MockHttpServletResponse response = new MockHttpServletResponse();

        AtomicBoolean chainSawAuth = new AtomicBoolean(false);
        FilterChain chain = (req, res) -> {
            chainSawAuth.set(SecurityContextHolder.getContext().getAuthentication() instanceof ApiKeyAuthentication);
            assertFalse(!TenantContextHolder.isPresent());
        };

        filter.doFilter(request, response, chain);

        assertTrue(chainSawAuth.get());
        assertFalse(TenantContextHolder.isPresent());
    }
}

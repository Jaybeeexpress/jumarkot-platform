package com.jumarkot.decision.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RulesClientAuthTest {

    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void getActiveRules_sendsBasicAuthHeaderToInternalRulesEndpoint() throws Exception {
        StringRedisTemplate redis = mock(StringRedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> valueOps = mock(ValueOperations.class);
        when(redis.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(anyString())).thenReturn(null);

        mockWebServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody("[]")
                .setResponseCode(200));

        RulesClient client = new RulesClient(
                WebClient.builder(),
                mockWebServer.url("/").toString(),
                "admin",
                "changeme",
                redis,
                new ObjectMapper()
        );

        client.getActiveRules(UUID.randomUUID(), "PRODUCTION");

        RecordedRequest request = mockWebServer.takeRequest();
        assertNotNull(request);
        assertEquals("GET", request.getMethod());

        String expected = "Basic " + Base64.getEncoder()
                .encodeToString("admin:changeme".getBytes(StandardCharsets.UTF_8));
        assertEquals(expected, request.getHeader("Authorization"));

        verify(valueOps).set(anyString(), anyString(), eq(java.time.Duration.ofSeconds(60)));
    }
}

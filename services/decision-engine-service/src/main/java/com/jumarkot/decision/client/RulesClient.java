package com.jumarkot.decision.client;

import com.jumarkot.decision.config.RulesServiceProperties;
import com.jumarkot.decision.rules.RuleDto;
import io.netty.channel.ChannelOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * Loads active rules for a tenant+environment from rules-service.
 *
 * <p>Results are cached in Redis with a 60-second TTL to minimise
 * synchronous call overhead on the decision hot path. rules-service
 * should evict the cache key on rule mutations (future improvement).
 */
@Component
public class RulesClient {

    private static final Logger log = LoggerFactory.getLogger(RulesClient.class);
    private static final Duration RULES_CACHE_TTL = Duration.ofSeconds(60);
    private static final String CACHE_PREFIX = "rules:active:";

    private final WebClient webClient;
    private final StringRedisTemplate redis;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;
    private final String rulesServiceUser;
    private final String rulesServicePassword;
        private final Duration blockTimeout;

    public RulesClient(WebClient.Builder builder,
                   RulesServiceProperties rulesServiceProperties,
                       StringRedisTemplate redis,
                       com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
        HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
                Math.toIntExact(rulesServiceProperties.getConnectTimeout().toMillis()))
            .responseTimeout(rulesServiceProperties.getReadTimeout());

        this.webClient = builder
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .baseUrl(rulesServiceProperties.getBaseUrl())
            .build();
        this.rulesServiceUser = rulesServiceProperties.getUsername();
        this.rulesServicePassword = rulesServiceProperties.getPassword();
        this.blockTimeout = rulesServiceProperties.getBlockTimeout();
        this.redis = redis;
        this.objectMapper = objectMapper;
    }

    public List<RuleDto> getActiveRules(UUID tenantId, String environmentType) {
        String cacheKey = CACHE_PREFIX + tenantId + ":" + environmentType;

        String cached = redis.opsForValue().get(cacheKey);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, RuleDto.class));
            } catch (Exception e) {
                log.warn("Failed to deserialize cached rules, reloading from rules-service", e);
            }
        }

        List<RuleDto> rules = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/internal/v1/rules/active")
                        .queryParam("tenantId", tenantId)
                        .queryParam("environmentType", environmentType)
                        .build())
            .header(HttpHeaders.AUTHORIZATION, basicAuthHeader())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<RuleDto>>() {})
                        .block(blockTimeout);

        if (rules == null) rules = List.of();

        try {
            redis.opsForValue().set(cacheKey, objectMapper.writeValueAsString(rules), RULES_CACHE_TTL);
        } catch (Exception e) {
            log.warn("Failed to cache rules in Redis", e);
        }

        return rules;
    }

    private String basicAuthHeader() {
        String credentials = rulesServiceUser + ":" + rulesServicePassword;
        return "Basic " + java.util.Base64.getEncoder().encodeToString(credentials.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}

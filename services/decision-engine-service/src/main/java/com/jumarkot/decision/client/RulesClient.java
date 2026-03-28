package com.jumarkot.decision.client;

import com.jumarkot.rules.dto.RuleDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

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

    public RulesClient(WebClient.Builder builder,
                       @Value("${jumarkot.rules-service.base-url}") String baseUrl,
                       StringRedisTemplate redis,
                       com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
        this.webClient = builder.baseUrl(baseUrl).build();
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
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<RuleDto>>() {})
                .block(Duration.ofSeconds(5));

        if (rules == null) rules = List.of();

        try {
            redis.opsForValue().set(cacheKey, objectMapper.writeValueAsString(rules), RULES_CACHE_TTL);
        } catch (Exception e) {
            log.warn("Failed to cache rules in Redis", e);
        }

        return rules;
    }
}

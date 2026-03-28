package com.jumarkot.ingestion.config;

import com.jumarkot.shared.auth.ApiKeyAuthenticationFilter;
import com.jumarkot.shared.auth.RedisApiKeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           ApiKeyAuthenticationFilter apiKeyFilter) throws Exception {
        return http
                .csrf(c -> c.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .anyRequest().authenticated())
                .build();
    }

    @Bean
    public RedisApiKeyResolver redisApiKeyResolver(StringRedisTemplate redis,
                                                   com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
        return new RedisApiKeyResolver(redis, objectMapper);
    }

    @Bean
    public ApiKeyAuthenticationFilter apiKeyAuthenticationFilter(RedisApiKeyResolver resolver) {
        return new ApiKeyAuthenticationFilter(resolver);
    }
}

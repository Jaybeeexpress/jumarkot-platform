package com.jumarkot.decision.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumarkot.shared.auth.ApiKeyAuthenticationFilter;
import com.jumarkot.shared.auth.ApiKeyResolver;
import com.jumarkot.shared.auth.RedisApiKeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public ApiKeyResolver apiKeyResolver(StringRedisTemplate redis, ObjectMapper objectMapper) {
        return new RedisApiKeyResolver(redis, objectMapper);
    }

    @Bean
    public ApiKeyAuthenticationFilter apiKeyAuthenticationFilter(ApiKeyResolver apiKeyResolver) {
        return new ApiKeyAuthenticationFilter(apiKeyResolver);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           ApiKeyAuthenticationFilter apiKeyFilter) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/actuator/info", "/error").permitAll()
                .anyRequest().authenticated()
            );

        return http.build();
    }
}

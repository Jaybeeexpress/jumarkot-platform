package com.jumarkot.decision.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumarkot.contracts.common.ErrorResponse;
import com.jumarkot.shared.auth.ApiKeyAuthenticationFilter;
import com.jumarkot.shared.auth.ApiKeyResolver;
import com.jumarkot.shared.auth.RedisApiKeyResolver;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
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
                                           ApiKeyAuthenticationFilter apiKeyFilter,
                                           ObjectMapper objectMapper) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/actuator/info", "/error").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) ->
                        writeSecurityError(request, response, objectMapper))
                .accessDeniedHandler((request, response, accessDeniedException) ->
                        writeSecurityError(request, response, objectMapper))
            );

        return http.build();
    }

    private void writeSecurityError(jakarta.servlet.http.HttpServletRequest request,
                                    HttpServletResponse response,
                                    ObjectMapper objectMapper) throws java.io.IOException {
        boolean invalidApiKey = request.getAttribute(ApiKeyAuthenticationFilter.INVALID_API_KEY_REQUEST_ATTRIBUTE) != null;
        ErrorResponse errorResponse = invalidApiKey
                ? ErrorResponse.of("INVALID_API_KEY", "Invalid API key", request.getHeader("X-Request-Id"))
                : ErrorResponse.of("API_KEY_REQUIRED", "API key is required", request.getHeader("X-Request-Id"));

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}

package com.jumarkot.rules.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumarkot.contracts.common.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for rules-service.
 *
 * <p>rules-service is an internal service reached by platform operators (for rule
 * authoring) and by the decision-engine-service (via its internal rule fetch
 * endpoint). It uses HTTP Basic auth with credentials configured in application.yml.
 *
 * <p>The {@code /internal/rules} path used by decision-engine is also covered by
 * Basic auth so that service-to-service calls carry a shared secret header in
 * production.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, ObjectMapper objectMapper) throws Exception {
        http
            // No CSRF needed — stateless REST API
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Infra probes and Spring error dispatch — no credentials required
                .requestMatchers("/actuator/health", "/actuator/info", "/error").permitAll()
                // All other endpoints (public + internal) require Basic auth
                .anyRequest().authenticated()
            )
            // HTTP Basic auth using credentials from spring.security.user in application.yml
                .httpBasic(basic -> basic.authenticationEntryPoint(
                    (request, response, authException) -> writeUnauthorizedResponse(request, response, objectMapper)))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) ->
                        writeUnauthorizedResponse(request, response, objectMapper))
                .accessDeniedHandler((request, response, accessDeniedException) ->
                        writeAccessDeniedResponse(request, response, objectMapper))
            );

        return http.build();
    }

    private void writeUnauthorizedResponse(HttpServletRequest request,
                                           HttpServletResponse response,
                                           ObjectMapper objectMapper) throws java.io.IOException {
        boolean hasBasicHeader = hasBasicAuthorizationHeader(request);
        ErrorResponse errorResponse = hasBasicHeader
                ? ErrorResponse.of("INVALID_BASIC_CREDENTIALS", "Invalid username or password", request.getHeader("X-Request-Id"))
                : ErrorResponse.of("BASIC_AUTH_REQUIRED", "Basic authentication is required", request.getHeader("X-Request-Id"));

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"Jumarkot\"");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }

    private void writeAccessDeniedResponse(HttpServletRequest request,
                                           HttpServletResponse response,
                                           ObjectMapper objectMapper) throws java.io.IOException {
        ErrorResponse errorResponse = ErrorResponse.of(
                "ACCESS_DENIED", "Access denied", request.getHeader("X-Request-Id"));
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }

    private boolean hasBasicAuthorizationHeader(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        return authorization != null && authorization.startsWith("Basic ");
    }
}

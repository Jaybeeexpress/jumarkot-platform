package com.jumarkot.identity.config;

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
 * identity-access-service is an internal service reached by other services and
 * platform operators. It uses HTTP Basic or bearer token auth for management
 * endpoints, and exposes unauthenticated health/actuator probes.
 *
 * API key management endpoints require an ADMIN JWT or a master service token.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, ObjectMapper objectMapper) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                // Internal API — protected by network policy in production;
                // callers use a service-level shared secret header.
                .anyRequest().authenticated()
            )
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

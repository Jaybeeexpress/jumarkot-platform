package com.jumarkot.tenant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for tenant-service.
 *
 * <p>tenant-service is an internal admin service consumed by platform operators and
 * automation tooling. It uses HTTP Basic auth so callers supply a configured
 * username/password (see {@code spring.security.user} in application.yml).
 *
 * <p>In production, the service sits behind a network boundary and the Basic
 * credentials are rotated via secrets management. Unauthenticated access is only
 * permitted for health/liveness probes and the error dispatcher.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // No CSRF needed — stateless REST API
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Infra probes and Spring error dispatch — no credentials required
                .requestMatchers("/actuator/health", "/actuator/info", "/error").permitAll()
                // All other endpoints require Basic auth
                .anyRequest().authenticated()
            )
            // HTTP Basic auth using credentials from spring.security.user in application.yml
            .httpBasic(basic -> {});

        return http.build();
    }
}

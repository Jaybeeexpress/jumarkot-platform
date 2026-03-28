package com.jumarkot.rules.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
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
            .httpBasic(basic -> {});

        return http.build();
    }
}

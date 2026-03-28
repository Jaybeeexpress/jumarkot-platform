package com.jumarkot.shared.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Extracts and validates API keys from inbound HTTP requests.
 *
 * <p>Supported header formats:
 * <ul>
 *   <li>{@code X-API-Key: <key>}</li>
 *   <li>{@code Authorization: ApiKey <key>}</li>
 * </ul>
 *
 * <p>On success, sets a {@link ApiKeyAuthentication} in the SecurityContext
 * and populates {@link TenantContextHolder} for the request thread.
 * The thread-local is always cleared in the finally block.
 */
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ApiKeyAuthenticationFilter.class);
    private static final String API_KEY_HEADER = "X-API-Key";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String API_KEY_PREFIX = "ApiKey ";

    private final ApiKeyResolver apiKeyResolver;

    public ApiKeyAuthenticationFilter(ApiKeyResolver apiKeyResolver) {
        this.apiKeyResolver = apiKeyResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        try {
            String rawKey = extractApiKey(request);
            if (rawKey != null) {
                try {
                    TenantContext tenantContext = apiKeyResolver.resolve(rawKey);
                    ApiKeyAuthentication auth = new ApiKeyAuthentication(
                            ApiKeyHasher.mask(rawKey), tenantContext);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    TenantContextHolder.set(tenantContext);
                    log.debug("Authenticated request for tenant={} env={}",
                            tenantContext.tenantId(), tenantContext.environmentType());
                } catch (InvalidApiKeyException e) {
                    log.debug("Invalid API key presented: {}", ApiKeyHasher.mask(rawKey));
                    // Unauthenticated — let Spring Security return 401 downstream
                }
            }
            chain.doFilter(request, response);
        } finally {
            TenantContextHolder.clear();
            SecurityContextHolder.clearContext();
        }
    }

    private String extractApiKey(HttpServletRequest request) {
        String xApiKey = request.getHeader(API_KEY_HEADER);
        if (xApiKey != null && !xApiKey.isBlank()) {
            return xApiKey.trim();
        }

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader != null && authHeader.startsWith(API_KEY_PREFIX)) {
            return authHeader.substring(API_KEY_PREFIX.length()).trim();
        }

        return null;
    }
}

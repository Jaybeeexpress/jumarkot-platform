package com.jumarkot.shared.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Spring Security Authentication token populated after successful API key validation.
 * Authorities reflect the key's scopes, prefixed with "SCOPE_" to integrate
 * cleanly with Spring Security's expression-based access control.
 */
public class ApiKeyAuthentication extends AbstractAuthenticationToken {

    private final String maskedKey;
    private final TenantContext tenantContext;

    public ApiKeyAuthentication(String maskedKey, TenantContext tenantContext) {
        super(tenantContext.scopes().stream()
                .map(s -> new SimpleGrantedAuthority("SCOPE_" + s))
                .toList());
        this.maskedKey = maskedKey;
        this.tenantContext = tenantContext;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null; // raw key is never retained
    }

    @Override
    public Object getPrincipal() {
        return tenantContext.tenantId().toString();
    }

    public TenantContext getTenantContext() {
        return tenantContext;
    }

    public String getMaskedKey() {
        return maskedKey;
    }
}

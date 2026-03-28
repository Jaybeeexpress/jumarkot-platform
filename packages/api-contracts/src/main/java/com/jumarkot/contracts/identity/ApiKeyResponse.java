package com.jumarkot.contracts.identity;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.Set;

@Value
@Builder
public class ApiKeyResponse {
    String id;
    String name;
    /** Only populated on creation; never returned again. */
    String rawKey;
    String keyPrefix;
    Set<String> scopes;
    Instant expiresAt;
    Instant createdAt;
    boolean active;
}

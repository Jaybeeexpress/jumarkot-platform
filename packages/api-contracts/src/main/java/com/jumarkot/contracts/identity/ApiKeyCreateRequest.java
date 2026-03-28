package com.jumarkot.contracts.identity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder
public class ApiKeyCreateRequest {
    @NotBlank
    String name;

    /** Scopes granted to this key (e.g. "decisions:write", "events:write"). */
    @NotNull
    Set<String> scopes;

    /** Optional expiry in days; null = never expires. */
    Integer expiresInDays;
}

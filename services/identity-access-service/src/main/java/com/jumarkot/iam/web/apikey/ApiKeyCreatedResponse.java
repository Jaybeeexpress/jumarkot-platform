package com.jumarkot.iam.web.apikey;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ApiKeyCreatedResponse {

    private UUID id;
    private String name;
    private String prefix;

    /** The raw API key. Present ONLY at creation time — never stored or returned again. */
    @JsonProperty("key")
    private String rawKey;

    private List<String> scopes;

    @JsonProperty("expires_at")
    private Instant expiresAt;

    @JsonProperty("created_at")
    private Instant createdAt;
}

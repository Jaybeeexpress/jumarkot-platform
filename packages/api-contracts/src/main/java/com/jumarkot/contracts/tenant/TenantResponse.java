package com.jumarkot.contracts.tenant;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class TenantResponse {
    String id;
    String name;
    String slug;
    String plan;
    String region;
    String contactEmail;
    String status;
    Instant createdAt;
    Instant updatedAt;
}

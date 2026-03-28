package com.jumarkot.contracts.identity;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class TokenResponse {
    String accessToken;
    String refreshToken;
    String tokenType;
    long expiresIn;
    Instant issuedAt;
}

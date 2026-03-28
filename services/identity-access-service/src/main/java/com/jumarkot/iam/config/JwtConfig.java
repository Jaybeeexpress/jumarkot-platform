package com.jumarkot.iam.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "jumarkot.jwt")
public class JwtConfig {

    /** Base64-encoded PKCS#8 RSA private key. */
    private String privateKey;

    /** Base64-encoded X.509 RSA public key. */
    private String publicKey;

    /** Token issuer claim. */
    private String issuer = "jumarkot-iam";

    /** Access token TTL in seconds (default: 15 minutes). */
    private long accessTokenTtlSeconds = 900L;

    /** Refresh token TTL in seconds (default: 7 days). */
    private long refreshTokenTtlSeconds = 604_800L;
}

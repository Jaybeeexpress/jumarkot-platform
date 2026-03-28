package com.jumarkot.identity.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jumarkot.jwt")
@Data
public class JwtProperties {
    private String secret;
    private long accessTokenExpirySeconds = 900;
    private long refreshTokenExpirySeconds = 86400;
}

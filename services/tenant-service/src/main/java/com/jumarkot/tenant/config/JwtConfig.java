package com.jumarkot.tenant.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@Data
@Validated
@ConfigurationProperties(prefix = "jumarkot.jwt")
public class JwtConfig {

    @NotBlank
    private String publicKey;

    private String issuer = "jumarkot-iam";
}

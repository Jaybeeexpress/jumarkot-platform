package com.jumarkot.tenant.infrastructure.security;

import com.jumarkot.tenant.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class JwtValidator {

    private final PublicKey publicKey;
    private final String issuer;

    public JwtValidator(JwtConfig jwtConfig) {
        this.issuer = jwtConfig.getIssuer();
        this.publicKey = loadPublicKey(jwtConfig.getPublicKey());
    }

    private PublicKey loadPublicKey(String base64PublicKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(
                base64PublicKey.replaceAll("-----BEGIN PUBLIC KEY-----", "")
                               .replaceAll("-----END PUBLIC KEY-----", "")
                               .replaceAll("\\s+", "")
            );
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load RSA public key", e);
        }
    }

    public Claims validateAndGetClaims(String token) {
        return Jwts.parser()
                .verifyWith((java.security.interfaces.RSAPublicKey) publicKey)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public UUID extractUserId(Claims claims) {
        return UUID.fromString(claims.getSubject());
    }

    public UUID extractTenantId(Claims claims) {
        String tid = claims.get("tid", String.class);
        if (tid == null) {
            throw new IllegalArgumentException("Missing 'tid' claim in JWT");
        }
        return UUID.fromString(tid);
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(Claims claims) {
        Object roles = claims.get("roles");
        if (roles instanceof List<?> list) {
            return list.stream().map(Object::toString).toList();
        }
        return List.of();
    }
}

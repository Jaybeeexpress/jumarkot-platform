package com.jumarkot.iam.application.apikey;

import com.jumarkot.iam.domain.apikey.ApiKey;
import com.jumarkot.iam.domain.apikey.ApiKeyRepository;
import com.jumarkot.iam.domain.apikey.ApiKeyStatus;
import com.jumarkot.iam.web.apikey.ApiKeyCreatedResponse;
import com.jumarkot.iam.web.apikey.CreateApiKeyRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private static final String KEY_PREFIX = "jmk_";
    private static final int PREFIX_LENGTH = 6;
    private static final int RANDOM_PART_LENGTH = 32;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final ApiKeyRepository apiKeyRepository;

    @Transactional
    public ApiKeyCreatedResponse generateApiKey(UUID tenantId, UUID userId, CreateApiKeyRequest request) {
        String rawPrefix = generateRandomAlphanumeric(PREFIX_LENGTH);
        String rawRandom = generateRandomAlphanumeric(RANDOM_PART_LENGTH);
        String rawKey = KEY_PREFIX + rawPrefix + "_" + rawRandom;
        String keyHash = sha256Hex(rawKey);

        ApiKey apiKey = ApiKey.builder()
                .keyHash(keyHash)
                .prefix(rawPrefix)
                .tenantId(tenantId)
                .userId(userId)
                .name(request.getName())
                .scopes(request.getScopes() != null ? request.getScopes() : List.of())
                .expiresAt(request.getExpiresAt())
                .status(ApiKeyStatus.ACTIVE)
                .build();

        ApiKey saved = apiKeyRepository.save(apiKey);
        log.info("Generated API key '{}' (id={}) for user {} in tenant {}", request.getName(), saved.getId(), userId, tenantId);

        return ApiKeyCreatedResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .prefix(saved.getPrefix())
                .rawKey(rawKey)
                .scopes(saved.getScopes())
                .expiresAt(saved.getExpiresAt())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ApiKey> listApiKeys(UUID tenantId, UUID userId) {
        return apiKeyRepository.findByTenantIdAndUserId(tenantId, userId);
    }

    @Transactional(readOnly = true)
    public List<ApiKey> listAllTenantApiKeys(UUID tenantId) {
        return apiKeyRepository.findByTenantId(tenantId);
    }

    @Transactional
    public void revokeApiKey(UUID tenantId, UUID keyId) {
        ApiKey apiKey = apiKeyRepository.findById(keyId)
                .filter(k -> k.getTenantId().equals(tenantId))
                .orElseThrow(() -> new NoSuchElementException(
                        "API key " + keyId + " not found in tenant " + tenantId));
        apiKey.setStatus(ApiKeyStatus.REVOKED);
        apiKeyRepository.save(apiKey);
        log.info("Revoked API key {} in tenant {}", keyId, tenantId);
    }

    public String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private String generateRandomAlphanumeric(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(SECURE_RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }
}

package com.jumarkot.iam.apikey;

import com.jumarkot.iam.application.apikey.ApiKeyService;
import com.jumarkot.iam.domain.apikey.ApiKey;
import com.jumarkot.iam.domain.apikey.ApiKeyRepository;
import com.jumarkot.iam.domain.apikey.ApiKeyStatus;
import com.jumarkot.iam.web.apikey.ApiKeyCreatedResponse;
import com.jumarkot.iam.web.apikey.CreateApiKeyRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApiKeyService Tests")
class ApiKeyServiceTest {

    @Mock
    private ApiKeyRepository apiKeyRepository;

    private ApiKeyService apiKeyService;

    private final UUID tenantId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        apiKeyService = new ApiKeyService(apiKeyRepository);
    }

    @Test
    @DisplayName("generateApiKey returns a response with raw key starting with jmk_")
    void generateApiKey_validRequest_returnsKeyWithPrefix() {
        CreateApiKeyRequest request = new CreateApiKeyRequest();
        request.setName("test-key");
        request.setScopes(List.of("read:events", "write:decisions"));

        ArgumentCaptor<ApiKey> captor = ArgumentCaptor.forClass(ApiKey.class);
        when(apiKeyRepository.save(captor.capture())).thenAnswer(inv -> {
            ApiKey k = inv.getArgument(0);
            return ApiKey.builder()
                    .id(UUID.randomUUID())
                    .keyHash(k.getKeyHash())
                    .prefix(k.getPrefix())
                    .tenantId(k.getTenantId())
                    .userId(k.getUserId())
                    .name(k.getName())
                    .scopes(k.getScopes())
                    .status(ApiKeyStatus.ACTIVE)
                    .createdAt(Instant.now())
                    .build();
        });

        ApiKeyCreatedResponse response = apiKeyService.generateApiKey(tenantId, userId, request);

        assertThat(response.getRawKey()).startsWith("jmk_");
        assertThat(response.getRawKey().split("_")).hasSize(3);
        assertThat(response.getId()).isNotNull();
        assertThat(response.getName()).isEqualTo("test-key");
    }

    @Test
    @DisplayName("generateApiKey stores only the SHA-256 hash, not the raw key")
    void generateApiKey_storesHashNotRawKey() {
        CreateApiKeyRequest request = new CreateApiKeyRequest();
        request.setName("hash-test-key");
        request.setScopes(List.of());

        ArgumentCaptor<ApiKey> captor = ArgumentCaptor.forClass(ApiKey.class);
        when(apiKeyRepository.save(captor.capture())).thenAnswer(inv -> {
            ApiKey k = inv.getArgument(0);
            return ApiKey.builder()
                    .id(UUID.randomUUID())
                    .keyHash(k.getKeyHash())
                    .prefix(k.getPrefix())
                    .tenantId(k.getTenantId())
                    .userId(k.getUserId())
                    .name(k.getName())
                    .scopes(k.getScopes())
                    .status(ApiKeyStatus.ACTIVE)
                    .createdAt(Instant.now())
                    .build();
        });

        ApiKeyCreatedResponse response = apiKeyService.generateApiKey(tenantId, userId, request);
        ApiKey savedKey = captor.getValue();

        // Raw key must NOT be stored in keyHash
        assertThat(savedKey.getKeyHash()).isNotEqualTo(response.getRawKey());
        // Hash must be exactly 64 hex chars (SHA-256)
        assertThat(savedKey.getKeyHash()).hasSize(64).matches("[0-9a-f]+");
        // Verify consistency: sha256(rawKey) == keyHash
        assertThat(apiKeyService.sha256Hex(response.getRawKey())).isEqualTo(savedKey.getKeyHash());
    }

    @Test
    @DisplayName("generateApiKey two calls produce different keys")
    void generateApiKey_twoCallsProduceDifferentKeys() {
        CreateApiKeyRequest request = new CreateApiKeyRequest();
        request.setName("unique-key");
        request.setScopes(List.of());

        when(apiKeyRepository.save(any())).thenAnswer(inv -> {
            ApiKey k = inv.getArgument(0);
            return ApiKey.builder()
                    .id(UUID.randomUUID())
                    .keyHash(k.getKeyHash())
                    .prefix(k.getPrefix())
                    .tenantId(k.getTenantId())
                    .userId(k.getUserId())
                    .name(k.getName())
                    .scopes(k.getScopes())
                    .status(ApiKeyStatus.ACTIVE)
                    .createdAt(Instant.now())
                    .build();
        });

        ApiKeyCreatedResponse r1 = apiKeyService.generateApiKey(tenantId, userId, request);
        ApiKeyCreatedResponse r2 = apiKeyService.generateApiKey(tenantId, userId, request);

        assertThat(r1.getRawKey()).isNotEqualTo(r2.getRawKey());
    }

    @Test
    @DisplayName("revokeApiKey sets status to REVOKED")
    void revokeApiKey_existingKey_setsStatusRevoked() {
        UUID keyId = UUID.randomUUID();
        ApiKey activeKey = ApiKey.builder()
                .id(keyId)
                .keyHash("abc123")
                .prefix("pfx123")
                .tenantId(tenantId)
                .userId(userId)
                .name("service-key")
                .status(ApiKeyStatus.ACTIVE)
                .build();

        when(apiKeyRepository.findById(keyId)).thenReturn(Optional.of(activeKey));
        when(apiKeyRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        apiKeyService.revokeApiKey(tenantId, keyId);

        ArgumentCaptor<ApiKey> captor = ArgumentCaptor.forClass(ApiKey.class);
        verify(apiKeyRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(ApiKeyStatus.REVOKED);
    }

    @Test
    @DisplayName("revokeApiKey throws NoSuchElementException for key in different tenant")
    void revokeApiKey_wrongTenant_throwsNotFound() {
        UUID keyId = UUID.randomUUID();
        UUID differentTenant = UUID.randomUUID();
        ApiKey key = ApiKey.builder()
                .id(keyId)
                .tenantId(differentTenant)
                .status(ApiKeyStatus.ACTIVE)
                .build();

        when(apiKeyRepository.findById(keyId)).thenReturn(Optional.of(key));

        assertThatThrownBy(() -> apiKeyService.revokeApiKey(tenantId, keyId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("sha256Hex produces consistent 64-char lowercase hex output")
    void sha256Hex_isConsistentAndCorrectLength() {
        String input = "jmk_test_key_value";
        String hash1 = apiKeyService.sha256Hex(input);
        String hash2 = apiKeyService.sha256Hex(input);

        assertThat(hash1).isEqualTo(hash2);
        assertThat(hash1).hasSize(64);
        assertThat(hash1).matches("[0-9a-f]+");
    }
}

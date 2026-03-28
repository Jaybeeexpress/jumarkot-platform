package com.jumarkot.tenant.application;

import com.jumarkot.tenant.domain.settings.TenantSettings;
import com.jumarkot.tenant.domain.settings.TenantSettingsRepository;
import com.jumarkot.tenant.web.dto.TenantSettingsDto;
import com.jumarkot.tenant.web.dto.UpdateTenantSettingsRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantSettingsService {

    private final TenantSettingsRepository settingsRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public TenantSettingsDto getSettings(UUID tenantId) {
        TenantSettings settings = settingsRepository.findByTenantId(tenantId)
                .orElseGet(() -> createDefaultSettings(tenantId));
        return toDto(settings);
    }

    @Transactional
    public TenantSettingsDto updateSettings(UUID tenantId, UpdateTenantSettingsRequest request) {
        TenantSettings settings = settingsRepository.findByTenantId(tenantId)
                .orElseGet(() -> createDefaultSettings(tenantId));
        if (request.getTimezone() != null) {
            settings.setTimezone(request.getTimezone());
        }
        if (request.getDefaultCurrency() != null) {
            settings.setDefaultCurrency(request.getDefaultCurrency());
        }
        if (request.getRiskThreshold() != null) {
            settings.setRiskThreshold(request.getRiskThreshold());
        }
        settings = settingsRepository.save(settings);
        log.info("Updated settings for tenant id={}", tenantId);
        return toDto(settings);
    }

    @Transactional
    public String rotateWebhookSecret(UUID tenantId) {
        TenantSettings settings = settingsRepository.findByTenantId(tenantId)
                .orElseGet(() -> createDefaultSettings(tenantId));
        String newSecret = generateWebhookSecret();
        settings.setWebhookSigningSecret(newSecret);
        settingsRepository.save(settings);
        log.info("Rotated webhook signing secret for tenant id={}", tenantId);
        return newSecret;
    }

    private TenantSettings createDefaultSettings(UUID tenantId) {
        TenantSettings settings = TenantSettings.builder()
                .tenantId(tenantId)
                .timezone("UTC")
                .defaultCurrency("USD")
                .riskThreshold(new BigDecimal("0.7000"))
                .webhookSigningSecret(generateWebhookSecret())
                .build();
        return settingsRepository.save(settings);
    }

    private String generateWebhookSecret() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    private TenantSettingsDto toDto(TenantSettings settings) {
        return TenantSettingsDto.builder()
                .tenantId(settings.getTenantId().toString())
                .timezone(settings.getTimezone())
                .defaultCurrency(settings.getDefaultCurrency())
                .riskThreshold(settings.getRiskThreshold())
                .updatedAt(settings.getUpdatedAt())
                .build();
    }
}

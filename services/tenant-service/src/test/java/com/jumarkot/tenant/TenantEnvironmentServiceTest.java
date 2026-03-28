package com.jumarkot.tenant;

import com.jumarkot.contracts.tenant.EnvironmentDto;
import com.jumarkot.contracts.tenant.EnvironmentType;
import com.jumarkot.tenant.application.TenantEnvironmentService;
import com.jumarkot.tenant.domain.environment.EnvironmentStatus;
import com.jumarkot.tenant.domain.environment.TenantEnvironment;
import com.jumarkot.tenant.domain.environment.TenantEnvironmentRepository;
import com.jumarkot.tenant.domain.tenant.Tenant;
import com.jumarkot.tenant.domain.tenant.TenantPlan;
import com.jumarkot.tenant.domain.tenant.TenantRepository;
import com.jumarkot.tenant.domain.tenant.TenantStatus;
import com.jumarkot.tenant.web.dto.CreateEnvironmentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TenantEnvironmentService Tests")
class TenantEnvironmentServiceTest {

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private TenantEnvironmentRepository environmentRepository;

    private TenantEnvironmentService environmentService;

    private static final UUID TENANT_ID = UUID.randomUUID();
    private static final UUID ENV_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        environmentService = new TenantEnvironmentService(tenantRepository, environmentRepository);
    }

    private Tenant buildTenant() {
        return Tenant.builder()
                .id(TENANT_ID)
                .name("Test Corp")
                .slug("test-corp")
                .plan(TenantPlan.STARTER)
                .region("us-east-1")
                .ownerId(UUID.randomUUID())
                .status(TenantStatus.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("createEnvironment succeeds for valid tenant and request")
    void createEnvironment_valid_returnsEnvironmentDto() {
        CreateEnvironmentRequest request = CreateEnvironmentRequest.builder()
                .name("Production")
                .type(com.jumarkot.tenant.domain.environment.EnvironmentType.PRODUCTION)
                .apiBaseUrl("https://api.acme.com")
                .webhookUrl("https://hooks.acme.com/events")
                .build();

        TenantEnvironment saved = TenantEnvironment.builder()
                .id(ENV_ID)
                .tenantId(TENANT_ID)
                .name("Production")
                .type(com.jumarkot.tenant.domain.environment.EnvironmentType.PRODUCTION)
                .apiBaseUrl("https://api.acme.com")
                .webhookUrl("https://hooks.acme.com/events")
                .status(EnvironmentStatus.ACTIVE)
                .createdAt(Instant.now())
                .build();

        when(tenantRepository.findById(TENANT_ID)).thenReturn(Optional.of(buildTenant()));
        when(environmentRepository.save(any(TenantEnvironment.class))).thenReturn(saved);

        EnvironmentDto result = environmentService.createEnvironment(TENANT_ID, request);

        assertThat(result).isNotNull();
        assertThat(result.getEnvironmentId()).isEqualTo(ENV_ID.toString());
        assertThat(result.getName()).isEqualTo("Production");
        assertThat(result.getType()).isEqualTo(EnvironmentType.PRODUCTION);
        assertThat(result.isActive()).isTrue();
        verify(tenantRepository).findById(TENANT_ID);
        verify(environmentRepository).save(any(TenantEnvironment.class));
    }

    @Test
    @DisplayName("createEnvironment throws NoSuchElementException when tenant not found")
    void createEnvironment_tenantNotFound_throwsNoSuchElement() {
        CreateEnvironmentRequest request = CreateEnvironmentRequest.builder()
                .name("Sandbox")
                .type(com.jumarkot.tenant.domain.environment.EnvironmentType.SANDBOX)
                .build();

        UUID unknownTenant = UUID.randomUUID();
        when(tenantRepository.findById(unknownTenant)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> environmentService.createEnvironment(unknownTenant, request))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining(unknownTenant.toString());

        verify(environmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("listEnvironments returns only environments for the given tenant")
    void listEnvironments_tenantScoped_returnsOnlyTenantEnvironments() {
        List<TenantEnvironment> envs = List.of(
                TenantEnvironment.builder()
                        .id(ENV_ID)
                        .tenantId(TENANT_ID)
                        .name("Prod")
                        .type(com.jumarkot.tenant.domain.environment.EnvironmentType.PRODUCTION)
                        .status(EnvironmentStatus.ACTIVE)
                        .createdAt(Instant.now())
                        .build(),
                TenantEnvironment.builder()
                        .id(UUID.randomUUID())
                        .tenantId(TENANT_ID)
                        .name("Sandbox")
                        .type(com.jumarkot.tenant.domain.environment.EnvironmentType.SANDBOX)
                        .status(EnvironmentStatus.ACTIVE)
                        .createdAt(Instant.now())
                        .build()
        );

        when(environmentRepository.findByTenantId(TENANT_ID)).thenReturn(envs);

        List<EnvironmentDto> result = environmentService.listEnvironments(TENANT_ID);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(e -> e.isActive());
        verify(environmentRepository).findByTenantId(TENANT_ID);
    }

    @Test
    @DisplayName("getEnvironment returns EnvironmentDto for valid tenant and environment ID")
    void getEnvironment_validTenantAndEnvId_returnsDto() {
        TenantEnvironment env = TenantEnvironment.builder()
                .id(ENV_ID)
                .tenantId(TENANT_ID)
                .name("Staging")
                .type(com.jumarkot.tenant.domain.environment.EnvironmentType.STAGING)
                .status(EnvironmentStatus.ACTIVE)
                .createdAt(Instant.now())
                .build();

        when(environmentRepository.findByIdAndTenantId(ENV_ID, TENANT_ID)).thenReturn(Optional.of(env));

        EnvironmentDto result = environmentService.getEnvironment(TENANT_ID, ENV_ID);

        assertThat(result.getEnvironmentId()).isEqualTo(ENV_ID.toString());
        assertThat(result.getName()).isEqualTo("Staging");
        assertThat(result.getType()).isEqualTo(EnvironmentType.SANDBOX); // STAGING maps to SANDBOX
    }

    @Test
    @DisplayName("getEnvironment throws NoSuchElementException when environment belongs to different tenant")
    void getEnvironment_wrongTenantId_throwsNoSuchElement() {
        UUID wrongTenant = UUID.randomUUID();
        when(environmentRepository.findByIdAndTenantId(ENV_ID, wrongTenant)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> environmentService.getEnvironment(wrongTenant, ENV_ID))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining(ENV_ID.toString());
    }
}

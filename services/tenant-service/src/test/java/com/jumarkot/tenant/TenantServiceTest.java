package com.jumarkot.tenant;

import com.jumarkot.contracts.common.PageResponse;
import com.jumarkot.contracts.tenant.TenantDto;
import com.jumarkot.tenant.application.TenantService;
import com.jumarkot.tenant.domain.tenant.Tenant;
import com.jumarkot.tenant.domain.tenant.TenantPlan;
import com.jumarkot.tenant.domain.tenant.TenantRepository;
import com.jumarkot.tenant.domain.tenant.TenantStatus;
import com.jumarkot.tenant.web.dto.CreateTenantRequest;
import com.jumarkot.tenant.web.dto.UpdateTenantRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TenantService Tests")
class TenantServiceTest {

    @Mock
    private TenantRepository tenantRepository;

    private TenantService tenantService;

    @BeforeEach
    void setUp() {
        tenantService = new TenantService(tenantRepository);
    }

    @Test
    @DisplayName("createTenant succeeds for valid request")
    void createTenant_validRequest_returnsTenantDto() {
        CreateTenantRequest request = CreateTenantRequest.builder()
                .name("Acme Corp")
                .slug("acme-corp")
                .plan(TenantPlan.STARTER)
                .region("us-east-1")
                .ownerEmail("admin@acme.com")
                .build();

        Tenant saved = Tenant.builder()
                .id(UUID.randomUUID())
                .name("Acme Corp")
                .slug("acme-corp")
                .plan(TenantPlan.STARTER)
                .region("us-east-1")
                .ownerId(UUID.randomUUID())
                .status(TenantStatus.PENDING_SETUP)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(tenantRepository.existsBySlug("acme-corp")).thenReturn(false);
        when(tenantRepository.save(any(Tenant.class))).thenReturn(saved);

        TenantDto result = tenantService.createTenant(request);

        assertThat(result).isNotNull();
        assertThat(result.getSlug()).isEqualTo("acme-corp");
        assertThat(result.getName()).isEqualTo("Acme Corp");
        assertThat(result.getStatus()).isEqualTo(TenantDto.TenantStatus.PENDING_VERIFICATION);
        verify(tenantRepository).existsBySlug("acme-corp");
        verify(tenantRepository).save(any(Tenant.class));
    }

    @Test
    @DisplayName("createTenant throws IllegalArgumentException when slug is already taken")
    void createTenant_duplicateSlug_throwsIllegalArgument() {
        CreateTenantRequest request = CreateTenantRequest.builder()
                .name("Duplicate Corp")
                .slug("taken-slug")
                .plan(TenantPlan.FREE)
                .region("eu-west-1")
                .ownerEmail("owner@dup.com")
                .build();

        when(tenantRepository.existsBySlug("taken-slug")).thenReturn(true);

        assertThatThrownBy(() -> tenantService.createTenant(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("taken-slug");

        verify(tenantRepository, never()).save(any());
    }

    @Test
    @DisplayName("getTenant returns TenantDto for existing tenant")
    void getTenant_existingId_returnsTenantDto() {
        UUID id = UUID.randomUUID();
        Tenant tenant = Tenant.builder()
                .id(id)
                .name("Existing Corp")
                .slug("existing-corp")
                .plan(TenantPlan.GROWTH)
                .region("ap-southeast-1")
                .ownerId(UUID.randomUUID())
                .status(TenantStatus.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(tenantRepository.findById(id)).thenReturn(Optional.of(tenant));

        TenantDto result = tenantService.getTenant(id);

        assertThat(result.getTenantId()).isEqualTo(id.toString());
        assertThat(result.getStatus()).isEqualTo(TenantDto.TenantStatus.ACTIVE);
    }

    @Test
    @DisplayName("getTenant throws NoSuchElementException for missing tenant")
    void getTenant_missingId_throwsNoSuchElement() {
        UUID id = UUID.randomUUID();
        when(tenantRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tenantService.getTenant(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    @DisplayName("suspendTenant sets status to SUSPENDED")
    void suspendTenant_activeTenant_setsStatusSuspended() {
        UUID id = UUID.randomUUID();
        Tenant tenant = Tenant.builder()
                .id(id)
                .name("Active Corp")
                .slug("active-corp")
                .plan(TenantPlan.ENTERPRISE)
                .region("us-west-2")
                .ownerId(UUID.randomUUID())
                .status(TenantStatus.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(tenantRepository.findById(id)).thenReturn(Optional.of(tenant));
        when(tenantRepository.save(any(Tenant.class))).thenAnswer(inv -> inv.getArgument(0));

        tenantService.suspendTenant(id);

        assertThat(tenant.getStatus()).isEqualTo(TenantStatus.SUSPENDED);
        verify(tenantRepository).save(tenant);
    }

    @Test
    @DisplayName("listTenants returns PageResponse with mapped TenantDtos")
    void listTenants_pageable_returnsPageResponse() {
        Pageable pageable = PageRequest.of(0, 10);
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        List<Tenant> tenants = List.of(
                Tenant.builder().id(id1).name("Corp A").slug("corp-a").plan(TenantPlan.FREE)
                        .region("us-east-1").ownerId(UUID.randomUUID())
                        .status(TenantStatus.ACTIVE).createdAt(Instant.now()).updatedAt(Instant.now()).build(),
                Tenant.builder().id(id2).name("Corp B").slug("corp-b").plan(TenantPlan.STARTER)
                        .region("eu-west-1").ownerId(UUID.randomUUID())
                        .status(TenantStatus.SUSPENDED).createdAt(Instant.now()).updatedAt(Instant.now()).build()
        );
        Page<Tenant> page = new PageImpl<>(tenants, pageable, 2);
        when(tenantRepository.findAll(pageable)).thenReturn(page);

        PageResponse<TenantDto> result = tenantService.listTenants(pageable);

        assertThat(result.getData()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2L);
        assertThat(result.isHasNext()).isFalse();
        assertThat(result.getData().get(0).getSlug()).isEqualTo("corp-a");
        assertThat(result.getData().get(1).getStatus()).isEqualTo(TenantDto.TenantStatus.SUSPENDED);
    }
}

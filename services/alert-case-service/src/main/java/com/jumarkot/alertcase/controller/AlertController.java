package com.jumarkot.alertcase.controller;

import com.jumarkot.contracts.common.ApiResponse;
import com.jumarkot.alertcase.domain.Alert;
import com.jumarkot.alertcase.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/tenants/{tenantId}/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @PostMapping
    public ResponseEntity<ApiResponse<Alert>> create(
            @PathVariable UUID tenantId,
            @RequestBody Alert alert) {
        alert.setTenantId(tenantId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(alertService.create(alert)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Alert>>> list(
            @PathVariable UUID tenantId,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.ok(alertService.list(tenantId, status)));
    }

    @PostMapping("/{alertId}/resolve")
    public ResponseEntity<ApiResponse<Alert>> resolve(
            @PathVariable UUID tenantId,
            @PathVariable UUID alertId) {
        return ResponseEntity.ok(ApiResponse.ok(alertService.resolve(tenantId, alertId)));
    }
}

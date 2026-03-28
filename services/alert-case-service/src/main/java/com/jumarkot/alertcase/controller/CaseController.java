package com.jumarkot.alertcase.controller;

import com.jumarkot.contracts.common.ApiResponse;
import com.jumarkot.alertcase.domain.Case;
import com.jumarkot.alertcase.service.CaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/tenants/{tenantId}/cases")
@RequiredArgsConstructor
public class CaseController {

    private final CaseService caseService;

    @PostMapping
    public ResponseEntity<ApiResponse<Case>> create(
            @PathVariable UUID tenantId,
            @RequestBody Case kase) {
        kase.setTenantId(tenantId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(caseService.create(kase)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Case>>> list(
            @PathVariable UUID tenantId,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.ok(caseService.list(tenantId, status)));
    }

    @PostMapping("/{caseId}/close")
    public ResponseEntity<ApiResponse<Case>> close(
            @PathVariable UUID tenantId,
            @PathVariable UUID caseId) {
        return ResponseEntity.ok(ApiResponse.ok(caseService.close(tenantId, caseId)));
    }
}

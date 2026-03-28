package com.jumarkot.decision.controller;

import com.jumarkot.contracts.common.ApiResponse;
import com.jumarkot.contracts.decision.DecisionRequest;
import com.jumarkot.contracts.decision.DecisionResponse;
import com.jumarkot.decision.service.DecisionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/decisions")
@RequiredArgsConstructor
public class DecisionController {

    private final DecisionService decisionService;

    @PostMapping
    public ResponseEntity<ApiResponse<DecisionResponse>> decide(@Valid @RequestBody DecisionRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(decisionService.decide(request)));
    }
}

package com.jumarkot.decision.api;

import com.jumarkot.contracts.common.ApiResponse;
import com.jumarkot.contracts.decision.DecisionRequest;
import com.jumarkot.contracts.decision.DecisionResponse;
import com.jumarkot.decision.service.DecisionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/decisions")
public class DecisionController {

    private final DecisionService decisionService;

    public DecisionController(DecisionService decisionService) {
        this.decisionService = decisionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DecisionResponse>> evaluate(@Valid @RequestBody DecisionRequest request) {
        DecisionResponse response = decisionService.decide(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}

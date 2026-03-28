package com.jumarkot.rules.api;

import com.jumarkot.contracts.common.ApiResponse;
import com.jumarkot.rules.domain.ConditionLogic;
import com.jumarkot.rules.domain.RuleOperator;
import com.jumarkot.rules.dto.RuleDto;
import com.jumarkot.rules.repository.RuleRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/rules")
public class RuleController {

    private final RuleRepository ruleRepository;

    public RuleController(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UUID> create(@Valid @RequestBody CreateRuleRequest req) {
        RuleDto dto = new RuleDto(
                null,
                req.tenantId(),
                req.environmentType(),
                req.name(),
                req.description(),
                req.category(),
                req.priority(),
                "ACTIVE",
                req.conditions(),
                req.conditionLogic(),
                req.action(),
                req.scoreAdjustment(),
                req.reasonCode(),
                req.effectiveFrom(),
                req.effectiveTo(),
                1
        );
        return ApiResponse.ok(ruleRepository.insert(dto));
    }

    @GetMapping
    public ApiResponse<List<RuleDto>> list(
            @RequestParam UUID tenantId,
            @RequestParam String environmentType) {
        return ApiResponse.ok(
                ruleRepository.findActiveByTenantAndEnvironment(tenantId, environmentType));
    }

    @PutMapping("/{ruleId}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateStatus(@PathVariable UUID ruleId,
                              @RequestParam UUID tenantId,
                              @RequestParam @Pattern(regexp = "ACTIVE|INACTIVE|DRAFT") String status) {
        ruleRepository.updateStatus(ruleId, tenantId, status);
    }

    // ─── Request DSL ─────────────────────────────────────────────────────────

    public record CreateRuleRequest(
            @NotNull UUID tenantId,
            @NotBlank String environmentType,
            @NotBlank @Size(max = 255) String name,
            String description,
            @NotBlank String category,
            @Min(0) @Max(9999) int priority,
            @NotEmpty List<RuleDto.RuleConditionDto> conditions,
            @NotNull ConditionLogic conditionLogic,
            @NotBlank String action,
            @Min(0) @Max(100) int scoreAdjustment,
            @NotBlank String reasonCode,
            OffsetDateTime effectiveFrom,
            OffsetDateTime effectiveTo
    ) {}
}

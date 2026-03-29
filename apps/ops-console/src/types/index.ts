// ── API envelope ──────────────────────────────────────────────────────────────

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  requestId?: string;
  timestamp: string;
}

export interface ApiErrorResponse {
  success: false;
  errorCode: string;
  message: string;
  fieldErrors?: Array<{ field: string; message: string }>;
  requestId?: string;
  timestamp: string;
}

// ── Decisions ─────────────────────────────────────────────────────────────────

export type RiskDecision = 'APPROVE' | 'REVIEW' | 'DECLINE' | 'BLOCK';
export type RiskLevel    = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export interface DecisionRequest {
  idempotencyKey: string;
  entityId:       string;
  entityType:     string;
  eventType:      string;
  payload?:       Record<string, unknown>;
  ipAddress?:     string;
  deviceId?:      string;
  sessionId?:     string;
  userAgent?:     string;
}

export interface MatchedRule {
  ruleId:           string;
  ruleName:         string;
  priority:         number;
  reasonCode:       string;
  scoreContribution: number;
}

export interface TriggeredSignal {
  signalType:    string;
  description:   string;
  severity:      number;
  observedValue: unknown;
}

export interface DecisionResponse {
  decisionId:        string;
  decision:          RiskDecision;
  riskScore:         number;
  riskLevel:         RiskLevel;
  matchedRules:      MatchedRule[];
  triggeredSignals:  TriggeredSignal[];
  recommendedAction: string;
  correlationId:     string;
  timestamp:         string;
}

// ── Rules ─────────────────────────────────────────────────────────────────────

export type ConditionLogic = 'ALL' | 'ANY';

export interface RuleConditionDto {
  field:    string;
  operator: string;
  value:    string;
}

export interface RuleDto {
  id:              string;
  tenantId:        string;
  environmentType: string;
  name:            string;
  description?:    string;
  category:        string;
  priority:        number;
  status:          string;
  conditions:      RuleConditionDto[];
  conditionLogic:  ConditionLogic;
  action:          string;
  scoreAdjustment: number;
  reasonCode:      string;
  effectiveFrom?:  string;
  effectiveTo?:    string;
  version:         number;
}

export interface CreateRulePayload {
  tenantId:        string;
  environmentType: string;
  name:            string;
  description?:    string;
  category:        string;
  priority:        number;
  conditions:      RuleConditionDto[];
  conditionLogic:  ConditionLogic;
  action:          string;
  scoreAdjustment: number;
  reasonCode:      string;
  effectiveFrom?:  string;
  effectiveTo?:    string;
}

// ── Tenants ───────────────────────────────────────────────────────────────────

export interface Tenant {
  id:           string;
  slug:         string;
  name:         string;
  status:       string;
  plan:         string;
  contactEmail: string;
  createdAt:    string;
  updatedAt?:   string;
}

export interface TenantEnvironment {
  id:        string;
  tenantId:  string;
  name:      string;
  type:      string;   // PRODUCTION | SANDBOX
  status:    string;
  createdAt: string;
}

export interface ProvisionTenantPayload {
  slug:         string;
  name:         string;
  plan:         string;
  contactEmail: string;
}

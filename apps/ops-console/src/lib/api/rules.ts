import apiClient from '../apiClient';
import type { ApiResponse, CreateRulePayload, RuleDto } from '@/types';

export async function listRules(
  tenantId: string,
  environmentType: string,
): Promise<ApiResponse<RuleDto[]>> {
  const { data } = await apiClient.get<ApiResponse<RuleDto[]>>('/api/rules', {
    params: { tenantId, environmentType },
  });
  return data;
}

export async function createRule(
  payload: CreateRulePayload,
): Promise<ApiResponse<string>> {
  const { data } = await apiClient.post<ApiResponse<string>>('/api/rules', payload);
  return data;
}

export async function updateRuleStatus(
  ruleId: string,
  tenantId: string,
  status: 'ACTIVE' | 'INACTIVE' | 'DRAFT',
): Promise<void> {
  await apiClient.put(`/api/rules/${ruleId}/status`, null, {
    params: { tenantId, status },
  });
}

import { describe, expect, it, vi } from 'vitest';

vi.mock('../apiClient', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
  },
}));

import apiClient from '../apiClient';
import { createRule, listRules, updateRuleStatus } from './rules';

describe('rules API', () => {
  it('lists rules with tenant and environment query params', async () => {
    const responseData = {
      success: true,
      data: [],
      timestamp: '2026-01-01T00:00:00.000Z',
    };
    vi.mocked(apiClient.get).mockResolvedValue({ data: responseData });

    const result = await listRules('tenant-1', 'PRODUCTION');

    expect(apiClient.get).toHaveBeenCalledWith('/api/rules', {
      params: { tenantId: 'tenant-1', environmentType: 'PRODUCTION' },
    });
    expect(result).toEqual(responseData);
  });

  it('creates a rule and returns response payload', async () => {
    const payload = {
      tenantId: 'tenant-1',
      environmentType: 'PRODUCTION',
      name: 'Test Rule',
      category: 'FRAUD',
      priority: 10,
      conditions: [],
      conditionLogic: 'ALL' as const,
      action: 'REVIEW',
      scoreAdjustment: 15,
      reasonCode: 'SUSPICIOUS_ACTIVITY',
    };
    const responseData = {
      success: true,
      data: 'rule-1',
      timestamp: '2026-01-01T00:00:00.000Z',
    };
    vi.mocked(apiClient.post).mockResolvedValue({ data: responseData });

    const result = await createRule(payload);

    expect(apiClient.post).toHaveBeenCalledWith('/api/rules', payload);
    expect(result).toEqual(responseData);
  });

  it('updates rule status with expected route and query params', async () => {
    vi.mocked(apiClient.put).mockResolvedValue({});

    await updateRuleStatus('rule-1', 'tenant-1', 'ACTIVE');

    expect(apiClient.put).toHaveBeenCalledWith('/api/rules/rule-1/status', null, {
      params: { tenantId: 'tenant-1', status: 'ACTIVE' },
    });
  });
});

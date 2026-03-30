import { describe, expect, it, vi } from 'vitest';

vi.mock('../apiClient', () => ({
  default: {
    post: vi.fn(),
  },
}));

import apiClient from '../apiClient';
import { submitDecision } from './decisions';

describe('submitDecision', () => {
  it('posts decision payload and returns response data', async () => {
    const responseData = {
      success: true,
      data: {
        decisionId: 'd-1',
        decision: 'APPROVE',
        riskScore: 10,
        riskLevel: 'LOW',
        matchedRules: [],
        triggeredSignals: [],
        recommendedAction: 'ALLOW',
        correlationId: 'corr-1',
        timestamp: '2026-01-01T00:00:00.000Z',
      },
      timestamp: '2026-01-01T00:00:00.000Z',
    };

    vi.mocked(apiClient.post).mockResolvedValue({ data: responseData });

    const payload = {
      idempotencyKey: 'idem-1',
      entityId: 'user-1',
      entityType: 'USER',
      eventType: 'LOGIN',
    };

    const result = await submitDecision(payload);

    expect(apiClient.post).toHaveBeenCalledWith('/api/decisions', payload);
    expect(result).toEqual(responseData);
  });
});

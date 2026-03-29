import apiClient from '../apiClient';
import type { ApiResponse, DecisionRequest, DecisionResponse } from '@/types';

export async function submitDecision(
  req: DecisionRequest,
): Promise<ApiResponse<DecisionResponse>> {
  const { data } = await apiClient.post<ApiResponse<DecisionResponse>>(
    '/api/decisions',
    req,
  );
  return data;
}

import type { ApiResponse } from '@/types';

export function isSuccessfulResponse<T>(
  response: ApiResponse<T> | null | undefined,
): response is ApiResponse<T> & { success: true } {
  return Boolean(response?.success);
}

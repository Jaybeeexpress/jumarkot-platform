import axios from 'axios';
import type { ApiErrorResponse } from '@/types';

function isApiErrorResponse(value: unknown): value is ApiErrorResponse {
  if (!value || typeof value !== 'object') {
    return false;
  }
  const candidate = value as Partial<ApiErrorResponse>;
  return (
    candidate.success === false &&
    typeof candidate.errorCode === 'string' &&
    typeof candidate.message === 'string'
  );
}

function resolveMessageByCode(errorCode: string): string | null {
  switch (errorCode) {
    case 'MISSING_SERVICE_CONFIG':
      return 'Required service configuration is missing. Verify backend URL and credentials in .env.local.';
    case 'NO_API_KEY':
      return 'Decision API key is missing in environment config. Set DECISION_API_KEY and retry.';
    case 'INVALID_JSON_PAYLOAD':
      return 'Payload must be valid JSON before submitting a decision.';
    case 'API_KEY_REQUIRED':
      return 'Decision API key is required. Configure DECISION_API_KEY before running evaluations.';
    case 'INVALID_API_KEY':
      return 'Decision API key is invalid or revoked. Generate a new key in identity-access-service.';
    case 'BASIC_AUTH_REQUIRED':
      return 'Service authentication is required. Verify service user/password settings in .env.local.';
    case 'INVALID_BASIC_CREDENTIALS':
      return 'Configured service credentials were rejected. Check *_SERVICE_USER and *_SERVICE_PASSWORD values.';
    case 'UPSTREAM_ERROR':
      return 'Could not reach upstream backend service. Confirm the service is running and reachable.';
    default:
      return null;
  }
}

function resolveMessageByStatus(status: number): string {
  if (status === 401) {
    return 'Authentication failed (401). Check API key or service credentials.';
  }
  if (status === 403) {
    return 'Access denied (403). Verify tenant access and API/service credentials.';
  }
  if (status === 404) {
    return 'Requested resource was not found (404). Confirm identifiers and backend route availability.';
  }
  if (status >= 500) {
    return 'Backend service returned a server error. Check backend logs and retry.';
  }
  return 'Request failed. Review configuration and try again.';
}

export function toUserFacingApiError(error: unknown, fallback: string): string {
  if (axios.isAxiosError(error)) {
    if (!error.response) {
      return 'Network error: could not reach the API route. Verify dev server/backend availability.';
    }

    const data = error.response.data;
    if (isApiErrorResponse(data)) {
      return resolveMessageByCode(data.errorCode) ?? data.message;
    }

    return resolveMessageByStatus(error.response.status);
  }

  if (error instanceof Error && error.message.trim()) {
    return resolveMessageByCode(error.message) ?? error.message;
  }

  return fallback;
}

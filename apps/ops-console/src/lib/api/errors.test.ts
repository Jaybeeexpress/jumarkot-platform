import axios from 'axios';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { toUserFacingApiError } from './errors';

describe('toUserFacingApiError', () => {
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('returns network message when axios error has no response', () => {
    const error = { response: undefined };
    vi.spyOn(axios, 'isAxiosError').mockReturnValue(true);

    const result = toUserFacingApiError(error, 'fallback');

    expect(result).toBe(
      'Network error: could not reach the API route. Verify dev server/backend availability.',
    );
  });

  it('maps known API error codes to user-facing guidance', () => {
    const error = {
      response: {
        status: 400,
        data: {
          success: false,
          errorCode: 'INVALID_BASIC_CREDENTIALS',
          message: 'raw backend message',
        },
      },
    };
    vi.spyOn(axios, 'isAxiosError').mockReturnValue(true);

    const result = toUserFacingApiError(error, 'fallback');

    expect(result).toBe(
      'Configured service credentials were rejected. Check *_SERVICE_USER and *_SERVICE_PASSWORD values.',
    );
  });

  it('falls back to backend message when API code is unknown', () => {
    const error = {
      response: {
        status: 400,
        data: {
          success: false,
          errorCode: 'UNKNOWN_CODE',
          message: 'backend detail',
        },
      },
    };
    vi.spyOn(axios, 'isAxiosError').mockReturnValue(true);

    const result = toUserFacingApiError(error, 'fallback');

    expect(result).toBe('backend detail');
  });

  it('maps status-based axios responses when payload is not API error shape', () => {
    const error = {
      response: {
        status: 503,
        data: { detail: 'temporarily unavailable' },
      },
    };
    vi.spyOn(axios, 'isAxiosError').mockReturnValue(true);

    const result = toUserFacingApiError(error, 'fallback');

    expect(result).toBe('Backend service returned a server error. Check backend logs and retry.');
  });

  it('maps 401 axios responses to auth guidance', () => {
    const error = {
      response: {
        status: 401,
        data: { detail: 'unauthorized' },
      },
    };
    vi.spyOn(axios, 'isAxiosError').mockReturnValue(true);

    const result = toUserFacingApiError(error, 'fallback');

    expect(result).toBe('Authentication failed (401). Check API key or service credentials.');
  });

  it('maps 404 axios responses to not-found guidance', () => {
    const error = {
      response: {
        status: 404,
        data: { detail: 'missing' },
      },
    };
    vi.spyOn(axios, 'isAxiosError').mockReturnValue(true);

    const result = toUserFacingApiError(error, 'fallback');

    expect(result).toBe(
      'Requested resource was not found (404). Confirm identifiers and backend route availability.',
    );
  });

  it('maps unknown status axios responses to generic failure guidance', () => {
    const error = {
      response: {
        status: 418,
        data: { detail: 'teapot' },
      },
    };
    vi.spyOn(axios, 'isAxiosError').mockReturnValue(true);

    const result = toUserFacingApiError(error, 'fallback');

    expect(result).toBe('Request failed. Review configuration and try again.');
  });

  it('resolves plain Error code strings via known code mapping', () => {
    vi.spyOn(axios, 'isAxiosError').mockReturnValue(false);

    const result = toUserFacingApiError(new Error('NO_API_KEY'), 'fallback');

    expect(result).toBe(
      'Decision API key is missing in environment config. Set DECISION_API_KEY and retry.',
    );
  });

  it('returns fallback for unknown non-error values', () => {
    vi.spyOn(axios, 'isAxiosError').mockReturnValue(false);

    const result = toUserFacingApiError({ any: 'value' }, 'fallback message');

    expect(result).toBe('fallback message');
  });

  it('returns plain Error message when code mapping is unknown', () => {
    vi.spyOn(axios, 'isAxiosError').mockReturnValue(false);

    const result = toUserFacingApiError(new Error('custom issue'), 'fallback');

    expect(result).toBe('custom issue');
  });

  it('returns fallback when Error message is blank', () => {
    vi.spyOn(axios, 'isAxiosError').mockReturnValue(false);

    const result = toUserFacingApiError(new Error('   '), 'fallback');

    expect(result).toBe('fallback');
  });
});

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
});

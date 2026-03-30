import { describe, expect, it } from 'vitest';
import { isSuccessfulResponse } from './response';

describe('isSuccessfulResponse', () => {
  it('returns true when success is true', () => {
    const result = isSuccessfulResponse({
      success: true,
      data: { id: 'tenant-1' },
      timestamp: '2026-01-01T00:00:00.000Z',
    });

    expect(result).toBe(true);
  });

  it('returns false for failed responses', () => {
    const result = isSuccessfulResponse({
      success: false,
      data: null,
      timestamp: '2026-01-01T00:00:00.000Z',
    });

    expect(result).toBe(false);
  });

  it('returns false for nullish values', () => {
    expect(isSuccessfulResponse(null)).toBe(false);
    expect(isSuccessfulResponse(undefined)).toBe(false);
  });
});

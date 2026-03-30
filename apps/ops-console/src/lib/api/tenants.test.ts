import { describe, expect, it, vi } from 'vitest';

vi.mock('../apiClient', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
  },
}));

import apiClient from '../apiClient';
import { getTenant, getTenantEnvironments, provisionTenant } from './tenants';

describe('tenants API', () => {
  it('provisions a tenant and returns API response data', async () => {
    const payload = {
      slug: 'acme',
      name: 'Acme Corp',
      plan: 'STARTER',
      contactEmail: 'ops@acme.test',
    };
    const responseData = {
      success: true,
      data: {
        id: 'tenant-1',
        slug: 'acme',
        name: 'Acme Corp',
        status: 'ACTIVE',
        plan: 'STARTER',
        contactEmail: 'ops@acme.test',
        createdAt: '2026-01-01T00:00:00.000Z',
      },
      timestamp: '2026-01-01T00:00:00.000Z',
    };
    vi.mocked(apiClient.post).mockResolvedValue({ data: responseData });

    const result = await provisionTenant(payload);

    expect(apiClient.post).toHaveBeenCalledWith('/api/tenants', payload);
    expect(result).toEqual(responseData);
  });

  it('gets tenant by id and returns response data', async () => {
    const responseData = {
      success: true,
      data: {
        id: 'tenant-1',
        slug: 'acme',
        name: 'Acme Corp',
        status: 'ACTIVE',
        plan: 'STARTER',
        contactEmail: 'ops@acme.test',
        createdAt: '2026-01-01T00:00:00.000Z',
      },
      timestamp: '2026-01-01T00:00:00.000Z',
    };
    vi.mocked(apiClient.get).mockResolvedValue({ data: responseData });

    const result = await getTenant('tenant-1');

    expect(apiClient.get).toHaveBeenCalledWith('/api/tenants/tenant-1');
    expect(result).toEqual(responseData);
  });

  it('gets tenant environments with expected route', async () => {
    const responseData = {
      success: true,
      data: [],
      timestamp: '2026-01-01T00:00:00.000Z',
    };
    vi.mocked(apiClient.get).mockResolvedValue({ data: responseData });

    const result = await getTenantEnvironments('tenant-1');

    expect(apiClient.get).toHaveBeenCalledWith('/api/tenants/tenant-1/environments');
    expect(result).toEqual(responseData);
  });
});

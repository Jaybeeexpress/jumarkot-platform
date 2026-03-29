import apiClient from '../apiClient';
import type { ApiResponse, ProvisionTenantPayload, Tenant, TenantEnvironment } from '@/types';

export async function provisionTenant(
  payload: ProvisionTenantPayload,
): Promise<ApiResponse<Tenant>> {
  const { data } = await apiClient.post<ApiResponse<Tenant>>('/api/tenants', payload);
  return data;
}

export async function getTenant(tenantId: string): Promise<ApiResponse<Tenant>> {
  const { data } = await apiClient.get<ApiResponse<Tenant>>(`/api/tenants/${tenantId}`);
  return data;
}

export async function getTenantEnvironments(
  tenantId: string,
): Promise<ApiResponse<TenantEnvironment[]>> {
  const { data } = await apiClient.get<ApiResponse<TenantEnvironment[]>>(
    `/api/tenants/${tenantId}/environments`,
  );
  return data;
}

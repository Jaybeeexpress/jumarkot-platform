'use client';

import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useMutation, useQuery } from '@tanstack/react-query';
import { Nav } from '@/components/layout/Nav';
import { StatusBadge } from '@/components/ui/StatusBadge';
import { ErrorAlert } from '@/components/ui/ErrorAlert';
import { provisionTenant, getTenant, getTenantEnvironments } from '@/lib/api/tenants';
import type { Tenant } from '@/types';

const schema = z.object({
  slug:         z.string().regex(/^[a-z0-9-]{3,63}$/, '3-63 lowercase alphanum or hyphens'),
  name:         z.string().min(1, 'Required'),
  plan:         z.string().min(1, 'Required'),
  contactEmail: z.string().email('Valid email required'),
});
type FormData = z.infer<typeof schema>;

export default function TenantsPage() {
  const [lookupInput, setLookupInput]         = useState('');
  const [activeLookupId, setActiveLookupId]   = useState('');
  const [provisioned, setProvisioned]         = useState<Tenant | null>(null);

  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: { plan: 'starter' },
  });

  const provision = useMutation({
    mutationFn: provisionTenant,
    onSuccess: (res) => { if (res.success) { setProvisioned(res.data); reset(); } },
  });

  const { data: tenantData, isLoading: tenantLoading, error: tenantError } = useQuery({
    queryKey: ['tenant', activeLookupId],
    queryFn:  () => getTenant(activeLookupId),
    enabled:  !!activeLookupId,
  });

  const { data: envsData } = useQuery({
    queryKey: ['tenant-environments', activeLookupId],
    queryFn:  () => getTenantEnvironments(activeLookupId),
    enabled:  !!activeLookupId,
  });

  const tenant = tenantData?.success ? tenantData.data : null;
  const environments = envsData?.success ? envsData.data : [];

  const inputCls =
    'mt-1 w-full rounded-md border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-slate-500';

  return (
    <div className="flex min-h-screen flex-col bg-slate-50">
      <Nav />
      <main className="flex-1 p-8">
        <h1 className="mb-6 text-2xl font-semibold text-slate-900">Tenants</h1>

        <div className="grid gap-8 lg:grid-cols-2">
          {/* ── Provision form ── */}
          <div className="rounded-lg border bg-white p-6 shadow-sm">
            <h2 className="mb-4 text-base font-medium text-slate-900">Provision New Tenant</h2>
            <form onSubmit={handleSubmit((d) => provision.mutate(d))} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-slate-700">Slug</label>
                <input {...register('slug')} placeholder="acme-corp" className={inputCls} />
                {errors.slug && <p className="mt-1 text-xs text-red-600">{errors.slug.message}</p>}
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700">Name</label>
                <input {...register('name')} placeholder="Acme Corp" className={inputCls} />
                {errors.name && <p className="mt-1 text-xs text-red-600">{errors.name.message}</p>}
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700">Plan</label>
                <select {...register('plan')} className={inputCls}>
                  <option value="starter">Starter</option>
                  <option value="growth">Growth</option>
                  <option value="enterprise">Enterprise</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-slate-700">Contact Email</label>
                <input {...register('contactEmail')} type="email" placeholder="admin@acme.com" className={inputCls} />
                {errors.contactEmail && <p className="mt-1 text-xs text-red-600">{errors.contactEmail.message}</p>}
              </div>

              {provision.error && <ErrorAlert message={(provision.error as Error).message} />}

              <button
                type="submit"
                disabled={provision.isPending}
                className="w-full rounded-md bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-700 disabled:opacity-50"
              >
                {provision.isPending ? 'Provisioning…' : 'Provision Tenant'}
              </button>
            </form>

            {provisioned && (
              <div className="mt-4 rounded-lg bg-green-50 p-4">
                <p className="text-sm font-medium text-green-800">Tenant provisioned</p>
                <p className="mt-1 font-mono text-xs text-green-700">ID: {provisioned.id}</p>
                <p className="font-mono text-xs text-green-700">Slug: {provisioned.slug}</p>
              </div>
            )}
          </div>

          {/* ── Lookup ── */}
          <div className="space-y-6">
            <div className="rounded-lg border bg-white p-6 shadow-sm">
              <h2 className="mb-4 text-base font-medium text-slate-900">Look Up Tenant</h2>
              <div className="flex gap-3">
                <input
                  value={lookupInput}
                  onChange={(e) => setLookupInput(e.target.value)}
                  placeholder="Tenant UUID"
                  className="flex-1 rounded-md border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-slate-500"
                />
                <button
                  onClick={() => setActiveLookupId(lookupInput)}
                  className="rounded-md bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-700"
                >
                  Look Up
                </button>
              </div>

              {tenantError && <ErrorAlert message={(tenantError as Error).message} />}
              {tenantLoading && <p className="mt-3 text-sm text-slate-500">Loading…</p>}

              {tenant && (
                <dl className="mt-4 space-y-2 text-sm">
                  {([
                    ['Name',  tenant.name],
                    ['Slug',  tenant.slug],
                    ['Plan',  tenant.plan],
                    ['Email', tenant.contactEmail],
                  ] as [string, string][]).map(([label, value]) => (
                    <div key={label} className="flex justify-between">
                      <dt className="text-slate-500">{label}</dt>
                      <dd className="text-slate-900">{value}</dd>
                    </div>
                  ))}
                  <div className="flex justify-between">
                    <dt className="text-slate-500">Status</dt>
                    <dd><StatusBadge value={tenant.status} /></dd>
                  </div>
                </dl>
              )}
            </div>

            {environments && environments.length > 0 && (
              <div className="rounded-lg border bg-white p-6 shadow-sm">
                <h2 className="mb-3 text-sm font-medium text-slate-900">Environments</h2>
                <div className="space-y-2">
                  {environments.map((env) => (
                    <div
                      key={env.id}
                      className="flex items-center justify-between rounded bg-slate-50 px-3 py-2"
                    >
                      <div>
                        <p className="text-sm font-medium text-slate-900">{env.name}</p>
                        <p className="font-mono text-xs text-slate-500">{env.id}</p>
                      </div>
                      <StatusBadge value={env.type} />
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        </div>
      </main>
    </div>
  );
}

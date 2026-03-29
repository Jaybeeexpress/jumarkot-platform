'use client';

import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Nav } from '@/components/layout/Nav';
import { StatusBadge } from '@/components/ui/StatusBadge';
import { ErrorAlert } from '@/components/ui/ErrorAlert';
import { listRules, updateRuleStatus } from '@/lib/api/rules';
import type { RuleDto } from '@/types';

export default function RulesPage() {
  const [tenantInput, setTenantInput]   = useState('');
  const [envInput, setEnvInput]         = useState('PRODUCTION');
  const [activeFilter, setActiveFilter] = useState({ tenantId: '', envType: '' });

  const { data, isLoading, error } = useQuery({
    queryKey: ['rules', activeFilter.tenantId, activeFilter.envType],
    queryFn: () => listRules(activeFilter.tenantId, activeFilter.envType),
    enabled:  !!activeFilter.tenantId && !!activeFilter.envType,
  });

  const qc = useQueryClient();
  const toggleStatus = useMutation({
    mutationFn: ({ rule }: { rule: RuleDto }) =>
      updateRuleStatus(
        rule.id,
        rule.tenantId,
        rule.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE',
      ),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['rules'] }),
  });

  const rules: RuleDto[] = data?.success ? (data.data as RuleDto[]) : [];

  return (
    <div className="flex min-h-screen flex-col bg-slate-50">
      <Nav />
      <main className="flex-1 p-8">
        <h1 className="mb-6 text-2xl font-semibold text-slate-900">Rules</h1>

        {/* ── Filter bar ── */}
        <div className="mb-6 flex flex-wrap items-end gap-4 rounded-lg border bg-white p-4 shadow-sm">
          <div>
            <label className="block text-xs font-medium text-slate-600">Tenant ID</label>
            <input
              value={tenantInput}
              onChange={(e) => setTenantInput(e.target.value)}
              placeholder="xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
              className="mt-1 w-80 rounded-md border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-slate-500"
            />
          </div>
          <div>
            <label className="block text-xs font-medium text-slate-600">Environment</label>
            <select
              value={envInput}
              onChange={(e) => setEnvInput(e.target.value)}
              className="mt-1 rounded-md border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-slate-500"
            >
              <option value="PRODUCTION">PRODUCTION</option>
              <option value="SANDBOX">SANDBOX</option>
            </select>
          </div>
          <button
            onClick={() => setActiveFilter({ tenantId: tenantInput, envType: envInput })}
            className="rounded-md bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-700"
          >
            Load Rules
          </button>
        </div>

        {error && <ErrorAlert message={(error as Error).message} />}
        {isLoading && <p className="text-sm text-slate-500">Loading rules…</p>}
        {!isLoading && activeFilter.tenantId && rules.length === 0 && !error && (
          <p className="text-sm text-slate-500">No active rules found for this tenant / environment.</p>
        )}

        {/* ── Rules table ── */}
        {rules.length > 0 && (
          <div className="overflow-hidden rounded-lg border bg-white shadow-sm">
            <table className="w-full text-sm">
              <thead className="border-b bg-slate-50 text-left">
                <tr>
                  {['Name', 'Category', 'Priority', 'Action', 'Score Adj.', 'Status', ''].map((h) => (
                    <th key={h} className="px-4 py-3 font-medium text-slate-700">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody className="divide-y">
                {rules.map((rule) => (
                  <tr key={rule.id} className="hover:bg-slate-50">
                    <td className="px-4 py-3 font-medium text-slate-900">{rule.name}</td>
                    <td className="px-4 py-3 text-slate-600">{rule.category}</td>
                    <td className="px-4 py-3 text-slate-600">{rule.priority}</td>
                    <td className="px-4 py-3 text-slate-600">{rule.action}</td>
                    <td className="px-4 py-3 text-slate-600">+{rule.scoreAdjustment}</td>
                    <td className="px-4 py-3"><StatusBadge value={rule.status} /></td>
                    <td className="px-4 py-3">
                      <button
                        onClick={() => toggleStatus.mutate({ rule })}
                        disabled={toggleStatus.isPending}
                        className="text-xs text-slate-500 underline hover:text-slate-900 disabled:opacity-50"
                      >
                        {rule.status === 'ACTIVE' ? 'Deactivate' : 'Activate'}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </main>
    </div>
  );
}

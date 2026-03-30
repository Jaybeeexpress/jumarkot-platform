'use client';

import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Layers3, Sparkles } from 'lucide-react';
import { Nav } from '@/components/layout/Nav';
import { StatusBadge } from '@/components/ui/StatusBadge';
import { ErrorAlert } from '@/components/ui/ErrorAlert';
import { SuccessAlert } from '@/components/ui/SuccessAlert';
import { toUserFacingApiError } from '@/lib/api/errors';
import { isSuccessfulResponse } from '@/lib/api/response';
import { listRules, updateRuleStatus } from '@/lib/api/rules';
import type { RuleDto } from '@/types';

export default function RulesPage() {
  const [tenantInput, setTenantInput] = useState('');
  const [envInput, setEnvInput] = useState('PRODUCTION');
  const [activeFilter, setActiveFilter] = useState({ tenantId: '', envType: '' });
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const canLoadRules = tenantInput.trim().length > 0;

  const { data, isLoading, error } = useQuery({
    queryKey: ['rules', activeFilter.tenantId, activeFilter.envType],
    queryFn: () => listRules(activeFilter.tenantId, activeFilter.envType),
    enabled: !!activeFilter.tenantId && !!activeFilter.envType,
  });

  const qc = useQueryClient();
  const toggleStatus = useMutation({
    mutationFn: ({ rule }: { rule: RuleDto }) =>
      updateRuleStatus(
        rule.id,
        rule.tenantId,
        rule.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE',
      ),
    onSuccess: (_data, { rule }) => {
      setSuccessMessage(
        `Rule "${rule.name}" ${rule.status === 'ACTIVE' ? 'deactivated' : 'activated'} successfully.`,
      );
      qc.invalidateQueries({ queryKey: ['rules'] });
    },
  });

  const rules: RuleDto[] = isSuccessfulResponse(data) ? data.data : [];
  const listErrorMessage = error ? toUserFacingApiError(error, 'Failed to load rules.') : null;
  const toggleErrorMessage = toggleStatus.error
    ? toUserFacingApiError(toggleStatus.error, 'Failed to update rule status.')
    : null;

  return (
    <div className="soft-grid min-h-screen">
      <Nav />
      <main className="mx-auto flex w-full max-w-7xl flex-1 flex-col gap-6 px-4 pb-10 pt-6 md:px-6">
        <section className="animate-rise interactive-card rounded-2xl border border-cyan-100/80 bg-gradient-to-r from-cyan-900 via-sky-900 to-teal-800 px-6 py-7 text-cyan-50 shadow-2xl shadow-cyan-900/30 md:px-8">
          <div className="flex flex-col gap-3 md:flex-row md:items-end md:justify-between">
            <div>
              <p className="mb-2 inline-flex items-center gap-2 rounded-full bg-white/15 px-3 py-1 text-xs font-semibold uppercase tracking-[0.12em]">
                <Sparkles className="h-3.5 w-3.5" />
                Rule Workspace
              </p>
              <h1 className="font-heading display-gradient text-3xl font-semibold md:text-4xl">Rules Control Center</h1>
              <p className="mt-2 max-w-2xl text-sm text-cyan-100/90 md:text-base">
                Load tenant-specific rules, review scoring behavior, and toggle policy status in a single operational view.
              </p>
            </div>
            <div className="rounded-xl border border-white/20 bg-white/10 px-4 py-3 text-sm text-cyan-100/90">
              <p className="font-semibold text-white">Live Rule Count</p>
              <p className="mt-1 text-2xl font-bold text-white">{rules.length}</p>
            </div>
          </div>
        </section>

        <section className="glass-card interactive-card animate-fade-delayed rounded-2xl p-5 md:p-6">
          <div className="mb-4 flex items-center gap-2 text-sm font-semibold text-cyan-900">
            <Layers3 className="h-4 w-4" />
            Filter Rules
          </div>
          <div className="grid gap-4 md:grid-cols-[minmax(0,1fr)_220px_auto] md:items-end">
            <div>
              <label htmlFor="rules-tenant-id" className="text-xs font-semibold uppercase tracking-[0.08em] text-slate-500">
                Tenant ID
              </label>
              <input
                id="rules-tenant-id"
                value={tenantInput}
                onChange={(e) => setTenantInput(e.target.value)}
                placeholder="xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
                className="field-control mt-1.5 text-sm"
              />
            </div>
            <div>
              <label htmlFor="rules-environment" className="text-xs font-semibold uppercase tracking-[0.08em] text-slate-500">
                Environment
              </label>
              <select
                id="rules-environment"
                value={envInput}
                onChange={(e) => setEnvInput(e.target.value)}
                className="field-control mt-1.5 text-sm"
              >
                <option value="PRODUCTION">PRODUCTION</option>
                <option value="SANDBOX">SANDBOX</option>
              </select>
            </div>
            <button
              onClick={() => {
                setSuccessMessage(null);
                setActiveFilter({ tenantId: tenantInput.trim(), envType: envInput });
              }}
              disabled={!canLoadRules}
              className="btn-glow h-11 rounded-2xl px-5 text-sm font-semibold text-cyan-50 shadow-lg shadow-cyan-900/20 transition hover:-translate-y-0.5 disabled:cursor-not-allowed disabled:opacity-50"
            >
              Load Rules
            </button>
          </div>
        </section>

        {listErrorMessage && <ErrorAlert message={listErrorMessage} />}
        {toggleErrorMessage && <ErrorAlert message={toggleErrorMessage} />}
        {successMessage && <SuccessAlert message={successMessage} />}

        {!activeFilter.tenantId && (
          <section className="glass-card interactive-card rounded-2xl border-dashed p-8 text-center text-sm text-slate-500">
            Enter a tenant ID and choose an environment to fetch active rules.
          </section>
        )}

        {isLoading && (
          <section className="glass-card interactive-card rounded-2xl p-8 text-center text-sm text-slate-500">Loading rules...</section>
        )}

        {!isLoading && activeFilter.tenantId && rules.length === 0 && !error && (
          <section className="glass-card interactive-card rounded-2xl border-dashed p-8 text-center text-sm text-slate-500">
            No active rules found for this tenant and environment.
          </section>
        )}

        {rules.length > 0 && (
          <section className="glass-card interactive-card overflow-hidden rounded-2xl">
            <div className="overflow-x-auto">
              <table className="min-w-full text-sm">
                <thead className="bg-slate-100/70 text-left text-[11px] uppercase tracking-[0.1em] text-slate-600">
                  <tr>
                    {['Name', 'Category', 'Priority', 'Action', 'Score Adj.', 'Status', ''].map((h) => (
                      <th key={h} className="px-4 py-3 font-semibold md:px-5">{h}</th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {rules.map((rule) => (
                    <tr key={rule.id} className="border-t border-slate-200/70 transition hover:bg-cyan-50/40">
                      <td className="px-4 py-3.5 font-semibold text-slate-900 md:px-5">{rule.name}</td>
                      <td className="px-4 py-3.5 text-slate-600 md:px-5">{rule.category}</td>
                      <td className="px-4 py-3.5 text-slate-600 md:px-5">{rule.priority}</td>
                      <td className="px-4 py-3.5 text-slate-700 md:px-5">{rule.action}</td>
                      <td className="px-4 py-3.5 text-slate-600 md:px-5">+{rule.scoreAdjustment}</td>
                      <td className="px-4 py-3.5 md:px-5"><StatusBadge value={rule.status} /></td>
                      <td className="px-4 py-3.5 md:px-5">
                        <button
                          onClick={() => toggleStatus.mutate({ rule })}
                          disabled={toggleStatus.isPending}
                          className="rounded-2xl border border-cyan-200 bg-white px-3 py-1.5 text-xs font-semibold text-cyan-800 transition hover:-translate-y-0.5 hover:border-cyan-300 hover:bg-cyan-50 hover:shadow-md hover:shadow-cyan-900/10 disabled:opacity-50"
                        >
                          {rule.status === 'ACTIVE' ? 'Deactivate' : 'Activate'}
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>
        )}
      </main>
    </div>
  );
}

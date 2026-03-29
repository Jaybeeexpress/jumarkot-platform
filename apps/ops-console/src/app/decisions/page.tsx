'use client';

import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useMutation } from '@tanstack/react-query';
import { Nav } from '@/components/layout/Nav';
import { StatusBadge } from '@/components/ui/StatusBadge';
import { ErrorAlert } from '@/components/ui/ErrorAlert';
import { submitDecision } from '@/lib/api/decisions';
import type { DecisionResponse } from '@/types';

const schema = z.object({
  entityId:   z.string().min(1, 'Required'),
  entityType: z.string().min(1, 'Required'),
  eventType:  z.string().min(1, 'Required'),
  payload:    z.string().optional(),
});
type FormData = z.infer<typeof schema>;

export default function DecisionsPage() {
  const [result, setResult] = useState<DecisionResponse | null>(null);

  const { register, handleSubmit, formState: { errors } } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: {
      entityId: '',
      entityType: 'ACCOUNT',
      eventType: 'PAYMENT',
      payload: JSON.stringify({ amount: 500, currency: 'USD' }, null, 2),
    },
  });

  const mutation = useMutation({
    mutationFn: (data: FormData) => {
      let parsedPayload: Record<string, unknown> = {};
      if (data.payload) {
        try { parsedPayload = JSON.parse(data.payload); } catch { /* ignore */ }
      }
      return submitDecision({
        idempotencyKey: `ops-${Date.now()}-${Math.random().toString(36).slice(2, 8)}`,
        entityId:   data.entityId,
        entityType: data.entityType,
        eventType:  data.eventType,
        payload:    parsedPayload,
      });
    },
    onSuccess: (res) => { if (res.success) setResult(res.data); },
  });

  const inputCls =
    'mt-1 w-full rounded-md border px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-slate-500';

  return (
    <div className="flex min-h-screen flex-col bg-slate-50">
      <Nav />
      <main className="flex-1 p-8">
        <h1 className="mb-6 text-2xl font-semibold text-slate-900">Decision Evaluator</h1>

        <div className="grid gap-6 lg:grid-cols-2">
          {/* ── Evaluation form ── */}
          <form
            onSubmit={handleSubmit((d) => mutation.mutate(d))}
            className="space-y-4 rounded-lg border bg-white p-6 shadow-sm"
          >
            <h2 className="text-base font-medium text-slate-900">Submit Evaluation</h2>

            <div>
              <label className="block text-sm font-medium text-slate-700">Entity ID</label>
              <input {...register('entityId')} placeholder="user-123" className={inputCls} />
              {errors.entityId && <p className="mt-1 text-xs text-red-600">{errors.entityId.message}</p>}
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-700">Entity Type</label>
              <input {...register('entityType')} placeholder="ACCOUNT" className={inputCls} />
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-700">Event Type</label>
              <input {...register('eventType')} placeholder="PAYMENT" className={inputCls} />
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-700">Payload (JSON)</label>
              <textarea {...register('payload')} rows={5}
                className="mt-1 w-full rounded-md border px-3 py-2 font-mono text-sm focus:outline-none focus:ring-2 focus:ring-slate-500"
              />
            </div>

            {mutation.error && (
              <ErrorAlert message={(mutation.error as Error).message ?? 'Evaluation failed'} />
            )}

            <button
              type="submit"
              disabled={mutation.isPending}
              className="w-full rounded-md bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-700 disabled:opacity-50"
            >
              {mutation.isPending ? 'Evaluating…' : 'Evaluate'}
            </button>
          </form>

          {/* ── Result panel ── */}
          {result ? (
            <div className="rounded-lg border bg-white p-6 shadow-sm">
              <h2 className="mb-4 text-base font-medium text-slate-900">Result</h2>
              <dl className="space-y-3 text-sm">
                <Row label="Decision"><StatusBadge value={result.decision} /></Row>
                <Row label="Risk Level"><StatusBadge value={result.riskLevel} /></Row>
                <Row label="Risk Score">
                  <span className="font-medium text-slate-900">{result.riskScore}</span>
                </Row>
                <Row label="Recommended Action">
                  <span className="font-medium text-slate-900">{result.recommendedAction}</span>
                </Row>
                <Row label="Decision ID">
                  <span className="max-w-[200px] truncate font-mono text-xs text-slate-600">
                    {result.decisionId}
                  </span>
                </Row>

                {result.matchedRules?.length > 0 && (
                  <div>
                    <p className="mb-2 text-slate-500">Matched Rules</p>
                    <div className="space-y-1">
                      {result.matchedRules.map((r) => (
                        <div
                          key={r.ruleId}
                          className="flex items-center justify-between rounded bg-slate-50 px-3 py-2 text-xs"
                        >
                          <span className="font-medium">{r.ruleName}</span>
                          <span className="text-slate-500">
                            +{r.scoreContribution} pts · {r.reasonCode}
                          </span>
                        </div>
                      ))}
                    </div>
                  </div>
                )}
              </dl>
            </div>
          ) : (
            <div className="flex items-center justify-center rounded-lg border border-dashed bg-white p-12 text-sm text-slate-400">
              Result will appear here after evaluation
            </div>
          )}
        </div>
      </main>
    </div>
  );
}

function Row({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div className="flex items-center justify-between">
      <dt className="text-slate-500">{label}</dt>
      <dd>{children}</dd>
    </div>
  );
}

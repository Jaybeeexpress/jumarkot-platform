'use client';

import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation } from '@tanstack/react-query';
import { CheckCircle2, ShieldCheck, Sparkles } from 'lucide-react';
import { z } from 'zod';
import { Nav } from '@/components/layout/Nav';
import { StatusBadge } from '@/components/ui/StatusBadge';
import { ErrorAlert } from '@/components/ui/ErrorAlert';
import { SuccessAlert } from '@/components/ui/SuccessAlert';
import { submitDecision } from '@/lib/api/decisions';
import { toUserFacingApiError } from '@/lib/api/errors';
import { isSuccessfulResponse } from '@/lib/api/response';
import type { DecisionResponse } from '@/types';

const schema = z.object({
  entityId: z.string().min(1, 'Required'),
  entityType: z.string().min(1, 'Required'),
  eventType: z.string().min(1, 'Required'),
  payload: z.string().optional(),
});

type FormData = z.infer<typeof schema>;

export default function DecisionsPage() {
  const [result, setResult] = useState<DecisionResponse | null>(null);
  const [lastDecisionId, setLastDecisionId] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<FormData>({
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
        try {
          parsedPayload = JSON.parse(data.payload);
        } catch {
          throw new Error('INVALID_JSON_PAYLOAD');
        }
      }

      return submitDecision({
        idempotencyKey: `ops-${Date.now()}-${Math.random().toString(36).slice(2, 8)}`,
        entityId: data.entityId,
        entityType: data.entityType,
        eventType: data.eventType,
        payload: parsedPayload,
      });
    },
    onMutate: () => {
      setResult(null);
      setLastDecisionId(null);
    },
    onSuccess: (res) => {
      if (isSuccessfulResponse(res)) {
        setResult(res.data);
        setLastDecisionId(res.data.decisionId);
      }
    },
  });

  const mutationMessage = mutation.error
    ? toUserFacingApiError(mutation.error, 'Evaluation failed. Please try again.')
    : null;

  const inputCls =
    'mt-1.5 w-full rounded-xl border border-slate-200 bg-white px-3 py-2.5 text-sm text-slate-800 shadow-sm transition focus:border-cyan-400 focus:outline-none focus:ring-2 focus:ring-cyan-200';

  return (
    <div className="soft-grid min-h-screen">
      <Nav />
      <main className="mx-auto flex w-full max-w-7xl flex-1 flex-col gap-6 px-4 pb-10 pt-6 md:px-6">
        <section className="animate-rise rounded-2xl border border-teal-100/80 bg-gradient-to-r from-slate-900 via-cyan-900 to-teal-800 px-6 py-7 text-cyan-50 shadow-2xl shadow-cyan-900/30 md:px-8">
          <p className="mb-2 inline-flex items-center gap-2 rounded-full bg-white/15 px-3 py-1 text-xs font-semibold uppercase tracking-[0.12em]">
            <Sparkles className="h-3.5 w-3.5" />
            Decision Engine
          </p>
          <h1 className="font-heading text-3xl font-semibold leading-tight md:text-4xl">Risk Decision Evaluator</h1>
          <p className="mt-2 max-w-3xl text-sm text-cyan-100/90 md:text-base">
            Simulate live risk evaluations, inspect matched rule impact, and validate automated actions in real time.
          </p>
        </section>

        <div className="grid gap-6 lg:grid-cols-[minmax(0,1fr)_minmax(0,1fr)]">
          <form
            onSubmit={handleSubmit((d) => mutation.mutate(d))}
            className="glass-card animate-fade-delayed space-y-4 rounded-2xl p-5 md:p-6"
          >
            <div className="mb-1 flex items-center gap-2 text-sm font-semibold text-cyan-900">
              <ShieldCheck className="h-4 w-4" />
              Submit Evaluation
            </div>

            <div>
              <label className="text-xs font-semibold uppercase tracking-[0.08em] text-slate-500">Entity ID</label>
              <input {...register('entityId')} placeholder="user-123" className={inputCls} />
              {errors.entityId && <p className="mt-1 text-xs text-rose-600">{errors.entityId.message}</p>}
            </div>

            <div className="grid gap-4 sm:grid-cols-2">
              <div>
                <label className="text-xs font-semibold uppercase tracking-[0.08em] text-slate-500">Entity Type</label>
                <input {...register('entityType')} placeholder="ACCOUNT" className={inputCls} />
              </div>
              <div>
                <label className="text-xs font-semibold uppercase tracking-[0.08em] text-slate-500">Event Type</label>
                <input {...register('eventType')} placeholder="PAYMENT" className={inputCls} />
              </div>
            </div>

            <div>
              <label className="text-xs font-semibold uppercase tracking-[0.08em] text-slate-500">Payload JSON</label>
              <textarea
                {...register('payload')}
                rows={7}
                className="mt-1.5 w-full rounded-xl border border-slate-200 bg-white px-3 py-2.5 font-mono text-xs text-slate-700 shadow-sm transition focus:border-cyan-400 focus:outline-none focus:ring-2 focus:ring-cyan-200"
              />
            </div>

            {mutationMessage && <ErrorAlert message={mutationMessage} />}

            <button
              type="submit"
              disabled={mutation.isPending}
              className="flex h-11 w-full items-center justify-center rounded-xl bg-cyan-900 px-4 text-sm font-semibold text-cyan-50 shadow-lg shadow-cyan-900/20 transition hover:bg-cyan-800 disabled:opacity-50"
            >
              {mutation.isPending ? 'Evaluating...' : 'Evaluate Decision'}
            </button>
          </form>

          {mutation.isPending ? (
            <section className="glass-card flex animate-fade-delayed items-center justify-center rounded-2xl p-10 text-center text-sm text-slate-500">
              Evaluating request with live backend services...
            </section>
          ) : result ? (
            <section className="glass-card animate-fade-delayed rounded-2xl p-5 md:p-6">
              <h2 className="font-heading mb-4 text-xl font-semibold text-slate-900">Evaluation Result</h2>

              {lastDecisionId && (
                <div className="mb-4">
                  <SuccessAlert message={`Evaluation completed. Decision ID: ${lastDecisionId}`} />
                </div>
              )}

              <div className="grid gap-2 rounded-xl border border-slate-200/80 bg-white/80 p-4 text-sm">
                <Row label="Decision"><StatusBadge value={result.decision} /></Row>
                <Row label="Risk Level"><StatusBadge value={result.riskLevel} /></Row>
                <Row label="Risk Score"><strong className="text-slate-900">{result.riskScore}</strong></Row>
                <Row label="Recommended Action"><strong className="text-slate-900">{result.recommendedAction}</strong></Row>
                <Row label="Decision ID">
                  <span className="max-w-[220px] truncate rounded bg-slate-100 px-2 py-1 font-mono text-[11px] text-slate-700">
                    {result.decisionId}
                  </span>
                </Row>
              </div>

              {result.matchedRules?.length > 0 && (
                <div className="mt-5">
                  <p className="mb-2 text-xs font-semibold uppercase tracking-[0.1em] text-slate-500">Matched Rules</p>
                  <div className="space-y-2">
                    {result.matchedRules.map((r) => (
                      <div
                        key={r.ruleId}
                        className="flex items-center justify-between rounded-xl border border-slate-200/70 bg-white/90 px-3 py-2 text-xs"
                      >
                        <span className="font-semibold text-slate-800">{r.ruleName}</span>
                        <span className="text-slate-500">+{r.scoreContribution} pts · {r.reasonCode}</span>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </section>
          ) : (
            <section className="glass-card flex animate-fade-delayed items-center justify-center rounded-2xl border-dashed p-10 text-center text-sm text-slate-500">
              <div>
                <CheckCircle2 className="mx-auto mb-3 h-6 w-6 text-cyan-700" />
                Result details will appear here after evaluation.
              </div>
            </section>
          )}
        </div>
      </main>
    </div>
  );
}

function Row({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div className="flex items-center justify-between gap-3 border-b border-slate-100 py-2 last:border-b-0">
      <span className="text-slate-500">{label}</span>
      <div className="text-right">{children}</div>
    </div>
  );
}

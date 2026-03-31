'use client';

import { useState } from 'react';
import { LockKeyhole, Sparkles } from 'lucide-react';

type WorkspaceKey = 'analystQueue' | 'decisionPolicies' | 'entityGraph' | 'webhookEvents';

type WorkspaceConfig = {
  label: string;
  freshness: string;
  availability: string;
  stats: Array<[string, string]>;
  notes: string[];
  alerts: Array<[string, string, string, string]>;
  bars: number[];
};

const workspaceOrder: WorkspaceKey[] = ['analystQueue', 'decisionPolicies', 'entityGraph', 'webhookEvents'];

const workspaceConfig: Record<WorkspaceKey, WorkspaceConfig> = {
  analystQueue: {
    label: 'Analyst Queue',
    freshness: 'Updated 12 seconds ago',
    availability: '99.94%',
    stats: [
      ['Signals Routed', '18.4K'],
      ['Review Escalations', '241'],
      ['Policy Matches', '3.1K'],
    ],
    notes: [
      'Escalation pressure elevated in EU login flows.',
      'Queue routing stable after recent policy rollout.',
      'Webhook retries within SLO for all production tenants.',
    ],
    alerts: [
      ['ALT-90314', 'Credential abuse pattern', '91', 'Open'],
      ['ALT-90309', 'Transaction routing spike', '88', 'Review'],
      ['ALT-90288', 'Impossible travel sequence', '95', 'Escalated'],
    ],
    bars: [48, 66, 58, 78, 72, 84, 96, 91, 110, 106, 118, 130],
  },
  decisionPolicies: {
    label: 'Decision Policies',
    freshness: 'Updated 3 minutes ago',
    availability: '99.81%',
    stats: [
      ['Rules Evaluated', '9.8K'],
      ['Override Reviews', '36'],
      ['Routing Paths', '17'],
    ],
    notes: [
      'Approval policy drift remains within threshold.',
      'One manual override cluster requires secondary review.',
      'Recent simulation run validated route stability.',
    ],
    alerts: [
      ['POL-214', 'Velocity threshold adjustment', '84', 'Review'],
      ['POL-198', 'Device trust downgrade', '76', 'Staged'],
      ['POL-173', 'Manual override path', '89', 'Active'],
    ],
    bars: [34, 45, 52, 63, 70, 74, 79, 88, 96, 103, 112, 118],
  },
  entityGraph: {
    label: 'Entity Graph',
    freshness: 'Updated 47 seconds ago',
    availability: '99.89%',
    stats: [
      ['Linked Entities', '6.4K'],
      ['Device Clusters', '412'],
      ['Shared Signals', '1.2K'],
    ],
    notes: [
      'New account/device linkage cluster formed in APAC region.',
      'High-confidence entity joins remain explainable and stable.',
      'Downstream queues are prioritizing graph-driven escalations.',
    ],
    alerts: [
      ['ENT-441', 'Shared device cluster', '87', 'Open'],
      ['ENT-429', 'Linked payout account', '79', 'Review'],
      ['ENT-418', 'Identity overlap path', '92', 'Escalated'],
    ],
    bars: [28, 36, 48, 57, 61, 69, 75, 82, 94, 101, 109, 121],
  },
  webhookEvents: {
    label: 'Webhook Events',
    freshness: 'Updated 21 seconds ago',
    availability: '99.97%',
    stats: [
      ['Deliveries', '42.1K'],
      ['Retries', '118'],
      ['P95 Latency', '112ms'],
    ],
    notes: [
      'Retry volume normalized after partner endpoint recovery.',
      'Production event fan-out remains within target latency.',
      'No dropped deliveries detected in the last 12-hour window.',
    ],
    alerts: [
      ['WH-812', 'Partner retry sequence', '64', 'Stable'],
      ['WH-801', 'Latency spike resolved', '58', 'Recovered'],
      ['WH-794', 'Delivery replay audit', '72', 'Tracked'],
    ],
    bars: [42, 54, 61, 66, 73, 80, 86, 92, 95, 104, 111, 119],
  },
};

function statusTone(value: string): string {
  if (value === 'Escalated' || value === 'Active') {
    return 'bg-[rgba(255,45,111,0.12)] text-[#FDA4AF]';
  }
  if (value === 'Review' || value === 'Tracked' || value === 'Staged') {
    return 'bg-[rgba(245,158,11,0.14)] text-[#FCD34D]';
  }
  return 'bg-[rgba(56,189,248,0.14)] text-[#7DD3FC]';
}

const barHeightClassMap: Record<number, string> = {
  28: 'h-[28px]',
  34: 'h-[34px]',
  36: 'h-[36px]',
  42: 'h-[42px]',
  45: 'h-[45px]',
  48: 'h-[48px]',
  52: 'h-[52px]',
  54: 'h-[54px]',
  57: 'h-[57px]',
  58: 'h-[58px]',
  61: 'h-[61px]',
  63: 'h-[63px]',
  66: 'h-[66px]',
  69: 'h-[69px]',
  70: 'h-[70px]',
  72: 'h-[72px]',
  73: 'h-[73px]',
  74: 'h-[74px]',
  75: 'h-[75px]',
  78: 'h-[78px]',
  79: 'h-[79px]',
  80: 'h-[80px]',
  82: 'h-[82px]',
  84: 'h-[84px]',
  86: 'h-[86px]',
  88: 'h-[88px]',
  91: 'h-[91px]',
  92: 'h-[92px]',
  94: 'h-[94px]',
  95: 'h-[95px]',
  96: 'h-[96px]',
  101: 'h-[101px]',
  103: 'h-[103px]',
  104: 'h-[104px]',
  106: 'h-[106px]',
  109: 'h-[109px]',
  110: 'h-[110px]',
  111: 'h-[111px]',
  112: 'h-[112px]',
  118: 'h-[118px]',
  119: 'h-[119px]',
  121: 'h-[121px]',
  130: 'h-[130px]',
};

function barHeightClass(height: number): string {
  return barHeightClassMap[height] ?? 'h-[48px]';
}

export function InteractivePreview() {
  const [activeWorkspace, setActiveWorkspace] = useState<WorkspaceKey>('analystQueue');
  const current = workspaceConfig[activeWorkspace];

  return (
    <div className="landing-preview-card overflow-hidden">
      <div className="flex items-center justify-between border-b border-[#1F2937] bg-[#11182B] px-5 py-4">
        <div className="flex items-center gap-3">
          <span className="inline-flex h-8 items-center rounded-full border border-[var(--landing-border)] bg-[rgba(56,189,248,0.08)] px-3 text-[12px] font-semibold uppercase tracking-[0.04em] text-[var(--landing-tech)]">
            Live Console
          </span>
          <span className="text-[13px] text-landing-muted">{current.freshness}</span>
        </div>
        <div className="flex items-center gap-2 text-[11px] font-semibold uppercase tracking-[0.04em] text-landing-muted">
          <Sparkles className="h-3.5 w-3.5 text-[var(--landing-accent)]" />
          Premium Preview
        </div>
      </div>

      <div className="grid min-h-[480px] gap-0 lg:grid-cols-[220px_1fr]">
        <aside className="border-r border-[var(--landing-border)] bg-[#0D1325] p-5">
          <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">Workspaces</div>
          <div className="mt-4 space-y-3">
            {workspaceOrder.map((workspace) => {
              const active = workspace === activeWorkspace;

              return (
                <button
                  key={workspace}
                  type="button"
                  onClick={() => setActiveWorkspace(workspace)}
                  className={`w-full rounded-[10px] border px-4 py-3 text-left text-[13px] font-medium transition-all duration-150 ease-in ${active ? 'border-[rgba(255,45,111,0.35)] bg-[rgba(255,45,111,0.12)] text-landing-primary' : 'border-[var(--landing-border)] bg-[rgba(17,24,43,0.92)] text-landing-secondary hover:bg-[rgba(42,52,80,0.72)]'}`}
                >
                  {workspaceConfig[workspace].label}
                </button>
              );
            })}
          </div>

          <div className="mt-6 rounded-[12px] border border-[var(--landing-border)] bg-[#11182B] p-4">
            <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">Signal Freshness</div>
            <div className="mt-3 flex items-end justify-between">
              <div>
                <div className="text-[28px] font-bold text-landing-primary">{current.availability}</div>
                <div className="text-[12px] text-landing-muted">ingestion availability</div>
              </div>
              <span className="inline-flex items-center gap-1 rounded-full bg-[rgba(34,197,94,0.12)] px-2 py-1 text-[11px] font-semibold text-[#86EFAC]">
                <span className="h-1.5 w-1.5 rounded-full bg-[var(--landing-live)]" />
                Live
              </span>
            </div>
          </div>
        </aside>

        <div className="bg-[#0F172A] p-6">
          <div className="rounded-[12px] border border-[var(--landing-border)] bg-[#11182B] px-4 py-3">
            <div className="flex flex-col gap-3 xl:flex-row xl:items-center xl:justify-between">
              <div>
                <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">Decision Throughput</div>
                <div className="mt-1 text-[18px] font-semibold text-landing-primary">{current.label}</div>
              </div>
              <div className="flex flex-wrap items-center gap-2">
                <span className="inline-flex items-center gap-2 rounded-full bg-[rgba(56,189,248,0.1)] px-3 py-1 text-[11px] font-semibold uppercase tracking-[0.04em] text-[var(--landing-tech)]">
                  <LockKeyhole className="h-3.5 w-3.5" />
                  Protected
                </span>
                <span className="rounded-full border border-[var(--landing-border)] px-3 py-1 text-[11px] font-semibold uppercase tracking-[0.04em] text-landing-muted">
                  Production
                </span>
                <span className="rounded-full border border-[var(--landing-border)] px-3 py-1 text-[11px] font-semibold uppercase tracking-[0.04em] text-landing-muted">
                  Last 12 Hours
                </span>
              </div>
            </div>
          </div>

          <div className="mt-4 grid gap-4 xl:grid-cols-[1.45fr_0.95fr]">
            <section className="rounded-[12px] border border-[var(--landing-border)] bg-[#11182B] p-5">
              <div className="grid gap-3 sm:grid-cols-3">
                {current.stats.map(([label, value]) => (
                  <div key={label} className="rounded-[10px] border border-[var(--landing-border)] bg-[rgba(15,23,42,0.9)] px-4 py-3">
                    <div className="text-[11px] font-semibold uppercase tracking-[0.04em] text-landing-muted">{label}</div>
                    <div className="mt-2 text-[24px] font-bold text-landing-primary">{value}</div>
                  </div>
                ))}
              </div>

              <div className="mt-6 flex h-[190px] items-end gap-3 rounded-[12px] border border-[var(--landing-border)] bg-[rgba(15,23,42,0.92)] px-4 py-4">
                {current.bars.map((height, index) => (
                  <div key={index} className="flex flex-1 flex-col items-center gap-2">
                    <div
                      className={`w-full rounded-t-[10px] transition-all duration-150 ease-in ${barHeightClass(height)} ${index >= 9 ? 'bg-[var(--landing-accent)]' : 'bg-[rgba(56,189,248,0.75)]'}`}
                    />
                    <span className="text-[11px] font-medium text-landing-faint">W{index + 1}</span>
                  </div>
                ))}
              </div>

              <div className="mt-5 overflow-hidden rounded-[12px] border border-[var(--landing-border)] bg-[rgba(15,23,42,0.92)]">
                <div className="grid grid-cols-[1.2fr_1.6fr_0.7fr_0.8fr] border-b border-[var(--landing-border)] bg-[#11182B] px-4 py-3 text-[11px] font-semibold uppercase tracking-[0.04em] text-landing-muted">
                  <span>Alert</span>
                  <span>Reason</span>
                  <span>Score</span>
                  <span>Status</span>
                </div>
                {current.alerts.map((alert) => (
                  <div key={alert[0]} className="grid grid-cols-[1.2fr_1.6fr_0.7fr_0.8fr] items-center border-b border-[rgba(43,53,80,0.7)] px-4 py-3 text-[13px] last:border-b-0 hover:bg-[rgba(50,62,93,0.24)]">
                    <span className="font-semibold text-[var(--landing-tech)]">{alert[0]}</span>
                    <span className="text-landing-secondary">{alert[1]}</span>
                    <span className="font-semibold text-landing-primary">{alert[2]}</span>
                    <span className={`inline-flex w-fit rounded-full px-2.5 py-1 text-[11px] font-semibold ${statusTone(alert[3])}`}>{alert[3]}</span>
                  </div>
                ))}
              </div>
            </section>

            <section className="space-y-4">
              {[
                ['High Risk Decisions', current.stats[0]?.[1] ?? '0', '+4.8% vs prior day'],
                ['Open Investigations', current.stats[1]?.[1] ?? '0', 'Updated 4 mins ago'],
                ['Webhook Latency', current.stats[2]?.[1] ?? '0', 'P95 across production'],
              ].map(([label, value, note], index) => (
                <div key={label} className="rounded-[12px] border border-[var(--landing-border)] bg-[#11182B] p-4">
                  <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">{label}</div>
                  <div className="mt-3 flex items-end justify-between gap-3">
                    <div className="text-[30px] font-bold leading-none text-landing-primary">{value}</div>
                    <span className={`inline-flex h-7 items-center rounded-full px-2.5 text-[11px] font-semibold ${index === 0 ? 'bg-[rgba(255,45,111,0.12)] text-[#FDA4AF]' : index === 1 ? 'bg-[rgba(245,158,11,0.14)] text-[#FCD34D]' : 'bg-[rgba(56,189,248,0.14)] text-[#7DD3FC]'}`}>
                      {index === 0 ? 'Review' : index === 1 ? 'Queue' : 'Stable'}
                    </span>
                  </div>
                  <div className="mt-2 text-[13px] text-landing-muted">{note}</div>
                </div>
              ))}

              <div className="rounded-[12px] border border-[var(--landing-border)] bg-[#11182B] p-4">
                <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">Analyst Notes</div>
                <div className="mt-3 space-y-3">
                  {current.notes.map((note) => (
                    <div key={note} className="rounded-[10px] border border-[var(--landing-border)] bg-[rgba(15,23,42,0.92)] px-4 py-3 text-[13px] text-landing-secondary">
                      {note}
                    </div>
                  ))}
                </div>
              </div>
            </section>
          </div>
        </div>
      </div>
    </div>
  );
}

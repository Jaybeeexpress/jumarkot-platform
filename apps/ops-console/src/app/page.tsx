import { AppShell } from '@/components/layout/AppShell';
import type { Route } from 'next';
import Link from 'next/link';
import { DashboardTable } from '@/components/dashboard/DashboardTable';
import { DashboardTrendChart } from '@/components/dashboard/DashboardTrendChart';
import { MetricCard } from '@/components/dashboard/MetricCard';
import { QueueSummaryCard } from '@/components/dashboard/QueueSummaryCard';
import { ArrowUpRight, Clock3, Filter, Siren, Sparkles } from 'lucide-react';

const kpis = [
  { label: 'Alerts Today', value: '184', delta: 'vs prior window +12.4%', accent: 'danger' as const },
  { label: 'High Risk Decisions', value: '62', delta: 'vs prior window +4.8%', accent: 'warning' as const },
  { label: 'Review Rate', value: '8.7%', delta: 'vs prior window -1.1%', accent: 'brand' as const },
];

const queue = [
  { label: 'Open Alerts', value: '47', status: 'OPEN' as const, updatedAt: 'Updated 2 mins ago' },
  { label: 'Cases In Review', value: '19', status: 'IN_PROGRESS' as const, updatedAt: 'Updated 4 mins ago' },
  { label: 'Resolved Today', value: '63', status: 'RESOLVED' as const, updatedAt: 'Updated 1 min ago' },
];

const caseActivity = [
  { id: 'CASE-918', event: 'Ownership reassigned to L. Chen', age: '1 min ago', status: 'IN_PROGRESS' },
  { id: 'CASE-913', event: 'Escalated for sanctions review', age: '7 mins ago', status: 'OPEN' },
  { id: 'CASE-907', event: 'Supporting docs attached', age: '13 mins ago', status: 'IN_PROGRESS' },
  { id: 'CASE-901', event: 'Closed after analyst approval', age: '24 mins ago', status: 'RESOLVED' },
];

const recentAlerts = [
  ['ALT-90314', '2026-03-31 09:11', 'user_982', 'LOGIN_ATTEMPT', '91', 'HIGH', 'OPEN', 'S. Cole'],
  ['ALT-90309', '2026-03-31 09:04', 'acct_188', 'TRANSACTION', '88', 'HIGH', 'IN_PROGRESS', 'L. Chen'],
  ['ALT-90302', '2026-03-31 08:52', 'user_110', 'DEVICE_CHANGE', '74', 'MEDIUM', 'OPEN', 'A. Diaz'],
  ['ALT-90297', '2026-03-31 08:40', 'vendor_19', 'DOCUMENT_REVIEW', '67', 'MEDIUM', 'RESOLVED', 'J. Khan'],
  ['ALT-90288', '2026-03-31 08:19', 'user_044', 'LOGIN_ATTEMPT', '95', 'CRITICAL', 'OPEN', 'M. Ford'],
];

const trendData = [
  { day: 'Mar 18', pressure: 46 },
  { day: 'Mar 19', pressure: 52 },
  { day: 'Mar 20', pressure: 49 },
  { day: 'Mar 21', pressure: 58 },
  { day: 'Mar 22', pressure: 61 },
  { day: 'Mar 23', pressure: 55 },
  { day: 'Mar 24', pressure: 64 },
  { day: 'Mar 25', pressure: 68 },
  { day: 'Mar 26', pressure: 62 },
  { day: 'Mar 27', pressure: 73 },
  { day: 'Mar 28', pressure: 77 },
  { day: 'Mar 29', pressure: 71 },
  { day: 'Mar 30', pressure: 79 },
  { day: 'Mar 31', pressure: 83 },
];

const isLoading = false;

export default function DashboardPage() {
  return (
    <AppShell
      title="Dashboard"
      subtitle="Live overview for alert pressure, queue throughput, and investigator activity."
      breadcrumb={['Ops Console', 'Dashboard']}
    >
      <div className="section-stack gap-[24px] dashboard-container">
        <section className="enterprise-banner-slim">
          <div className="flex items-center gap-3">
            <span className="mc-workspace-icon h-9 w-9">
              <Sparkles className="h-4 w-4 text-[#67E8F9]" />
            </span>
            <div>
              <div className="text-[13px] font-semibold text-primary">Dashboard Workspace</div>
              <p className="text-[12px] text-secondary">Centralized view of alerts, decision trends, and analyst queue performance.</p>
            </div>
          </div>
          <div className="flex flex-wrap items-center gap-3 text-[12px] text-muted">
            <span className="enterprise-chip enterprise-chip-info">Active Window: 24h</span>
            <button type="button" className="enterprise-button enterprise-button-secondary h-8 gap-1 px-3 text-[12px]">
              Open full timeline
              <ArrowUpRight className="h-3.5 w-3.5" />
            </button>
          </div>
        </section>

        <div className="app-grid">
          {kpis.map((card) => (
            <MetricCard key={card.label} label={card.label} value={card.value} delta={card.delta} accent={card.accent} />
          ))}
        </div>

        <div className="app-grid">
          <section className="enterprise-card-dense row-span-8 min-h-[420px]">
            <div className="mb-5 flex flex-wrap items-center justify-between gap-3">
              <div>
                <div className="enterprise-label">Decision Trend</div>
                <h3 className="mt-1">Daily Review Pressure</h3>
              </div>
              <Link href="/decisions" className="enterprise-button enterprise-button-secondary">Open Decisions</Link>
            </div>
            <DashboardTrendChart data={trendData} isLoading={isLoading} />
          </section>

          <section className="enterprise-card-dense row-span-4 min-h-[420px]">
            <div className="mb-4 flex flex-wrap items-center justify-between gap-3">
              <div>
                <div className="enterprise-label">Queue Summary</div>
                <h3 className="mt-1">Analyst Throughput</h3>
              </div>
              <button type="button" className="enterprise-button enterprise-button-secondary h-8 gap-2 px-3 text-[12px]">
                <Filter className="h-3.5 w-3.5" />
                Filters
              </button>
            </div>
            <div className="mt-4 space-y-3">
              {queue.map((item) => (
                <QueueSummaryCard
                  key={item.label}
                  label={item.label}
                  value={item.value}
                  status={item.status}
                  updatedAt={item.updatedAt}
                />
              ))}
            </div>
          </section>
        </div>

        <div className="app-grid">
          <section className="enterprise-card-dense row-span-8 min-h-[360px]">
            <div className="mb-4 flex flex-wrap items-center justify-between gap-3">
              <div>
                <div className="enterprise-label">Recent Alerts</div>
                <h3 className="mt-1">Analyst Queue</h3>
              </div>
              <Link href="/alerts" className="enterprise-button enterprise-button-secondary">View All Alerts</Link>
            </div>
            <DashboardTable rows={recentAlerts} isLoading={isLoading} />
          </section>

          <section className="enterprise-card-dense row-span-4 min-h-[360px]">
            <div className="mb-4 flex flex-wrap items-center justify-between gap-3">
              <div>
                <div className="enterprise-label">Case Activity</div>
                <h3 className="mt-1">Latest Investigator Actions</h3>
              </div>
              <Link href={'/cases' as Route} className="enterprise-button enterprise-button-secondary h-8 px-3 text-[12px]">Open Cases</Link>
            </div>

            <div className="space-y-3">
              {caseActivity.map((item) => (
                <article key={item.id} className="rounded-[10px] border border-light bg-panel p-3 transition-all duration-150 ease-in hover:bg-[#243245]">
                  <div className="flex items-start justify-between gap-2">
                    <div>
                      <div className="inline-flex items-center gap-2 text-[12px] font-semibold text-primary">
                        <Siren className="h-3.5 w-3.5 text-[#60A5FA]" />
                        {item.id}
                      </div>
                      <p className="mt-1 text-[12px] text-secondary">{item.event}</p>
                    </div>
                    <span className="enterprise-chip enterprise-chip-info">{item.status}</span>
                  </div>
                  <div className="mt-2 inline-flex items-center gap-1 text-[11px] text-muted">
                    <Clock3 className="h-3.5 w-3.5" />
                    {item.age}
                  </div>
                </article>
              ))}
            </div>
          </section>
        </div>
      </div>
    </AppShell>
  );
}

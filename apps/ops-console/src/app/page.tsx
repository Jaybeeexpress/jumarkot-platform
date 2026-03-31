import Link from 'next/link';
import { AppShell } from '@/components/layout/AppShell';
import { DashboardTable } from '@/components/dashboard/DashboardTable';
import { DashboardTrendChart } from '@/components/dashboard/DashboardTrendChart';
import { MetricCard } from '@/components/dashboard/MetricCard';
import { QueueSummaryCard } from '@/components/dashboard/QueueSummaryCard';

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
    <AppShell title="Dashboard" breadcrumb={['Ops Console', 'Dashboard']}>
      <div className="section-stack">
        <div className="app-grid">
          {kpis.map((card) => (
            <MetricCard key={card.label} label={card.label} value={card.value} delta={card.delta} accent={card.accent} />
          ))}
        </div>

        <div className="app-grid">
          <section className="enterprise-card-dense row-span-8 min-h-[420px]">
            <div className="mb-5 flex items-center justify-between">
              <div>
                <div className="enterprise-label">Decision Trend</div>
                <h3 className="mt-1">Daily Review Pressure</h3>
              </div>
              <Link href="/decisions" className="enterprise-button enterprise-button-secondary">Open Decisions</Link>
            </div>
            <DashboardTrendChart data={trendData} isLoading={isLoading} />
          </section>

          <section className="enterprise-card-dense row-span-4 min-h-[420px]">
            <div className="enterprise-label">Queue Summary</div>
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

        <section className="enterprise-card-dense min-h-[320px]">
          <div className="mb-4 flex items-center justify-between">
            <div>
              <div className="enterprise-label">Recent Alerts</div>
              <h3 className="mt-1">Analyst Queue</h3>
            </div>
            <Link href="/alerts" className="enterprise-button enterprise-button-secondary">View All Alerts</Link>
          </div>
          <DashboardTable rows={recentAlerts} isLoading={isLoading} />
        </section>
      </div>
    </AppShell>
  );
}

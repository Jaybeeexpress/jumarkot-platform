import type { Route } from 'next';
import Link from 'next/link';
import { AppShell } from '@/components/layout/AppShell';
import { StatusBadge } from '@/components/ui/StatusBadge';

const kpis = [
  { label: 'Alerts Today', value: '184', delta: '+12.4%' },
  { label: 'High Risk Decisions', value: '62', delta: '+4.8%' },
  { label: 'Review Rate', value: '8.7%', delta: '-1.1%' },
];

const queue = [
  { label: 'Open Alerts', value: '47', status: 'OPEN' },
  { label: 'Cases In Review', value: '19', status: 'IN_PROGRESS' },
  { label: 'Resolved Today', value: '63', status: 'RESOLVED' },
];

const recentAlerts = [
  ['ALT-90314', '2026-03-31 09:11', 'user_982', 'LOGIN_ATTEMPT', '91', 'HIGH', 'OPEN', 'S. Cole'],
  ['ALT-90309', '2026-03-31 09:04', 'acct_188', 'TRANSACTION', '88', 'HIGH', 'IN_PROGRESS', 'L. Chen'],
  ['ALT-90302', '2026-03-31 08:52', 'user_110', 'DEVICE_CHANGE', '74', 'MEDIUM', 'OPEN', 'A. Diaz'],
  ['ALT-90297', '2026-03-31 08:40', 'vendor_19', 'DOCUMENT_REVIEW', '67', 'MEDIUM', 'RESOLVED', 'J. Khan'],
];

const trendBarHeights = ['h-[84px]', 'h-[92px]', 'h-[123px]', 'h-[108px]', 'h-[141px]', 'h-[128px]', 'h-[158px]', 'h-[145px]', 'h-[134px]', 'h-[165px]', 'h-[154px]', 'h-[180px]'];

export default function DashboardPage() {
  return (
    <AppShell title="Dashboard" breadcrumb={['Ops Console', 'Dashboard']}>
      <div className="section-stack">
        <div className="app-grid">
          {kpis.map((card) => (
            <section key={card.label} className="enterprise-card-dense row-span-4 flex h-[120px] flex-col justify-between">
              <div className="enterprise-label">{card.label}</div>
              <div>
                <div className="text-[28px] font-semibold leading-none text-primary">{card.value}</div>
                <div className="mt-2 text-[13px] text-secondary">vs prior window {card.delta}</div>
              </div>
            </section>
          ))}
        </div>

        <div className="app-grid">
          <section className="enterprise-card-dense row-span-8">
            <div className="mb-5 flex items-center justify-between">
              <div>
                <div className="enterprise-label">Decision Trend</div>
                <h3 className="mt-1">Daily Review Pressure</h3>
              </div>
              <Link href="/decisions" className="enterprise-button enterprise-button-secondary">Open Decisions</Link>
            </div>
            <div className="flex h-[260px] items-end gap-3 rounded-md border border-light px-4 py-5">
              {trendBarHeights.map((heightClass, index) => (
                <div key={index} className="flex flex-1 flex-col items-center gap-2">
                  <div className={`w-full rounded-t-md bg-[var(--brand-primary)]/80 ${heightClass}`} />
                  <span className="text-[11px] text-muted">W{index + 1}</span>
                </div>
              ))}
            </div>
          </section>

          <section className="enterprise-card-dense row-span-4">
            <div className="enterprise-label">Queue Summary</div>
            <div className="mt-5 space-y-3">
              {queue.map((item) => (
                <div key={item.label} className="enterprise-panel flex items-center justify-between px-4 py-3">
                  <div>
                    <div className="text-[13px] font-medium text-primary">{item.label}</div>
                    <div className="mt-1 text-[12px] text-muted">Updated 2 min ago</div>
                  </div>
                  <div className="flex items-center gap-3">
                    <div className="text-[20px] font-semibold text-primary">{item.value}</div>
                    <StatusBadge value={item.status} />
                  </div>
                </div>
              ))}
            </div>
          </section>
        </div>

        <section className="enterprise-card-dense">
          <div className="mb-4 flex items-center justify-between">
            <div>
              <div className="enterprise-label">Recent Alerts</div>
              <h3 className="mt-1">Analyst Queue</h3>
            </div>
            <Link href="/alerts" className="enterprise-button enterprise-button-secondary">View All Alerts</Link>
          </div>
          <table className="enterprise-table">
            <thead>
              <tr>
                <th>Alert ID</th>
                <th>Time</th>
                <th>Entity</th>
                <th>Event Type</th>
                <th>Risk Score</th>
                <th>Severity</th>
                <th>Status</th>
                <th>Assignee</th>
              </tr>
            </thead>
            <tbody>
              {recentAlerts.map((row) => (
                <tr key={row[0]}>
                  <td><Link href={`/alerts/${row[0]}` as Route} className="text-[var(--info)] hover:underline">{row[0]}</Link></td>
                  <td>{row[1]}</td>
                  <td>{row[2]}</td>
                  <td>{row[3]}</td>
                  <td>{row[4]}</td>
                  <td><StatusBadge value={row[5]} /></td>
                  <td><StatusBadge value={row[6]} /></td>
                  <td>{row[7]}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>
      </div>
    </AppShell>
  );
}

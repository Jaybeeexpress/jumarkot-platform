import type { Route } from 'next';
import Link from 'next/link';
import { EnterpriseShell } from '@/components/layout/EnterpriseShell';
import { StatusBadge } from '@/components/ui/StatusBadge';
import { OPS_CONSOLE_NAV_SECTIONS } from '@/lib/nav-config';

const alerts = [
  ['ALT-90314', '2026-03-31 09:11', 'user_982', 'LOGIN_ATTEMPT', '91', 'HIGH', 'OPEN', 'S. Cole'],
  ['ALT-90309', '2026-03-31 09:04', 'acct_188', 'TRANSACTION', '88', 'HIGH', 'IN_PROGRESS', 'L. Chen'],
  ['ALT-90302', '2026-03-31 08:52', 'user_110', 'DEVICE_CHANGE', '74', 'MEDIUM', 'OPEN', 'A. Diaz'],
  ['ALT-90297', '2026-03-31 08:40', 'vendor_19', 'DOCUMENT_REVIEW', '67', 'MEDIUM', 'RESOLVED', 'J. Khan'],
  ['ALT-90288', '2026-03-31 08:19', 'user_044', 'LOGIN_ATTEMPT', '95', 'CRITICAL', 'OPEN', 'M. Ford'],
];

export default function AlertsPage() {
  return (
    <EnterpriseShell
      title="Alerts"
      breadcrumb={['Ops Console', 'Alerts']}
      navSections={OPS_CONSOLE_NAV_SECTIONS}
      searchPlaceholder="Search alert ID, entity, assignee"
      environmentLabel="Production"
    >
      <div className="section-stack">
        <section className="enterprise-card-dense">
          <div className="flex h-14 items-center gap-4">
            <input className="enterprise-input w-[300px]" placeholder="Search alert ID, entity, assignee" />
            <select aria-label="Filter by severity" title="Filter by severity" className="enterprise-input w-[160px]"><option>Severity</option></select>
            <select aria-label="Filter by status" title="Filter by status" className="enterprise-input w-[160px]"><option>Status</option></select>
            <select aria-label="Filter by event" title="Filter by event" className="enterprise-input w-[160px]"><option>Event</option></select>
          </div>
        </section>

        <section className="enterprise-card-dense">
          <table className="enterprise-table">
            <thead>
              <tr>
                <th className="w-[120px]">Alert ID</th>
                <th className="w-[160px]">Time</th>
                <th className="w-[200px]">Entity</th>
                <th className="w-[160px]">Event Type</th>
                <th className="w-[100px]">Risk Score</th>
                <th className="w-[100px]">Severity</th>
                <th className="w-[120px]">Status</th>
                <th className="w-[140px]">Assignee</th>
              </tr>
            </thead>
            <tbody>
              {alerts.map((row) => (
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
    </EnterpriseShell>
  );
}

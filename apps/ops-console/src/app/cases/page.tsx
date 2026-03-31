import type { Route } from 'next';
import Link from 'next/link';
import { EnterpriseShell } from '@/components/layout/EnterpriseShell';
import { StatusBadge } from '@/components/ui/StatusBadge';
import { OPS_CONSOLE_NAV_SECTIONS } from '@/lib/nav-config';

const cases = [
  ['CASE-1208', '2026-03-31 09:02', 'ALT-90314', 'P1', '00:18:42', 'OPEN', 'S. Cole'],
  ['CASE-1205', '2026-03-31 08:41', 'ALT-90302', 'P2', '01:09:10', 'IN_PROGRESS', 'A. Diaz'],
  ['CASE-1198', '2026-03-31 08:07', 'ALT-90288', 'P1', '00:42:18', 'OPEN', 'L. Chen'],
];

export default function CasesPage() {
  return (
    <EnterpriseShell
      title="Cases"
      breadcrumb={['Ops Console', 'Cases']}
      navSections={OPS_CONSOLE_NAV_SECTIONS}
      searchPlaceholder="Search case ID or alert..."
      environmentLabel="Production"
    >
      <div className="section-stack">
        <section className="enterprise-card-dense">
          <div className="flex h-14 items-center gap-4">
            <input className="enterprise-input w-[300px]" placeholder="Search case ID or alert" />
            <select aria-label="Filter by priority" title="Filter by priority" className="enterprise-input w-[160px]"><option>Priority</option></select>
            <select aria-label="Filter by status" title="Filter by status" className="enterprise-input w-[160px]"><option>Status</option></select>
            <select aria-label="Filter by assignee" title="Filter by assignee" className="enterprise-input w-[160px]"><option>Assignee</option></select>
          </div>
        </section>
        <section className="enterprise-card-dense">
          <table className="enterprise-table">
            <thead>
              <tr>
                <th>Case ID</th>
                <th>Time</th>
                <th>Alert</th>
                <th>Priority</th>
                <th>SLA Timer</th>
                <th>Status</th>
                <th>Assignee</th>
              </tr>
            </thead>
            <tbody>
              {cases.map((row) => (
                <tr key={row[0]}>
                  <td><Link href={`/cases/${row[0]}` as Route} className="text-[var(--info)] hover:underline">{row[0]}</Link></td>
                  <td>{row[1]}</td>
                  <td>{row[2]}</td>
                  <td><StatusBadge value={row[3]} /></td>
                  <td>{row[4]}</td>
                  <td><StatusBadge value={row[5]} /></td>
                  <td>{row[6]}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>
      </div>
    </EnterpriseShell>
  );
}
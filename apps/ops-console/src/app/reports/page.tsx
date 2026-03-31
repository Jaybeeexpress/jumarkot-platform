import { EnterpriseShell } from '@/components/layout/EnterpriseShell';
import { OPS_CONSOLE_NAV_SECTIONS } from '@/lib/nav-config';

const reportMetrics = [
  { title: 'Weekly Throughput', value: '1,247', unit: 'decisions reviewed' },
  { title: 'Avg Review Time', value: '4.2m', unit: 'per decision' },
  { title: 'Error Rate', value: '0.3%', unit: 'below 1% threshold' },
];

const reports = [
  { name: 'Weekly Analyst Performance', updated: '2026-03-31', format: 'PDF' },
  { name: 'Decision Distribution Analysis', updated: '2026-03-30', format: 'CSV' },
  { name: 'Fraud Detection Accuracy', updated: '2026-03-29', format: 'PDF' },
  { name: 'Rule Effectiveness Report', updated: '2026-03-28', format: 'Excel' },
];

export default function ReportsPage() {
  return (
    <EnterpriseShell
      title="Reports"
      breadcrumb={['Ops Console', 'Reports']}
      navSections={OPS_CONSOLE_NAV_SECTIONS}
      searchPlaceholder="Search reports..."
      environmentLabel="Production"
    >
      <div className="section-stack">
        {/* Key Metrics */}
        <div className="app-grid">
          {reportMetrics.map((metric) => (
            <section key={metric.title} className="enterprise-card-dense">
              <div className="enterprise-label">{metric.title}</div>
              <div className="mt-4">
                <div className="text-[28px] font-semibold text-primary">{metric.value}</div>
                <div className="mt-2 text-[13px] text-secondary">{metric.unit}</div>
              </div>
            </section>
          ))}
        </div>

        {/* Report Sections */}
        <div className="app-grid">
          <section className="enterprise-card-dense row-span-12">
            <div className="enterprise-label">Operational Reporting</div>
            <h3 className="mt-2">Weekly analyst throughput</h3>
            <div className="mt-4 grid grid-cols-7 gap-2">
              {['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'].map((day, idx) => (
                <div key={day} className="enterprise-panel p-3 text-center">
                  <div className="text-[11px] text-muted">{day}</div>
                  <div className="mt-2 h-16 bg-gradient-to-t from-[var(--jk-indigo-500)] from-[${[35, 45, 52, 68, 73, 40, 50][idx]}%] to-transparent rounded" />
                  <div className="mt-2 text-[13px] text-primary">{[35, 45, 52, 68, 73, 40, 50][idx]}</div>
                </div>
              ))}
            </div>
          </section>

          <section className="enterprise-card-dense row-span-12">
            <div className="enterprise-label">Decision Distribution</div>
            <h3 className="mt-2">Weekly decision breakdown</h3>
            <div className="mt-4 space-y-3">
              {[
                { label: 'Approved', count: '628', pct: '50%' },
                { label: 'Reviewed', count: '456', pct: '36%' },
                { label: 'Escalated', count: '144', pct: '11%' },
                { label: 'Declined', count: '19', pct: '2%' },
              ].map((item) => (
                <div key={item.label} className="enterprise-panel p-4">
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-[13px] text-primary">{item.label}</span>
                    <span className="text-[13px] font-semibold text-primary">{item.count}</span>
                  </div>
                  <div className="h-2 bg-opacity-20 rounded overflow-hidden">
                    <div
                      className="h-full bg-[var(--jk-indigo-500)]"
                      style={{ width: item.pct }}
                    />
                  </div>
                  <div className="mt-1 text-[11px] text-muted text-right">{item.pct}</div>
                </div>
              ))}
            </div>
          </section>
        </div>

        {/* Available Reports */}
        <section className="enterprise-card-dense">
          <div className="enterprise-label mb-4">Available Reports</div>
          <table className="enterprise-table">
            <thead>
              <tr>
                <th>Report Name</th>
                <th>Last Updated</th>
                <th>Format</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {reports.map((report) => (
                <tr key={report.name}>
                  <td className="text-[var(--info)]">{report.name}</td>
                  <td>{report.updated}</td>
                  <td>
                    <span className="inline-flex items-center rounded-full bg-[var(--jk-indigo-500)] bg-opacity-10 px-3 py-1 text-[11px] text-[var(--jk-indigo-300)]">
                      {report.format}
                    </span>
                  </td>
                  <td><button className="text-[13px] text-[var(--info)] hover:underline">Download</button></td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>
      </div>
    </EnterpriseShell>
  );
}
import { PortalShell } from '@/components/layout/PortalShell';
import { StatusPill } from '@/components/ui/StatusPill';

const keys = [
  ['Platform Sandbox', 'SANDBOX', '2026-03-24', '2026-03-31 08:58'],
  ['Checkout Service', 'PRODUCTION', '2026-03-18', '2026-03-31 08:11'],
  ['Fraud Simulator', 'SANDBOX', '2026-03-12', '2026-03-30 18:02'],
];

const keyStats = [
  { label: 'Active Keys', value: '3', trend: '2 in production' },
  { label: 'Last Rotated', value: '18 days', trend: 'Sandbox: 7 days' },
  { label: 'Key Usage', value: '1.28M', trend: 'requests this month' },
];

export default function ApiKeysPage() {
  return (
    <PortalShell title="API Keys" breadcrumb={['Developer Portal', 'API Keys']}>
      <div className="portal-stack">
        {/* Key Statistics */}
        <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
          {keyStats.map((stat) => (
            <section key={stat.label} className="portal-card-dense">
              <div className="portal-label text-[12px]">{stat.label}</div>
              <div className="mt-3">
                <div className="text-[24px] font-semibold text-white">{stat.value}</div>
                <div className="mt-2 text-[12px] text-[#9CA3AF]">{stat.trend}</div>
              </div>
            </section>
          ))}
        </div>

        {/* Key Management Actions */}
        <div className="flex items-center justify-between">
          <div>
            <div className="portal-label">Key Management</div>
            <h3 className="mt-1 text-[16px] font-semibold text-white">Access Credentials</h3>
          </div>
          <div className="flex gap-2">
            <button className="portal-button portal-button-primary">Create</button>
            <button className="portal-button portal-button-secondary">Rotate</button>
            <button className="portal-button portal-button-secondary">Revoke</button>
          </div>
        </div>

        {/* Keys Table */}
        <section className="portal-card-dense">
          <table className="portal-table">
            <thead>
              <tr>
                <th>Key name</th>
                <th>Environment</th>
                <th>Created</th>
                <th>Last used</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {keys.map((row) => (
                <tr key={row[0]}>
                  <td className="font-medium">{row[0]}</td>
                  <td><StatusPill value={row[1]} /></td>
                  <td>{row[2]}</td>
                  <td>{row[3]}</td>
                  <td>
                    <button className="text-[13px] text-[#6366F1] hover:underline">View</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>

        {/* Security Recommendations */}
        <section className="portal-card-dense">
          <div className="portal-label mb-4">Security Recommendations</div>
          <div className="space-y-3">
            {[
              { icon: '⚠️', title: 'Sandbox key unused for 8 days', action: 'Consider revoking' },
              { icon: '✓', title: 'Prod keys rotated recently', action: 'Good security posture' },
              { icon: 'ℹ️', title: 'API key expiration policy', action: 'Enable for auto-rotation' },
            ].map((item) => (
              <div key={item.title} className="flex items-start justify-between rounded-md border border-[#374151] p-3">
                <div className="flex gap-3">
                  <span className="text-[18px]">{item.icon}</span>
                  <div className="text-[13px]">
                    <div className="text-white">{item.title}</div>
                    <div className="mt-1 text-[#9CA3AF]">Last activity: {['8 days', '2 hours', 'Never'][['Sandbox key unused for 8 days', 'Prod keys rotated recently', 'API key expiration policy'].indexOf(item.title)]} ago</div>
                  </div>
                </div>
                <button className="text-[12px] text-[#6366F1] hover:underline whitespace-nowrap">{item.action}</button>
              </div>
            ))}
          </div>
        </section>
      </div>
    </PortalShell>
  );
}
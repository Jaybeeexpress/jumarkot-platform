import { PortalShell } from '@/components/layout/PortalShell';
import { StatusPill } from '@/components/ui/StatusPill';

const keys = [
  ['Platform Sandbox', 'SANDBOX', '2026-03-24', '2026-03-31 08:58'],
  ['Checkout Service', 'PRODUCTION', '2026-03-18', '2026-03-31 08:11'],
  ['Fraud Simulator', 'SANDBOX', '2026-03-12', '2026-03-30 18:02'],
];

export default function ApiKeysPage() {
  return (
    <PortalShell title="API Keys" breadcrumb={['Developer Portal', 'API Keys']}>
      <div className="portal-stack">
        <div className="flex items-center justify-between">
          <div>
            <div className="portal-label">Key Management</div>
            <h3 className="mt-1">Access Credentials</h3>
          </div>
          <div className="flex gap-2">
            <button className="portal-button portal-button-primary">Create</button>
            <button className="portal-button portal-button-secondary">Rotate</button>
            <button className="portal-button portal-button-secondary">Revoke</button>
          </div>
        </div>
        <section className="portal-card-dense">
          <table className="portal-table">
            <thead>
              <tr>
                <th>Key name</th>
                <th>Environment</th>
                <th>Created</th>
                <th>Last used</th>
              </tr>
            </thead>
            <tbody>
              {keys.map((row) => (
                <tr key={row[0]}>
                  <td>{row[0]}</td>
                  <td><StatusPill value={row[1]} /></td>
                  <td>{row[2]}</td>
                  <td>{row[3]}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>
      </div>
    </PortalShell>
  );
}
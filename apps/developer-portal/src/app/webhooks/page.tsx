import { PortalShell } from '@/components/layout/PortalShell';
import { StatusPill } from '@/components/ui/StatusPill';

const webhooks = [
  ['Risk Decisions', 'https://api.acme.com/jumarkot/decisions', 'ACTIVE', '2026-03-31 08:54', '0.8%'],
  ['Alert Events', 'https://api.acme.com/jumarkot/alerts', 'DEGRADED', '2026-03-31 08:47', '2.4%'],
];

export default function WebhooksPage() {
  return (
    <PortalShell title="Webhooks" breadcrumb={['Developer Portal', 'Webhooks']}>
      <div className="portal-stack">
        <section className="portal-card-dense">
          <table className="portal-table">
            <thead>
              <tr>
                <th>URL</th>
                <th>Status</th>
                <th>Last delivery</th>
                <th>Fail rate</th>
              </tr>
            </thead>
            <tbody>
              {webhooks.map((row) => (
                <tr key={row[0]}>
                  <td>{row[1]}</td>
                  <td><StatusPill value={row[2]} /></td>
                  <td>{row[3]}</td>
                  <td>{row[4]}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>
        <section className="portal-card-dense">
          <div className="flex items-center justify-between">
            <div>
              <div className="portal-label">Delivery Logs</div>
              <h3 className="mt-1">Recent Retries</h3>
            </div>
            <button className="portal-button portal-button-primary">Retry</button>
          </div>
        </section>
      </div>
    </PortalShell>
  );
}
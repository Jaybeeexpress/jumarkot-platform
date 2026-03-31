import { PortalShell } from '@/components/layout/PortalShell';
import { StatusPill } from '@/components/ui/StatusPill';

const requests = [
  ['REQ-8011', 'POST /v1/events', '2026-03-31 09:02', '202', '48ms'],
  ['REQ-8008', 'POST /v1/decisions', '2026-03-31 08:57', '200', '112ms'],
  ['REQ-8003', 'GET /v1/webhooks', '2026-03-31 08:41', '200', '31ms'],
];

export default function RequestLogsPage() {
  return (
    <PortalShell title="Request Logs" breadcrumb={['Developer Portal', 'Request Logs']}>
      <div className="portal-grid">
        <section className="portal-card-dense col-span-12 xl:col-span-8">
          <table className="portal-table">
            <thead>
              <tr>
                <th>Request ID</th>
                <th>Route</th>
                <th>Timestamp</th>
                <th>Status</th>
                <th>Latency</th>
              </tr>
            </thead>
            <tbody>
              {requests.map((row) => (
                <tr key={row[0]}>
                  <td>{row[0]}</td>
                  <td>{row[1]}</td>
                  <td>{row[2]}</td>
                  <td><StatusPill value={row[3] === '202' ? 'ACTIVE' : 'SANDBOX'} /></td>
                  <td>{row[4]}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>
        <section className="portal-card-dense col-span-12 xl:col-span-4">
          <div className="portal-label">Request Detail</div>
          <div className="mt-4 border-b border-[#374151] pb-3">
            <div className="flex gap-2">
              <button className="portal-button portal-button-primary">Request</button>
              <button className="portal-button portal-button-secondary">Response</button>
            </div>
          </div>
          <pre className="mt-4 overflow-auto rounded-md bg-[#0B1220] p-4 text-[12px] leading-6 text-[#D1FAE5]">{`{
  "id": "REQ-8011",
  "route": "/v1/events",
  "status": 202,
  "latencyMs": 48
}`}</pre>
        </section>
      </div>
    </PortalShell>
  );
}
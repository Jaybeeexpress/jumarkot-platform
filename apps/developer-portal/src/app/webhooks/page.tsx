import { PortalShell } from '@/components/layout/PortalShell';
import { StatusPill } from '@/components/ui/StatusPill';

const webhooks = [
  ['Risk Decisions', 'https://api.acme.com/jumarkot/decisions', 'ACTIVE', '2026-03-31 08:54', '99.2%', '2.4K/day'],
  ['Alert Events', 'https://api.acme.com/jumarkot/alerts', 'DEGRADED', '2026-03-31 08:47', '97.6%', '1.8K/day'],
];

const webhookStats = [
  { label: 'Webhooks Sent', value: '54K', trend: 'Last 24 hours' },
  { label: 'Success Rate', value: '99.1%', trend: 'Average latency: 125ms' },
  { label: 'Failed Deliveries', value: '12', trend: '2 auto-retried' },
];

export default function WebhooksPage() {
  return (
    <PortalShell title="Webhooks" breadcrumb={['Developer Portal', 'Webhooks']}>
      <div className="portal-stack">
        {/* Webhook Statistics */}
        <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
          {webhookStats.map((stat) => (
            <section key={stat.label} className="portal-card-dense">
              <div className="portal-label text-[12px]">{stat.label}</div>
              <div className="mt-3">
                <div className="text-[24px] font-semibold text-white">{stat.value}</div>
                <div className="mt-2 text-[12px] text-[#9CA3AF]">{stat.trend}</div>
              </div>
            </section>
          ))}
        </div>

        {/* Webhook Endpoints */}
        <section className="portal-card-dense">
          <div className="portal-label mb-4">Webhook Endpoints</div>
          <table className="portal-table">
            <thead>
              <tr>
                <th>Event Type</th>
                <th>URL</th>
                <th>Status</th>
                <th>Last delivery</th>
                <th>Success Rate</th>
                <th>Volume (24h)</th>
              </tr>
            </thead>
            <tbody>
              {webhooks.map((row) => (
                <tr key={row[0]}>
                  <td className="font-medium">{row[0]}</td>
                  <td className="text-[12px] text-[#9CA3AF]">{row[1]}</td>
                  <td><StatusPill value={row[2]} /></td>
                  <td>{row[3]}</td>
                  <td>
                    <span
                      className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-[12px] font-medium ${
                        parseFloat(row[4]) > 98
                          ? 'bg-[#86EFAC] text-[#015312]'
                          : 'bg-[#FEF08A] text-[#713F12]'
                      }`}
                    >
                      {row[4]}
                    </span>
                  </td>
                  <td className="text-right text-[#9CA3AF]">{row[5]}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>

        {/* Delivery Timeline */}
        <section className="portal-card-dense">
          <div className="flex items-center justify-between mb-4">
            <div>
              <div className="portal-label">Delivery Logs</div>
              <h3 className="mt-1 text-[16px] font-semibold text-white">Recent Events</h3>
            </div>
            <button className="portal-button portal-button-primary">Retry Failed</button>
          </div>
          <div className="space-y-3">
            {[
              { time: '08:54', event: 'Risk Decisions webhook', status: '✓ Success', statusColor: 'text-[#86EFAC]' },
              { time: '08:47', event: 'Alert Events webhook', status: '↻ Retrying', statusColor: 'text-[#FEF08A]' },
              { time: '08:42', event: 'Risk Decisions webhook', status: '✓ Success', statusColor: 'text-[#86EFAC]' },
              { time: '08:31', event: 'Alert Events webhook', status: '✗ Failed (timeout)', statusColor: 'text-[#FCA5A5]' },
            ].map((item) => (
              <div
                key={item.time}
                className="flex items-center justify-between rounded-md border border-[#374151] p-3"
              >
                <div className="flex items-center gap-4">
                  <div className="min-w-[50px] text-[12px] font-medium text-[#6B7280]">{item.time}</div>
                  <div className="text-[13px] text-[#D1D5DB]">{item.event}</div>
                </div>
                <div className={`text-[12px] font-medium ${item.statusColor}`}>{item.status}</div>
              </div>
            ))}
          </div>
        </section>

        {/* Management Actions */}
        <section className="portal-card-dense">
          <div className="portal-label mb-4">Webhook Management</div>
          <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
            {[
              { title: 'Create Endpoint', desc: 'Add a new webhook endpoint' },
              { title: 'Test Webhook', desc: 'Send test events to endpoints' },
              { title: 'View Signature Keys', desc: 'Manage webhook signing keys' },
              { title: 'Export Delivery Logs', desc: 'Download detailed delivery records' },
            ].map((action) => (
              <button
                key={action.title}
                className="flex items-center justify-between rounded-md border border-[#374151] p-4 hover:bg-[#1F2937] transition-colors"
              >
                <div className="text-left">
                  <div className="text-[13px] font-medium text-white">{action.title}</div>
                  <div className="mt-1 text-[12px] text-[#9CA3AF]">{action.desc}</div>
                </div>
                <span className="text-[16px]">→</span>
              </button>
            ))}
          </div>
        </section>
      </div>
    </PortalShell>
  );
}
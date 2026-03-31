import { PortalShell } from '@/components/layout/PortalShell';

const series = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug'];
const barHeights = ['h-[72px]', 'h-[98px]', 'h-[110px]', 'h-[128px]', 'h-[144px]', 'h-[132px]', 'h-[168px]', 'h-[186px]'];

const usageMetrics = [
  { label: 'Total Requests', value: '1.28M', trend: '+18% vs last month' },
  { label: 'Failed (API)', value: '0.2%', trend: 'Avg latency: 87ms' },
  { label: 'Webhooks Sent', value: '54K', trend: '+22% vs last month' },
];

const environments = [
  { name: 'Sandbox', requests: '482,110', errorRate: '0.8%', webhooks: '12,104', avgLatency: '95ms' },
  { name: 'Production', requests: '798,220', errorRate: '0.2%', webhooks: '41,822', avgLatency: '78ms' },
];

export default function UsagePage() {
  return (
    <PortalShell title="Usage" breadcrumb={['Developer Portal', 'Usage']}>
      <div className="portal-stack">
        {/* Usage Metrics */}
        <div className="grid grid-cols-1 gap-4 md:grid-cols-3">
          {usageMetrics.map((metric) => (
            <section key={metric.label} className="portal-card-dense">
              <div className="portal-label text-[12px]">{metric.label}</div>
              <div className="mt-3">
                <div className="text-[24px] font-semibold text-white">{metric.value}</div>
                <div className="mt-2 text-[12px] text-[#9CA3AF]">{metric.trend}</div>
              </div>
            </section>
          ))}
        </div>

        {/* API Consumption Chart */}
        <section className="portal-card-dense">
          <div className="portal-label">Line Chart</div>
          <h3 className="mt-1 text-[16px] font-semibold text-white">API Consumption Trend</h3>
          <div className="mt-5 flex h-[260px] items-end gap-3 rounded-md border border-[#374151] px-4 py-5">
            {series.map((month, index) => (
              <div key={month} className="flex flex-1 flex-col items-center gap-2">
                <div className={`w-full rounded-t-md bg-gradient-to-t from-[#6366F1] to-[#818CF8] ${barHeights[index]}`} />
                <span className="text-[11px] text-[#6B7280]">{month}</span>
              </div>
            ))}
          </div>
        </section>

        {/* Environment Breakdown */}
        <section className="portal-card-dense">
          <div className="portal-label mb-4">Environment Breakdown</div>
          <table className="portal-table">
            <thead>
              <tr>
                <th>Environment</th>
                <th>Requests</th>
                <th>Error Rate</th>
                <th>Webhooks</th>
                <th>Avg Latency</th>
              </tr>
            </thead>
            <tbody>
              {environments.map((env) => (
                <tr key={env.name}>
                  <td className="font-medium">{env.name}</td>
                  <td className="text-right">{env.requests}</td>
                  <td>
                    <span
                      className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-[12px] font-medium ${
                        env.name === 'Sandbox'
                          ? 'bg-[#FEF08A] text-[#713F12]'
                          : 'bg-[#86EFAC] text-[#015312]'
                      }`}
                    >
                      {env.errorRate}
                    </span>
                  </td>
                  <td className="text-right">{env.webhooks}</td>
                  <td className="text-right text-[#9CA3AF]">{env.avgLatency}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>

        {/* Recent Activity */}
        <section className="portal-card-dense">
          <div className="portal-label mb-4">API Health Timeline</div>
          <div className="space-y-3">
            {[
              { time: '08:42', event: 'Error rate spike to 1.2% - auto-scaled API servers' },
              { time: '08:15', event: 'New API keys provisioned for staging environment' },
              { time: '07:30', event: 'Webhook delivery latency optimized - avg reduced to 78ms' },
              { time: '06:45', event: 'Database replica sync completed' },
            ].map((item) => (
              <div key={item.time} className="flex gap-4 rounded-md border border-[#374151] p-3">
                <div className="min-w-[60px] text-right text-[12px] font-medium text-[#6B7280]">{item.time}</div>
                <div className="text-[13px] text-[#D1D5DB]">{item.event}</div>
              </div>
            ))}
          </div>
        </section>
      </div>
    </PortalShell>
  );
}
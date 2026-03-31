import { PortalShell } from '@/components/layout/PortalShell';

const series = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug'];

export default function UsagePage() {
  return (
    <PortalShell title="Usage" breadcrumb={['Developer Portal', 'Usage']}>
      <div className="portal-stack">
        <section className="portal-card-dense">
          <div className="portal-label">Line Chart</div>
          <h3 className="mt-1">API Consumption</h3>
          <div className="mt-5 flex h-[260px] items-end gap-3 rounded-md border border-[#374151] px-4 py-5">
            {series.map((month, index) => (
              <div key={month} className="flex flex-1 flex-col items-center gap-2">
                <div className={`w-full rounded-t-md bg-[#6366F1] ${['h-[72px]','h-[98px]','h-[110px]','h-[128px]','h-[144px]','h-[132px]','h-[168px]','h-[186px]'][index]}`} />
                <span className="text-[11px] text-[#6B7280]">{month}</span>
              </div>
            ))}
          </div>
        </section>
        <section className="portal-card-dense">
          <div className="portal-label">Breakdown</div>
          <table className="portal-table mt-4">
            <thead>
              <tr>
                <th>Environment</th>
                <th>Requests</th>
                <th>Error Rate</th>
                <th>Webhook Volume</th>
              </tr>
            </thead>
            <tbody>
              <tr><td>Sandbox</td><td>482,110</td><td>0.8%</td><td>12,104</td></tr>
              <tr><td>Production</td><td>798,220</td><td>0.2%</td><td>41,822</td></tr>
            </tbody>
          </table>
        </section>
      </div>
    </PortalShell>
  );
}
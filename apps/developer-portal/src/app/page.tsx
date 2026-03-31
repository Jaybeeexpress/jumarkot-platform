import { PortalShell } from '@/components/layout/PortalShell';
import { StatusPill } from '@/components/ui/StatusPill';

const metrics = [
  ['Requests Today', '1.28M'],
  ['Errors', '312'],
  ['Webhook Success Rate', '99.2%'],
  ['Usage %', '74%'],
];

export default function OverviewPage() {
  return (
    <PortalShell title="Overview" breadcrumb={['Developer Portal', 'Overview']}>
      <div className="portal-stack">
        <div className="portal-grid">
          {metrics.map(([label, value]) => (
            <section key={label} className="portal-card-dense col-span-12 xl:col-span-3">
              <div className="portal-label">{label}</div>
              <div className="mt-4 text-[28px] font-semibold text-white">{value}</div>
              <div className="mt-2 text-[13px] text-[#9CA3AF]">Updated in near real time</div>
            </section>
          ))}
        </div>

        <div className="portal-grid">
          <section className="portal-card-dense col-span-12 xl:col-span-8">
            <div className="portal-label">Integration Readiness</div>
            <h3 className="mt-2">Environment Status</h3>
            <div className="mt-4 grid gap-4 md:grid-cols-2">
              <div className="portal-panel p-4">
                <div className="flex items-center justify-between">
                  <span className="text-[13px] text-white">Sandbox API</span>
                  <StatusPill value="ACTIVE" />
                </div>
              </div>
              <div className="portal-panel p-4">
                <div className="flex items-center justify-between">
                  <span className="text-[13px] text-white">Webhook Delivery</span>
                  <StatusPill value="DEGRADED" />
                </div>
              </div>
            </div>
          </section>
          <section className="portal-card-dense col-span-12 xl:col-span-4">
            <div className="portal-label">Quick Links</div>
            <div className="mt-4 space-y-3">
              {['Create API key', 'Inspect request log', 'Retry failed webhook'].map((item) => (
                <div key={item} className="portal-panel p-4 text-[13px] text-white">{item}</div>
              ))}
            </div>
          </section>
        </div>
      </div>
    </PortalShell>
  );
}
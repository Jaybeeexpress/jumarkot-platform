import { EnterpriseShell } from '@/components/layout/EnterpriseShell';
import { StatusBadge } from '@/components/ui/StatusBadge';
import { OPS_CONSOLE_NAV_SECTIONS } from '@/lib/nav-config';

export default async function AlertDetailPage({ params }: { params: Promise<{ alertId: string }> }) {
  const { alertId } = await params;

  const rightPanel = (
    <div className="section-stack">
      <section className="enterprise-card">
        <div className="enterprise-label">Entity Summary</div>
        <div className="mt-3 space-y-2 text-[13px] text-secondary">
          <div className="flex justify-between"><span>Entity</span><span className="text-primary">user_982</span></div>
          <div className="flex justify-between"><span>Linked Alerts</span><span className="text-primary">6</span></div>
          <div className="flex justify-between"><span>Last Decision</span><span className="text-primary">REVIEW</span></div>
        </div>
      </section>
      <section className="enterprise-card">
        <div className="enterprise-label">Device Summary</div>
        <div className="mt-3 space-y-2 text-[13px] text-secondary">
          <div className="flex justify-between"><span>Device</span><span className="text-primary">device_web_19</span></div>
          <div className="flex justify-between"><span>Geo</span><span className="text-primary">DE / Berlin</span></div>
          <div className="flex justify-between"><span>Velocity</span><span className="text-primary">4 logins / 6m</span></div>
        </div>
      </section>
      <section className="enterprise-card sticky top-6">
        <div className="enterprise-label">Actions</div>
        <div className="mt-4 space-y-2">
          <button className="enterprise-button enterprise-button-primary w-full">Block</button>
          <button className="enterprise-button enterprise-button-secondary w-full">Review</button>
          <button className="enterprise-button enterprise-button-secondary w-full">Dismiss</button>
        </div>
      </section>
    </div>
  );

  return (
    <EnterpriseShell
      title="Alert Detail"
      breadcrumb={['Ops Console', 'Alerts', alertId]}
      navSections={OPS_CONSOLE_NAV_SECTIONS}
      searchPlaceholder="Search alerts..."
      environmentLabel="Production"
      rightPanel={rightPanel}
    >
      <div className="section-stack">
        <section className="enterprise-card-dense flex h-[120px] items-center justify-between">
          <div>
            <div className="enterprise-label">Alert Summary</div>
            <h3 className="mt-2">{alertId}</h3>
            <div className="mt-2 text-[13px] text-secondary">Triggered 2026-03-31 09:11 UTC by anomalous login sequence</div>
          </div>
          <div className="flex items-center gap-3">
            <StatusBadge value="HIGH" />
            <StatusBadge value="OPEN" />
          </div>
        </section>
        <div className="app-grid">
          <section className="enterprise-card-dense row-span-8">
            <div className="enterprise-label">Decision Breakdown</div>
            <div className="mt-4 grid gap-4 md:grid-cols-3">
              <div className="enterprise-panel p-4"><div className="text-muted">Risk Score</div><div className="mt-2 text-[28px] font-semibold">91</div></div>
              <div className="enterprise-panel p-4"><div className="text-muted">Decision</div><div className="mt-2"><StatusBadge value="REVIEW" /></div></div>
              <div className="enterprise-panel p-4"><div className="text-muted">Timestamp</div><div className="mt-2 text-[13px] text-primary">2026-03-31 09:11 UTC</div></div>
            </div>
            <div className="mt-4 space-y-2">
              {['Impossible travel anomaly', 'High device churn', 'Credential mismatch'].map((item) => (
                <div key={item} className="enterprise-panel flex h-12 items-center px-4 text-[13px] text-primary">{item}</div>
              ))}
            </div>
          </section>
          <section className="enterprise-card-dense row-span-8">
            <div className="enterprise-label">Timeline</div>
            <div className="mt-4 max-h-[420px] space-y-3 overflow-auto pr-2">
              {[
                ['09:11', 'Alert created from decision pipeline'],
                ['09:08', 'New device observed for entity'],
                ['09:06', 'Login velocity threshold exceeded'],
                ['09:03', 'Credential challenge failed'],
              ].map(([time, text]) => (
                <div key={time} className="enterprise-panel min-h-[96px] p-4">
                  <div className="text-[12px] text-muted">{time}</div>
                  <div className="mt-2 text-[13px] text-primary">{text}</div>
                </div>
              ))}
            </div>
          </section>
        </div>
      </div>
    </EnterpriseShell>
  );
}
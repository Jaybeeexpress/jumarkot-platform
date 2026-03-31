import { EnterpriseShell } from '@/components/layout/EnterpriseShell';
import { OPS_CONSOLE_NAV_SECTIONS } from '@/lib/nav-config';

const adminStats = [
  { label: 'Active Tenants', value: '12', detail: '2 onboarding' },
  { label: 'System Health', value: '99.8%', detail: 'All services normal' },
  { label: 'Database', value: '68%', detail: 'Usage: 340 GB / 500 GB' },
];

const configSections = [
  {
    title: 'Platform Configuration',
    items: ['Environment policies', 'Analyst permissions', 'Tenant provisioning controls'],
  },
  {
    title: 'Audit Settings',
    items: ['Case retention policy', 'Notification routing', 'Webhook governance'],
  },
  {
    title: 'Security & Compliance',
    items: ['API rate limiting', '2FA enforcement', 'Encryption key rotation'],
  },
];

export default function AdminPage() {
  return (
    <EnterpriseShell
      title="Admin"
      breadcrumb={['Ops Console', 'Admin']}
      navSections={OPS_CONSOLE_NAV_SECTIONS}
      searchPlaceholder="Search configuration..."
      environmentLabel="Production"
    >
      <div className="section-stack">
        {/* Admin Stats */}
        <div className="app-grid">
          {adminStats.map((stat) => (
            <section key={stat.label} className="enterprise-card-dense">
              <div className="enterprise-label">{stat.label}</div>
              <div className="mt-4">
                <div className="text-[28px] font-semibold text-primary">{stat.value}</div>
                <div className="mt-2 text-[13px] text-secondary">{stat.detail}</div>
              </div>
            </section>
          ))}
        </div>

        {/* Configuration Sections */}
        <div className="app-grid">
          {configSections.map((section) => (
            <section key={section.title} className="enterprise-card-dense">
              <div className="enterprise-label">{section.title}</div>
              <div className="mt-4 space-y-3">
                {section.items.map((item) => (
                  <div
                    key={item}
                    className="enterprise-panel flex cursor-pointer items-center justify-between px-4 py-3 text-[13px] hover:bg-opacity-60 transition-colors"
                  >
                    <span className="text-primary">{item}</span>
                    <span className="text-muted">→</span>
                  </div>
                ))}
              </div>
            </section>
          ))}
        </div>

        {/* Recent Activity */}
        <section className="enterprise-card-dense">
          <div className="enterprise-label">Recent Configuration Changes</div>
          <div className="mt-4 space-y-3">
            {[
              { time: '2 hours ago', action: 'Updated tenant rate limits', user: 'S. Cole' },
              { time: '1 day ago', action: 'Rotated API credentials', user: 'Admin System' },
              { time: '2 days ago', action: 'Enabled webhook retry policy', user: 'L. Chen' },
            ].map((change) => (
              <div key={change.action} className="enterprise-panel p-4">
                <div className="flex items-center justify-between text-[13px]">
                  <div>
                    <div className="text-primary">{change.action}</div>
                    <div className="mt-1 text-muted">{change.user}</div>
                  </div>
                  <div className="text-muted">{change.time}</div>
                </div>
              </div>
            ))}
          </div>
        </section>
      </div>
    </EnterpriseShell>
  );
}
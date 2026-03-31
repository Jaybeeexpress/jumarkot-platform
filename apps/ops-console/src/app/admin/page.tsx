import { EnterpriseShell } from '@/components/layout/EnterpriseShell';
import { OPS_CONSOLE_NAV_SECTIONS } from '@/lib/nav-config';

export default function AdminPage() {
  return (
    <EnterpriseShell
      title="Admin"
      breadcrumb={['Ops Console', 'Admin']}
      navSections={OPS_CONSOLE_NAV_SECTIONS}
      searchPlaceholder="Search configuration..."
      environmentLabel="Production"
    >
      <div className="app-grid">
        <section className="enterprise-card-dense row-span-6">
          <div className="enterprise-label">Platform Configuration</div>
          <div className="mt-4 space-y-3 text-[13px] text-secondary">
            <div className="enterprise-panel p-4">Environment policies</div>
            <div className="enterprise-panel p-4">Analyst permissions</div>
            <div className="enterprise-panel p-4">Tenant provisioning controls</div>
          </div>
        </section>
        <section className="enterprise-card-dense row-span-6">
          <div className="enterprise-label">Audit Settings</div>
          <div className="mt-4 space-y-3 text-[13px] text-secondary">
            <div className="enterprise-panel p-4">Case retention policy</div>
            <div className="enterprise-panel p-4">Notification routing</div>
            <div className="enterprise-panel p-4">Webhook governance</div>
          </div>
        </section>
      </div>
    </EnterpriseShell>
  );
}
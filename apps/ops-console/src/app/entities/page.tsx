import { EnterpriseShell } from '@/components/layout/EnterpriseShell';
import { StatusBadge } from '@/components/ui/StatusBadge';
import { OPS_CONSOLE_NAV_SECTIONS } from '@/lib/nav-config';

export default function EntitiesPage() {
  return (
    <EnterpriseShell
      title="Entity Detail"
      breadcrumb={['Ops Console', 'Entities', 'user_982']}
      navSections={OPS_CONSOLE_NAV_SECTIONS}
      searchPlaceholder="Search entities..."
      environmentLabel="Production"
    >
      <div className="section-stack">
        <section className="enterprise-card-dense">
          <div className="flex flex-wrap items-center justify-between gap-4">
            <div>
              <div className="enterprise-label">Header Summary</div>
              <h3 className="mt-1">user_982</h3>
              <div className="mt-2 text-[13px] text-secondary">Customer account with elevated credential abuse pattern</div>
            </div>
            <div className="flex items-center gap-3">
              <StatusBadge value="HIGH" />
              <StatusBadge value="OPEN" />
            </div>
          </div>
        </section>
        <div className="app-grid">
          <section className="enterprise-card-dense row-span-8">
            <div className="enterprise-label">Activity Timeline</div>
            <div className="mt-4 space-y-3">
              {['Credential rotation failed', 'New device linked to entity', 'High-risk transaction denied'].map((item, index) => (
                <div key={index} className="enterprise-panel p-4 text-[13px] text-primary">{item}</div>
              ))}
            </div>
          </section>
          <section className="enterprise-card-dense row-span-4">
            <div className="enterprise-label">Linked Devices / Accounts</div>
            <div className="mt-4 space-y-2">
              {['device_web_19', 'device_ios_07', 'account_backup_2'].map((item) => (
                <div key={item} className="enterprise-panel flex items-center justify-between px-4 py-3 text-[13px]">
                  <span className="text-primary">{item}</span>
                  <StatusBadge value="ACTIVE" />
                </div>
              ))}
            </div>
            <div className="mt-4 border-t border-light pt-4">
              <div className="enterprise-label">Linked Alerts</div>
              <div className="mt-2 text-[13px] text-secondary">ALT-90314, ALT-90288, ALT-89984</div>
            </div>
          </section>
        </div>
      </div>
    </EnterpriseShell>
  );
}
import { EnterpriseShell } from '@/components/layout/EnterpriseShell';
import { StatusBadge } from '@/components/ui/StatusBadge';
import { OPS_CONSOLE_NAV_SECTIONS } from '@/lib/nav-config';

export default function TenantsPage() {
  const tenants = [
    ['TEN-001', 'Acme Corp', 'Enterprise', 'acme-corp', '2026-02-15', 'ACTIVE'],
    ['TEN-002', 'TechStart Inc', 'Growth', 'techstart-inc', '2026-03-10', 'ACTIVE'],
    ['TEN-003', 'Global Solutions', 'Starter', 'global-solutions', '2026-03-25', 'ACTIVE'],
    ['TEN-004', 'Alpha Labs', 'Enterprise', 'alpha-labs', '2026-03-28', 'ACTIVE'],
    ['TEN-005', 'Beta Partners', 'Growth', 'beta-partners', '2026-03-29', 'DEGRADED'],
  ];

  return (
    <EnterpriseShell
      title="Tenants"
      breadcrumb={['Ops Console', 'Tenants']}
      navSections={OPS_CONSOLE_NAV_SECTIONS}
      searchPlaceholder="Search tenants..."
      environmentLabel="Production"
    >
      <div className="section-stack">
        <section className="enterprise-card-dense">
          <div className="mb-4 flex items-center justify-between">
            <div>
              <div className="enterprise-label">Tenant Management</div>
              <div className="mt-1 text-[13px] text-secondary">12 total tenants, 11 active</div>
            </div>
            <button className="enterprise-button enterprise-button-primary">
              Provision Tenant
            </button>
          </div>
          <input
            type="text"
            placeholder="Search by name or identifier..."
            className="enterprise-input w-full"
          />
        </section>

        <section className="enterprise-card-dense overflow-hidden">
          <table className="enterprise-table w-full">
            <thead>
              <tr className="border-b border-light">
                <th className="px-4 py-3 text-left text-[11px] font-semibold tracking-[0.04em] text-secondary uppercase">Tenant ID</th>
                <th className="px-4 py-3 text-left text-[11px] font-semibold tracking-[0.04em] text-secondary uppercase">Name</th>
                <th className="px-4 py-3 text-left text-[11px] font-semibold tracking-[0.04em] text-secondary uppercase">Plan</th>
                <th className="px-4 py-3 text-left text-[11px] font-semibold tracking-[0.04em] text-secondary uppercase">Slug</th>
                <th className="px-4 py-3 text-left text-[11px] font-semibold tracking-[0.04em] text-secondary uppercase">Provisioned</th>
                <th className="px-4 py-3 text-left text-[11px] font-semibold tracking-[0.04em] text-secondary uppercase">Status</th>
              </tr>
            </thead>
            <tbody>
              {tenants.map((row, idx) => (
                <tr key={idx} className="border-b border-light last:border-b-0 hover:bg-card/50 transition-colors">
                  <td className="px-4 py-3 text-[13px] text-primary font-medium">{row[0]}</td>
                  <td className="px-4 py-3 text-[13px] text-primary">{row[1]}</td>
                  <td className="px-4 py-3 text-[13px] text-secondary">
                    <span className="inline-block px-2.5 py-1 rounded-md bg-indigo-500/12 text-indigo-400 border border-indigo-500/30 text-[11px] font-semibold">
                      {row[2]}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-[13px] text-secondary">{row[3]}</td>
                  <td className="px-4 py-3 text-[13px] text-secondary">{row[4]}</td>
                  <td className="px-4 py-3 text-[13px]">
                    <StatusBadge value={row[5]} />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>

        <section className="enterprise-card-dense">
          <div className="enterprise-label mb-4">Tenant Details</div>
          <div className="space-y-2 text-[13px] text-secondary">
            <div className="enterprise-panel flex items-center justify-between p-4">
              <span>Total Tenants</span>
              <span className="text-primary font-medium">12</span>
            </div>
            <div className="enterprise-panel flex items-center justify-between p-4">
              <span>Active Status</span>
              <span className="text-primary font-medium">11 / 12</span>
            </div>
            <div className="enterprise-panel flex items-center justify-between p-4">
              <span>Enterprise Plans</span>
              <span className="text-primary font-medium">4</span>
            </div>
          </div>
        </section>
      </div>
    </EnterpriseShell>
  );
}

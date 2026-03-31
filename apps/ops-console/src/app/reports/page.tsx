import { AppShell } from '@/components/layout/AppShell';

export default function ReportsPage() {
  return (
    <AppShell title="Reports" breadcrumb={['Ops Console', 'Reports']}>
      <div className="app-grid">
        <section className="enterprise-card-dense row-span-12">
          <div className="enterprise-label">Reports</div>
          <h3 className="mt-2">Operational Reporting</h3>
          <div className="mt-4 app-grid">
            <div className="enterprise-panel row-span-6 p-4 text-[13px] text-primary">Weekly analyst throughput</div>
            <div className="enterprise-panel row-span-6 p-4 text-[13px] text-primary">Decision distribution</div>
          </div>
        </section>
      </div>
    </AppShell>
  );
}
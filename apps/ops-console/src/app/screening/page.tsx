import { AppShell } from '@/components/layout/AppShell';

export default function ScreeningPage() {
  return (
    <AppShell title="Screening" breadcrumb={['Ops Console', 'Screening']}>
      <div className="app-grid">
        <section className="enterprise-card-dense row-span-12">
          <div className="enterprise-label">Screening Workspace</div>
          <h3 className="mt-2">Watchlist Screening Queue</h3>
          <div className="mt-4 grid gap-4 md:grid-cols-3">
            {['Pending checks', 'False positives', 'Escalated hits'].map((item) => (
              <div key={item} className="enterprise-panel p-4 text-[13px] text-primary">{item}</div>
            ))}
          </div>
        </section>
      </div>
    </AppShell>
  );
}
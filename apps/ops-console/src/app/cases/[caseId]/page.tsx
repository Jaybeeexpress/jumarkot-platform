import { EnterpriseShell } from '@/components/layout/EnterpriseShell';
import { StatusBadge } from '@/components/ui/StatusBadge';
import { OPS_CONSOLE_NAV_SECTIONS } from '@/lib/nav-config';

const tabs = ['Overview', 'Alerts', 'Timeline', 'Evidence', 'Notes'];

export default async function CaseDetailPage({ params }: { params: Promise<{ caseId: string }> }) {
  const { caseId } = await params;

  const rightPanel = (
    <div className="section-stack">
      <section className="enterprise-card">
        <div className="enterprise-label">Case Meta</div>
        <div className="mt-3 space-y-2 text-[13px] text-secondary">
          <div className="flex justify-between"><span>Owner</span><span className="text-primary">S. Cole</span></div>
          <div className="flex justify-between"><span>Priority</span><StatusBadge value="P1" /></div>
          <div className="flex justify-between"><span>Status</span><StatusBadge value="IN_PROGRESS" /></div>
        </div>
      </section>
      <section className="enterprise-card">
        <div className="enterprise-label">Linked Entity</div>
        <div className="mt-3 text-[13px] text-primary">user_982</div>
      </section>
    </div>
  );

  return (
    <EnterpriseShell
      title="Case Detail"
      breadcrumb={['Ops Console', 'Cases', caseId]}
      navSections={OPS_CONSOLE_NAV_SECTIONS}
      searchPlaceholder="Search case details..."
      environmentLabel="Production"
      rightPanel={rightPanel}
    >
      <div className="section-stack">
        <section className="enterprise-card-dense sticky top-6 z-10 flex h-20 items-center justify-between">
          <div>
            <div className="enterprise-label">Case Header</div>
            <h3 className="mt-1">{caseId}</h3>
          </div>
          <div className="flex items-center gap-3">
            <StatusBadge value="P1" />
            <StatusBadge value="IN_PROGRESS" />
          </div>
        </section>
        <div className="flex gap-2 border-b border-light pb-2">
          {tabs.map((tab) => (
            <button key={tab} className={`enterprise-button ${tab === 'Overview' ? 'enterprise-button-primary' : 'enterprise-button-secondary'}`}>{tab}</button>
          ))}
        </div>
        <div className="app-grid">
          <section className="enterprise-card-dense row-span-8">
            <div className="enterprise-label">Timeline</div>
            <div className="mt-4 space-y-3">
              {[
                ['09:12', 'Case assigned to analyst'],
                ['09:05', 'Escalated from alert ALT-90314'],
                ['08:59', 'Entity correlated to prior fraud pattern'],
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
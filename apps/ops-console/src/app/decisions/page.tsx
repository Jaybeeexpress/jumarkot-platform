import { AppShell } from '@/components/layout/AppShell';
import { StatusBadge } from '@/components/ui/StatusBadge';
const matchedRules = [
  'Impossible travel check',
  'Velocity threshold escalation',
  'Credential mismatch pattern',
  'Device novelty weighting',
];

const signals = [
  'Three failed logins from new IP in 6 minutes',
  'Geo distance exceeded expected movement threshold',
  'Device fingerprint unseen for 90 days',
  'Prior alert correlation found for same entity',
];

export default function DecisionsPage() {
  return (
    <AppShell title="Decision Explainability" breadcrumb={['Ops Console', 'Decisions']}>
      <div className="app-grid">
        <section className="enterprise-card-dense row-span-6">
          <div className="enterprise-label">Decision Explainability Panel</div>
          <div className="mt-6 flex flex-col items-center justify-center rounded-md border border-light bg-panel px-6 py-8 text-center">
            <div className="text-[56px] font-semibold leading-none text-primary">91</div>
            <div className="mt-3"><StatusBadge value="REVIEW" /></div>
            <div className="mt-2 text-[13px] text-secondary">Generated 2026-03-31 09:11 UTC</div>
          </div>
          <div className="mt-6">
            <div className="enterprise-label">Matched Rules</div>
            <div className="mt-3 space-y-2">
              {matchedRules.map((rule) => (
                <div key={rule} className="enterprise-panel flex h-12 items-center justify-between px-4">
                  <span className="text-[13px] text-primary">{rule}</span>
                  <span className="text-[12px] text-secondary">48 pts</span>
                </div>
              ))}
            </div>
          </div>
        </section>
        <section className="enterprise-card-dense row-span-6">
          <div className="enterprise-label">Signals</div>
          <div className="mt-3 space-y-2">
            {signals.map((signal) => (
              <div key={signal} className="enterprise-panel p-4 text-[13px] text-primary">{signal}</div>
            ))}
          </div>
        </section>
      </div>
    </AppShell>
  );
}

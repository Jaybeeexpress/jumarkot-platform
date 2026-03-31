import { AppShell } from '@/components/layout/AppShell';
import { StatusBadge } from '@/components/ui/StatusBadge';
const rules = [
  ['Credential Mismatch Escalation', 'ACCOUNT', '120', 'REVIEW', '+18', 'ACTIVE'],
  ['Impossible Travel Block', 'LOGIN', '140', 'BLOCK', '+30', 'ACTIVE'],
  ['Document Freshness Check', 'KYC', '90', 'REVIEW', '+12', 'DRAFT'],
  ['Velocity Suppression Rule', 'PAYMENT', '110', 'DECLINE', '+24', 'INACTIVE'],
];

export default function RulesPage() {
  return (
    <AppShell title="Rules" breadcrumb={['Ops Console', 'Rules']}>
      <div className="section-stack">
        <section className="enterprise-card-dense">
          <div className="app-grid">
            <div className="row-span-6">
              <label className="enterprise-label">Search</label>
              <input className="enterprise-input mt-2" placeholder="Rule name or category" />
            </div>
            <div className="row-span-3">
              <label className="enterprise-label">Environment</label>
              <select aria-label="Environment" title="Environment" className="enterprise-input mt-2"><option>Production</option></select>
            </div>
            <div className="row-span-3">
              <label className="enterprise-label">Status</label>
              <select aria-label="Status" title="Status" className="enterprise-input mt-2"><option>All</option></select>
            </div>
          </div>
        </section>
        <section className="enterprise-card-dense">
          <table className="enterprise-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Category</th>
                <th>Priority</th>
                <th>Action</th>
                <th>Score Adj.</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {rules.map((rule) => (
                <tr key={rule[0]}>
                  <td>{rule[0]}</td>
                  <td>{rule[1]}</td>
                  <td>{rule[2]}</td>
                  <td>{rule[3]}</td>
                  <td>{rule[4]}</td>
                  <td><StatusBadge value={rule[5]} /></td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>
      </div>
    </AppShell>
  );
}

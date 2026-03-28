import Link from 'next/link';

export default function DashboardPage() {
  return (
    <div className="flex min-h-screen flex-col">
      {/* Top Nav */}
      <header className="border-b bg-white px-6 py-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <span className="text-lg font-bold tracking-tight text-slate-900">Jumarkot</span>
            <span className="rounded-full bg-slate-100 px-2 py-0.5 text-xs font-medium text-slate-500">
              Ops Console
            </span>
          </div>
          <nav className="flex items-center gap-6 text-sm font-medium text-slate-600">
            <Link href="/decisions" className="hover:text-slate-900">Decisions</Link>
            <Link href="/rules" className="hover:text-slate-900">Rules</Link>
            <Link href="/tenants" className="hover:text-slate-900">Tenants</Link>
            <Link href="/alerts" className="hover:text-slate-900">Alerts</Link>
          </nav>
        </div>
      </header>

      {/* Main */}
      <main className="flex-1 bg-slate-50 p-8">
        <h1 className="mb-6 text-2xl font-semibold text-slate-900">Overview</h1>

        {/* KPI cards — placeholder */}
        <div className="grid grid-cols-4 gap-4">
          {[
            { label: 'Decisions (24h)', value: '—' },
            { label: 'Approve Rate', value: '—' },
            { label: 'Active Rules', value: '—' },
            { label: 'Open Alerts', value: '—' },
          ].map((kpi) => (
            <div key={kpi.label} className="rounded-lg border bg-white p-5 shadow-sm">
              <p className="text-sm text-slate-500">{kpi.label}</p>
              <p className="mt-1 text-3xl font-bold text-slate-900">{kpi.value}</p>
            </div>
          ))}
        </div>
      </main>
    </div>
  );
}

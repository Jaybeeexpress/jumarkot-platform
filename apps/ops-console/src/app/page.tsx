export default function OpsConsolePage() {
  return (
    <main className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="text-center">
          <h1 className="text-4xl font-bold text-gray-900 sm:text-5xl">
            Jumarkot Ops Console
          </h1>
          <p className="mt-4 text-xl text-gray-500">
            Risk, Fraud &amp; Compliance Operations Centre
          </p>
          <p className="mt-2 text-base text-gray-400">
            Manage alerts, cases, rules, and tenants across the platform.
          </p>
          <div className="mt-10 flex justify-center gap-4">
            <a
              href="/dashboard"
              className="bg-indigo-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-indigo-700 transition"
            >
              Operations Dashboard
            </a>
            <a
              href="/alerts"
              className="bg-white border border-gray-300 text-gray-700 px-6 py-3 rounded-lg font-semibold hover:bg-gray-50 transition"
            >
              View Alerts
            </a>
          </div>
        </div>

        <div className="mt-20 grid grid-cols-1 gap-8 sm:grid-cols-4">
          {[
            { title: 'Alerts', desc: 'Review and action risk alerts across all tenants.', icon: '🚨', href: '/alerts' },
            { title: 'Cases', desc: 'Manage investigation cases with full audit trail.', icon: '📋', href: '/cases' },
            { title: 'Rules', desc: 'Create, version, and manage risk evaluation rules.', icon: '⚙️', href: '/rules' },
            { title: 'Tenants', desc: 'Manage tenant accounts, plans, and environments.', icon: '🏢', href: '/tenants' },
          ].map((item) => (
            <a
              key={item.title}
              href={item.href}
              className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 hover:border-indigo-400 hover:shadow-md transition"
            >
              <div className="text-3xl mb-3">{item.icon}</div>
              <h3 className="text-lg font-semibold text-gray-900">{item.title}</h3>
              <p className="mt-2 text-sm text-gray-500">{item.desc}</p>
            </a>
          ))}
        </div>
      </div>
    </main>
  )
}

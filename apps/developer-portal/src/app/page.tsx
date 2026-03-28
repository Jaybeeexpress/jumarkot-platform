export default function DeveloperPortalHome() {
  return (
    <main className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="text-center">
          <h1 className="text-4xl font-bold text-gray-900 sm:text-5xl">
            Jumarkot Developer Portal
          </h1>
          <p className="mt-4 text-xl text-gray-500">
            API-first Risk, Fraud &amp; Compliance Intelligence Platform
          </p>
          <p className="mt-2 text-base text-gray-400">
            Integrate Jumarkot into your product with secure APIs, SDKs, and webhooks.
          </p>
          <div className="mt-10 flex justify-center gap-4">
            <a
              href="/dashboard"
              className="bg-indigo-600 text-white px-6 py-3 rounded-lg font-semibold hover:bg-indigo-700 transition"
            >
              Go to Dashboard
            </a>
            <a
              href="/api-keys"
              className="bg-white border border-gray-300 text-gray-700 px-6 py-3 rounded-lg font-semibold hover:bg-gray-50 transition"
            >
              Manage API Keys
            </a>
          </div>
        </div>

        <div className="mt-20 grid grid-cols-1 gap-8 sm:grid-cols-3">
          {[
            {
              title: 'Real-time Decisions',
              desc: 'Submit events and get instant risk scores, outcomes, and recommended actions.',
              icon: '⚡',
            },
            {
              title: 'Event Ingestion',
              desc: 'Push transactions, logins, and onboarding events with full idempotency support.',
              icon: '📥',
            },
            {
              title: 'API Keys & Webhooks',
              desc: 'Manage scoped API keys and configure webhooks for real-time notifications.',
              icon: '🔑',
            },
          ].map((feature) => (
            <div
              key={feature.title}
              className="bg-white rounded-xl shadow-sm border border-gray-200 p-6"
            >
              <div className="text-3xl mb-3">{feature.icon}</div>
              <h3 className="text-lg font-semibold text-gray-900">{feature.title}</h3>
              <p className="mt-2 text-sm text-gray-500">{feature.desc}</p>
            </div>
          ))}
        </div>
      </div>
    </main>
  )
}

import Link from 'next/link';

export default function HomePage() {
  return (
    <div className="flex min-h-screen flex-col">
      <header className="border-b bg-white px-6 py-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <span className="text-lg font-bold tracking-tight text-slate-900">Jumarkot</span>
            <span className="rounded-full bg-indigo-100 px-2 py-0.5 text-xs font-medium text-indigo-700">
              Developer Portal
            </span>
          </div>
          <nav className="flex items-center gap-6 text-sm font-medium text-slate-600">
            <Link href="/docs" className="hover:text-slate-900">API Reference</Link>
            <Link href="/keys" className="hover:text-slate-900">API Keys</Link>
            <Link href="/sandbox" className="hover:text-slate-900">Sandbox</Link>
            <Link href="/logs" className="hover:text-slate-900">Logs</Link>
          </nav>
        </div>
      </header>

      <main className="flex-1 bg-slate-50 p-8">
        <div className="mx-auto max-w-3xl">
          <h1 className="mb-4 text-3xl font-bold text-slate-900">
            Build with Jumarkot
          </h1>
          <p className="mb-8 text-lg text-slate-600">
            Real-time risk decisions, fraud signals, and compliance intelligence — all via a single API.
          </p>

          <div className="grid grid-cols-3 gap-4">
            {[
              { title: 'Quickstart', href: '/docs/quickstart', desc: 'Get your first decision in under 5 minutes' },
              { title: 'API Reference', href: '/docs/api', desc: 'Full endpoint documentation' },
              { title: 'Sandbox', href: '/sandbox', desc: 'Test decisioning without real data' },
            ].map((item) => (
              <Link
                key={item.title}
                href={item.href}
                className="rounded-lg border bg-white p-5 shadow-sm transition hover:shadow-md"
              >
                <h2 className="mb-1 font-semibold text-slate-900">{item.title}</h2>
                <p className="text-sm text-slate-500">{item.desc}</p>
              </Link>
            ))}
          </div>
        </div>
      </main>
    </div>
  );
}

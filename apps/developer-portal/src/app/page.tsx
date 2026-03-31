import Link from 'next/link';
import {
  ArrowRight,
  BadgeDollarSign,
  GitBranch,
  Github,
  Gitlab,
  LockKeyhole,
  Settings2,
  Shield,
  Sparkles,
} from 'lucide-react';

const navItems = ['Product', 'Solutions', 'Pricing', 'Developers', 'Resources', 'Sign In'];

const providers = [
  { label: 'Continue with GitHub', icon: Github },
  { label: 'Continue with GitLab', icon: Gitlab },
  { label: 'Continue with Bitbucket', icon: GitBranch },
];

const introCards = [
  {
    label: 'Live Intelligence',
    title: 'Operational routing for fraud, compliance, and risk review',
    text: 'Unify case queues, risk policies, and developer controls without fragmenting analyst workflows.',
  },
  {
    label: 'Built For Teams',
    title: 'Enterprise trust with product-led clarity',
    text: 'Present investigation context, signal freshness, and system state in a format teams can act on quickly.',
  },
  {
    label: 'Developer Ready',
    title: 'Structured controls for modern platform access',
    text: 'Ship ingestion, screening, and webhook orchestration from one secure interface with minimal operational drag.',
  },
];

const footerLinks = ['Privacy Policy', 'Terms of Service', 'Contact'];

export default function HomePage() {
  return (
    <div className="landing-page">
      <header className="landing-nav">
        <div className="landing-container flex h-full items-center justify-between gap-6">
          <Link href="/" className="text-[24px] font-bold tracking-[-0.02em] text-landing-primary">
            Jumarkot
          </Link>

          <nav className="hidden items-center gap-7 lg:flex">
            {navItems.map((item) => (
              <a key={item} href={item === 'Sign In' ? '#access' : '#'} className="landing-nav-link">
                {item}
              </a>
            ))}
          </nav>

          <div className="flex items-center gap-3">
            <button type="button" className="landing-settings-button" aria-label="Open settings">
              <Settings2 className="h-4.5 w-4.5" />
            </button>
            <a href="#access" className="landing-secondary-button hidden h-10 px-4 md:inline-flex">
              Enter Platform
            </a>
          </div>
        </div>
      </header>

      <main>
        <section className="landing-container grid min-h-[calc(100vh-72px)] gap-8 py-8 xl:grid-cols-[0.38fr_0.62fr] xl:py-0">
          <div id="access" className="flex py-4 xl:py-8">
            <div className="landing-surface flex min-h-full w-full flex-col bg-[#1A2238] px-10 py-12">
              <div className="flex flex-1 flex-col justify-between gap-10">
                <div className="space-y-8">
                  <div className="flex justify-center">
                    <div className="flex h-16 w-16 items-center justify-center rounded-[16px] border border-[rgba(255,45,111,0.22)] bg-[rgba(255,45,111,0.1)] shadow-[0_14px_40px_rgba(255,45,111,0.14)]">
                      <Shield className="h-8 w-8 text-[var(--landing-accent)]" />
                    </div>
                  </div>

                  <div className="space-y-3 text-center">
                    <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">
                      Trusted Access For Risk Operations
                    </div>
                    <h1 className="text-[36px] font-bold leading-[1.15] text-landing-primary">
                      Sign in to your account
                    </h1>
                    <p className="mx-auto max-w-[360px] text-[16px] leading-7 text-landing-secondary">
                      Access analyst operations, policy controls, and developer tooling from one structured platform surface.
                    </p>
                  </div>

                  <div className="space-y-4">
                    {providers.map((provider) => {
                      const Icon = provider.icon;

                      return (
                        <button key={provider.label} type="button" className="landing-auth-button">
                          <span className="flex items-center gap-3">
                            <Icon className="h-5 w-5 text-landing-secondary" />
                            <span className="text-[15px] font-semibold text-landing-primary">{provider.label}</span>
                          </span>
                          <ArrowRight className="h-4 w-4 text-landing-muted" />
                        </button>
                      );
                    })}
                  </div>

                  <div className="flex items-center justify-center gap-2 text-center text-[15px] text-landing-muted">
                    <BadgeDollarSign className="h-4 w-4 text-[var(--landing-tech)]" />
                    <span>Developer access includes sandbox onboarding, API tooling, and environment visibility.</span>
                  </div>
                </div>

                <div className="flex flex-col gap-4 border-t border-[var(--landing-border)] pt-6 sm:flex-row sm:items-center sm:justify-between">
                  <div className="flex flex-wrap items-center gap-4 text-[14px]">
                    {footerLinks.map((item) => (
                      <a key={item} href="#" className="landing-footer-link">
                        {item}
                      </a>
                    ))}
                  </div>
                  <div className="flex items-center gap-3 text-[var(--landing-text-muted)]">
                    <Github className="h-4 w-4" />
                    <Gitlab className="h-4 w-4" />
                    <GitBranch className="h-4 w-4" />
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="flex items-center py-12 xl:py-16">
            <div className="w-full space-y-8 px-2 xl:px-4">
              <div className="flex items-center gap-2">
                {Array.from({ length: 5 }).map((_, index) => (
                  <span
                    key={index}
                    className={`h-[6px] rounded-full ${index === 2 ? 'w-12 bg-[var(--landing-accent)]' : 'w-8 bg-[rgba(248,250,252,0.22)]'}`}
                  />
                ))}
              </div>

              <div className="space-y-5">
                <div className="inline-flex items-center gap-2 rounded-full border border-[var(--landing-border)] bg-[rgba(17,24,43,0.82)] px-3 py-1.5 text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-secondary">
                  <span className="inline-block h-2 w-2 rounded-full bg-[var(--landing-live)]" />
                  Risk, Fraud, and Compliance Intelligence
                </div>
                <h2 className="max-w-[680px] text-[56px] font-bold leading-[1.04] tracking-[-0.03em] text-landing-primary">
                  See every risk signal, route, and action in one place.
                </h2>
                <p className="max-w-[760px] text-[20px] font-normal leading-[1.7] text-landing-secondary">
                  Jumarkot gives analysts and platform teams a shared operating surface for policy execution, signal review, and developer-grade integrations across fraud, compliance, and trust operations.
                </p>
              </div>

              <div className="landing-preview-card overflow-hidden">
                <div className="flex items-center justify-between border-b border-[#1F2937] bg-[#11182B] px-5 py-4">
                  <div className="flex items-center gap-3">
                    <span className="inline-flex h-8 items-center rounded-full border border-[var(--landing-border)] bg-[rgba(56,189,248,0.08)] px-3 text-[12px] font-semibold uppercase tracking-[0.04em] text-[var(--landing-tech)]">
                      Live Console
                    </span>
                    <span className="text-[13px] text-landing-muted">Updated 12 seconds ago</span>
                  </div>
                  <div className="flex items-center gap-2 text-[11px] font-semibold uppercase tracking-[0.04em] text-landing-muted">
                    <Sparkles className="h-3.5 w-3.5 text-[var(--landing-accent)]" />
                    Premium Preview
                  </div>
                </div>

                <div className="grid min-h-[420px] gap-0 lg:grid-cols-[220px_1fr]">
                  <aside className="border-r border-[var(--landing-border)] bg-[#0D1325] p-5">
                    <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">Workspaces</div>
                    <div className="mt-4 space-y-3">
                      {['Analyst Queue', 'Decision Policies', 'Entity Graph', 'Webhook Events'].map((item, index) => (
                        <div
                          key={item}
                          className={`rounded-[10px] border px-4 py-3 text-[13px] font-medium transition-all duration-150 ease-in ${index === 0 ? 'border-[rgba(255,45,111,0.35)] bg-[rgba(255,45,111,0.12)] text-landing-primary' : 'border-[var(--landing-border)] bg-[rgba(17,24,43,0.92)] text-landing-secondary hover:bg-[rgba(42,52,80,0.72)]'}`}
                        >
                          {item}
                        </div>
                      ))}
                    </div>

                    <div className="mt-6 rounded-[12px] border border-[var(--landing-border)] bg-[#11182B] p-4">
                      <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">Signal Freshness</div>
                      <div className="mt-3 flex items-end justify-between">
                        <div>
                          <div className="text-[28px] font-bold text-landing-primary">99.94%</div>
                          <div className="text-[12px] text-landing-muted">ingestion availability</div>
                        </div>
                        <span className="inline-flex items-center gap-1 rounded-full bg-[rgba(34,197,94,0.12)] px-2 py-1 text-[11px] font-semibold text-[#86EFAC]">
                          <span className="h-1.5 w-1.5 rounded-full bg-[var(--landing-live)]" />
                          Live
                        </span>
                      </div>
                    </div>
                  </aside>

                  <div className="bg-[#0F172A] p-6">
                    <div className="grid gap-4 xl:grid-cols-[1.4fr_0.85fr]">
                      <section className="rounded-[12px] border border-[var(--landing-border)] bg-[#11182B] p-5">
                        <div className="flex items-center justify-between">
                          <div>
                            <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">Decision Throughput</div>
                            <div className="mt-1 text-[18px] font-semibold text-landing-primary">Risk Routing Overview</div>
                          </div>
                          <div className="inline-flex items-center gap-2 rounded-full bg-[rgba(56,189,248,0.1)] px-3 py-1 text-[11px] font-semibold uppercase tracking-[0.04em] text-[var(--landing-tech)]">
                            <LockKeyhole className="h-3.5 w-3.5" />
                            Protected
                          </div>
                        </div>

                        <div className="mt-6 flex h-[220px] items-end gap-3">
                          {[48, 66, 58, 78, 72, 84, 96, 91, 110, 106, 118, 130].map((height, index) => (
                            <div key={index} className="flex flex-1 flex-col items-center gap-2">
                              <div
                                className={`w-full rounded-t-[10px] ${index >= 9 ? 'bg-[var(--landing-accent)]' : 'bg-[rgba(56,189,248,0.75)]'}`}
                                style={{ height }}
                              />
                              <span className="text-[11px] font-medium text-landing-faint">W{index + 1}</span>
                            </div>
                          ))}
                        </div>
                      </section>

                      <section className="space-y-4">
                        {[
                          ['High Risk Decisions', '62', '+4.8% vs prior day'],
                          ['Open Investigations', '19', 'Updated 4 mins ago'],
                          ['Webhook Latency', '112ms', 'P95 across production'],
                        ].map(([label, value, note], index) => (
                          <div key={label} className="rounded-[12px] border border-[var(--landing-border)] bg-[#11182B] p-4">
                            <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">{label}</div>
                            <div className="mt-3 flex items-end justify-between gap-3">
                              <div className="text-[30px] font-bold leading-none text-landing-primary">{value}</div>
                              <span className={`inline-flex h-7 items-center rounded-full px-2.5 text-[11px] font-semibold ${index === 0 ? 'bg-[rgba(255,45,111,0.12)] text-[#FDA4AF]' : index === 1 ? 'bg-[rgba(245,158,11,0.14)] text-[#FCD34D]' : 'bg-[rgba(56,189,248,0.14)] text-[#7DD3FC]'}`}>
                                {index === 0 ? 'Review' : index === 1 ? 'Queue' : 'Stable'}
                              </span>
                            </div>
                            <div className="mt-2 text-[13px] text-landing-muted">{note}</div>
                          </div>
                        ))}
                      </section>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>

        <section className="landing-container py-10">
          <div className="grid gap-5 xl:grid-cols-3">
            {introCards.map((card) => (
              <article key={card.title} className="landing-surface bg-[rgba(17,24,43,0.74)] p-6 transition-all duration-150 ease-in hover:bg-[rgba(26,34,56,0.92)]">
                <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">{card.label}</div>
                <h3 className="mt-4 text-[28px] font-semibold leading-[1.2] text-landing-primary">{card.title}</h3>
                <p className="mt-4 text-[16px] leading-8 text-landing-secondary">{card.text}</p>
              </article>
            ))}
          </div>
        </section>

        <section className="landing-container py-6">
          <div className="landing-surface flex flex-col items-start justify-between gap-6 bg-[rgba(17,24,43,0.9)] px-8 py-8 lg:flex-row lg:items-center">
            <div>
              <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">Platform Access</div>
              <h3 className="mt-2 text-[28px] font-semibold text-landing-primary">Start with developer access, scale into live risk operations.</h3>
              <p className="mt-3 max-w-[720px] text-[16px] leading-8 text-landing-secondary">
                Use the same secure entry point for policy rollout, webhook monitoring, and signal inspection across sandbox and production environments.
              </p>
            </div>

            <div className="flex flex-col gap-3 sm:flex-row">
              <a href="#access" className="landing-accent-button h-11 px-5">
                Sign In
              </a>
              <Link href="/api-keys" className="landing-secondary-button h-11 px-5">
                Explore Developer Portal
              </Link>
            </div>
          </div>
        </section>
      </main>

      <footer className="landing-container border-t border-[#1F2937] py-8 text-[14px] text-landing-muted">
        <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">
          <div>Jumarkot is a premium risk, fraud, and compliance intelligence platform for enterprise operations.</div>
          <div className="flex flex-wrap items-center gap-4">
            {footerLinks.map((item) => (
              <a key={item} href="#" className="landing-footer-link">
                {item}
              </a>
            ))}
          </div>
        </div>
      </footer>
    </div>
  );
}
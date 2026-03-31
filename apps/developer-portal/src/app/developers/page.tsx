import Link from 'next/link';
import { BookOpenText, Bot, Code2 } from 'lucide-react';
import { MarketingShell } from '@/components/marketing/MarketingShell';

const navItems = [
  { label: 'Product', href: '/product' },
  { label: 'Solutions', href: '/solutions' },
  { label: 'Pricing', href: '/pricing' },
  { label: 'Developers', href: '/developers' },
  { label: 'Resources', href: '/resources' },
  { label: 'Sign In', href: '/signin' },
];

const links = [
  ['API keys and environment access', 'Create scoped access and rotate credentials cleanly.', 'Open API Keys', '/api-keys', Code2],
  ['Request logs and payload inspection', 'Review inbound and outbound traffic with request and response context.', 'Inspect Logs', '/request-logs', Bot],
  ['Webhook governance', 'Monitor delivery performance and retry operationally sensitive events.', 'Open Webhooks', '/webhooks', BookOpenText],
] as const;

export default function DevelopersPage() {
  return (
    <MarketingShell navItems={navItems} ctaHref="/signin" ctaLabel="Sign In">
      <section className="landing-container py-14">
        <div className="landing-surface bg-[rgba(17,24,43,0.84)] p-8 lg:p-10">
          <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">Developers</div>
          <h1 className="mt-4 text-[48px] font-bold leading-[1.05] text-landing-primary">Integration tooling designed for real implementation work.</h1>
          <p className="mt-5 max-w-[760px] text-[20px] leading-[1.7] text-landing-secondary">Use the same dark, structured surface to onboard environments, inspect payloads, and govern outbound delivery performance.</p>
        </div>
      </section>

      <section className="landing-container py-4">
        <div className="grid gap-5 xl:grid-cols-3">
          {links.map(([title, text, cta, href, Icon]) => (
            <article key={title} className="landing-surface landing-animated-card bg-[rgba(17,24,43,0.82)] p-6 transition-all duration-150 ease-in hover:bg-[rgba(26,34,56,0.96)]">
              <div className="flex h-12 w-12 items-center justify-center rounded-[12px] bg-[rgba(56,189,248,0.12)] text-[var(--landing-tech)]">
                <Icon className="h-5 w-5" />
              </div>
              <h2 className="mt-5 text-[22px] font-semibold text-landing-primary">{title}</h2>
              <p className="mt-3 text-[16px] leading-8 text-landing-secondary">{text}</p>
              <Link href={href} className="mt-6 inline-flex items-center gap-2 text-[15px] font-semibold text-[var(--landing-tech)] hover:text-[#7DD3FC]">
                {cta}
              </Link>
            </article>
          ))}
        </div>
      </section>

      <section className="landing-container py-6">
        <article className="landing-surface landing-animated-card bg-[rgba(17,24,43,0.82)] p-7">
          <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">Implementation Sequence</div>
          <div className="mt-4 grid gap-4 md:grid-cols-4">
            {[
              ['1', 'Generate API key'],
              ['2', 'Route sample signals'],
              ['3', 'Verify webhook delivery'],
              ['4', 'Promote to production'],
            ].map(([step, text]) => (
              <div key={step} className="rounded-[12px] border border-[var(--landing-border)] bg-[rgba(15,23,42,0.92)] px-4 py-4">
                <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">Step {step}</div>
                <div className="mt-2 text-[15px] font-semibold text-landing-primary">{text}</div>
              </div>
            ))}
          </div>
          <Link href="/signin" className="landing-accent-button mt-6 inline-flex h-11 px-5">
            Start Onboarding
          </Link>
        </article>
      </section>
    </MarketingShell>
  );
}

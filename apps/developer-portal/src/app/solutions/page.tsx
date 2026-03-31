import { FileCheck2, Radar, Scale } from 'lucide-react';
import { MarketingShell } from '@/components/marketing/MarketingShell';

const navItems = [
  { label: 'Product', href: '/product' },
  { label: 'Solutions', href: '/solutions' },
  { label: 'Pricing', href: '/pricing' },
  { label: 'Developers', href: '/developers' },
  { label: 'Resources', href: '/resources' },
  { label: 'Sign In', href: '/#access' },
];

const solutions = [
  {
    title: 'Fraud Operations',
    text: 'Centralize queue management, alert explainability, and escalation timing for fraud analysts.',
    icon: Radar,
  },
  {
    title: 'Compliance Review',
    text: 'Organize screening decisions, audit evidence, and review accountability across teams.',
    icon: FileCheck2,
  },
  {
    title: 'Platform Risk',
    text: 'Give operations and engineering a shared decisioning surface for routing, monitoring, and response.',
    icon: Scale,
  },
];

export default function SolutionsPage() {
  return (
    <MarketingShell navItems={navItems} ctaHref="/#access" ctaLabel="Sign In">
      <section className="landing-container py-14">
        <div className="landing-surface bg-[rgba(17,24,43,0.84)] p-8 lg:p-10">
          <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">Solutions</div>
          <h1 className="mt-4 text-[48px] font-bold leading-[1.05] text-landing-primary">Purpose-built for fraud, compliance, and enterprise risk operations.</h1>
          <p className="mt-5 max-w-[760px] text-[20px] leading-[1.7] text-landing-secondary">Jumarkot helps teams move from raw signal overload to deliberate, governed action across the operational stack.</p>
        </div>
      </section>

      <section className="landing-container py-4">
        <div className="grid gap-5 xl:grid-cols-3">
          {solutions.map((solution) => {
            const Icon = solution.icon;
            return (
              <article key={solution.title} className="landing-surface bg-[rgba(17,24,43,0.82)] p-7 transition-all duration-150 ease-in hover:bg-[rgba(26,34,56,0.96)]">
                <div className="flex h-12 w-12 items-center justify-center rounded-[12px] bg-[rgba(56,189,248,0.12)] text-[var(--landing-tech)]">
                  <Icon className="h-5 w-5" />
                </div>
                <h2 className="mt-5 text-[28px] font-semibold text-landing-primary">{solution.title}</h2>
                <p className="mt-4 text-[16px] leading-8 text-landing-secondary">{solution.text}</p>
              </article>
            );
          })}
        </div>
      </section>
    </MarketingShell>
  );
}

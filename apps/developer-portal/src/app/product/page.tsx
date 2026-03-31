import Link from 'next/link';
import { Layers3, Radar, Scale, ShieldCheck, Workflow } from 'lucide-react';
import { MarketingShell } from '@/components/marketing/MarketingShell';

const navItems = [
  { label: 'Product', href: '/product' },
  { label: 'Solutions', href: '/solutions' },
  { label: 'Pricing', href: '/pricing' },
  { label: 'Developers', href: '/developers' },
  { label: 'Resources', href: '/resources' },
  { label: 'Sign In', href: '/signin' },
];

const pillars = [
  {
    icon: Layers3,
    label: 'Command Surface',
    title: 'One interface for decisioning, queues, and operational controls.',
    text: 'Coordinate analysts, administrators, and developers from a single shared operating model with clean access boundaries.',
  },
  {
    icon: Workflow,
    label: 'Orchestration',
    title: 'Route every signal through explainable policies and action layers.',
    text: 'Make routing, review, and escalation logic visible enough for product, ops, and audit stakeholders to trust.',
  },
  {
    icon: ShieldCheck,
    label: 'Governance',
    title: 'Keep audit readiness and operational confidence in the same workflow.',
    text: 'Present policy outcomes, environment state, and reviewer actions together instead of scattering them across tools.',
  },
];

const useCases = [
  { label: 'Fraud Operations', icon: Radar },
  { label: 'Trust & Safety', icon: Scale },
  { label: 'Risk Governance', icon: ShieldCheck },
];

export default function ProductPage() {
  return (
    <MarketingShell navItems={navItems} ctaHref="/signin" ctaLabel="Sign In">
      <section className="landing-container py-14">
        <div className="landing-surface bg-[rgba(17,24,43,0.84)] p-8 lg:p-10">
          <div className="max-w-[880px]">
            <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">Product</div>
            <h1 className="mt-4 text-[48px] font-bold leading-[1.05] text-landing-primary">A risk operations platform designed to make high-stakes decisions legible.</h1>
            <p className="mt-5 max-w-[760px] text-[20px] leading-[1.7] text-landing-secondary">
              Jumarkot unifies signal review, action routing, policy explainability, and developer-grade platform control into one deliberate enterprise interface.
            </p>
          </div>
        </div>
      </section>

      <section className="landing-container py-4">
        <div className="grid gap-5 xl:grid-cols-3">
          {pillars.map((pillar) => {
            const Icon = pillar.icon;
            return (
              <article key={pillar.title} className="landing-surface landing-animated-card bg-[rgba(17,24,43,0.82)] p-6 transition-all duration-150 ease-in hover:bg-[rgba(26,34,56,0.96)]">
                <div className="flex h-12 w-12 items-center justify-center rounded-[12px] bg-[rgba(255,45,111,0.12)] text-[var(--landing-accent)]">
                  <Icon className="h-5 w-5" />
                </div>
                <div className="mt-5 text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">{pillar.label}</div>
                <h2 className="mt-3 text-[28px] font-semibold text-landing-primary">{pillar.title}</h2>
                <p className="mt-4 text-[16px] leading-8 text-landing-secondary">{pillar.text}</p>
              </article>
            );
          })}
        </div>
      </section>

      <section className="landing-container py-10">
        <div className="grid gap-5 xl:grid-cols-[1.25fr_0.75fr]">
          <article className="landing-surface landing-animated-card bg-[rgba(17,24,43,0.82)] p-8">
            <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">Platform Narrative</div>
            <h2 className="mt-3 text-[28px] font-semibold text-landing-primary">See how signals become actions, cases, and accountable outcomes.</h2>
            <div className="mt-6 grid gap-4 md:grid-cols-3">
              {[
                ['Signal Intake', 'Events, devices, and policy inputs arrive with environment context.'],
                ['Decision Layer', 'Rules and explainability guide routing before escalation happens.'],
                ['Operational Closure', 'Analysts act with queue state, notes, and audit-ready evidence.'],
              ].map(([label, text]) => (
                <div key={label} className="rounded-[12px] border border-[var(--landing-border)] bg-[rgba(15,23,42,0.92)] p-4">
                  <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">{label}</div>
                  <p className="mt-3 text-[15px] leading-7 text-landing-secondary">{text}</p>
                </div>
              ))}
            </div>
          </article>

          <article className="landing-surface landing-animated-card bg-[rgba(17,24,43,0.82)] p-8">
            <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">Core Use Cases</div>
            <div className="mt-5 space-y-4">
              {useCases.map((useCase) => {
                const Component = useCase.icon;
                return (
                  <div key={useCase.label} className="flex items-center gap-3 rounded-[12px] border border-[var(--landing-border)] bg-[rgba(15,23,42,0.92)] px-4 py-4 text-[15px] text-landing-secondary">
                    <Component className="h-5 w-5 text-[var(--landing-tech)]" />
                    <span>{useCase.label}</span>
                  </div>
                );
              })}
            </div>
            <Link href="/solutions" className="landing-accent-button mt-6 inline-flex h-11 px-5">
              Explore Solutions
            </Link>
          </article>
        </div>
      </section>

      <section className="landing-container py-6">
        <div className="grid gap-5 xl:grid-cols-3">
          {[
            ['Visibility', 'Track route confidence and analyst pressure before queues overflow.'],
            ['Control', 'Adjust policies with clear blast-radius understanding and environment context.'],
            ['Trust', 'Maintain explainable decisions for internal teams and external auditors.'],
          ].map(([title, text]) => (
            <article key={title} className="landing-surface landing-animated-card bg-[rgba(17,24,43,0.82)] p-6">
              <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">Capability</div>
              <h3 className="mt-3 text-[22px] font-semibold text-landing-primary">{title}</h3>
              <p className="mt-3 text-[16px] leading-8 text-landing-secondary">{text}</p>
            </article>
          ))}
        </div>
      </section>
    </MarketingShell>
  );
}

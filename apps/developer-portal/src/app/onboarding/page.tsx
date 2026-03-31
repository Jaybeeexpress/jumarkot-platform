'use client';

import Link from 'next/link';
import { useSearchParams } from 'next/navigation';
import { CheckCircle2, Circle, CircleDashed, Rocket, Shield } from 'lucide-react';
import { MarketingShell } from '@/components/marketing/MarketingShell';

const navItems = [
  { label: 'Product', href: '/product' },
  { label: 'Solutions', href: '/solutions' },
  { label: 'Pricing', href: '/pricing' },
  { label: 'Developers', href: '/developers' },
  { label: 'Resources', href: '/resources' },
  { label: 'Sign In', href: '/signin' },
];

const setupSteps = [
  { title: 'Verify identity provider', detail: 'Provider scope and workspace trust checks are complete.', done: true },
  { title: 'Select environment profile', detail: 'Choose sandbox or production deployment posture.', done: true },
  { title: 'Create first API key', detail: 'Generate scoped credentials for integration.', done: false },
  { title: 'Run signal health check', detail: 'Validate ingestion, routing, and webhook delivery.', done: false },
];

export default function OnboardingPage() {
  const searchParams = useSearchParams();
  const provider = (searchParams.get('provider') ?? 'provider').toUpperCase();

  return (
    <MarketingShell navItems={navItems} ctaHref="/signin" ctaLabel="Sign In">
      <section className="landing-container py-14">
        <div className="grid gap-6 xl:grid-cols-[1.3fr_0.7fr]">
          <article className="landing-surface landing-animated-card bg-[rgba(17,24,43,0.88)] p-8 lg:p-10">
            <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">Onboarding</div>
            <h1 className="mt-3 text-[48px] font-bold leading-[1.05] text-landing-primary">Workspace setup in progress</h1>
            <p className="mt-4 text-[20px] leading-[1.7] text-landing-secondary">
              Signed in with {provider}. Complete these steps to activate your first routing workflow.
            </p>

            <div className="mt-8 space-y-3">
              {setupSteps.map((step) => (
                <div key={step.title} className="rounded-[12px] border border-[var(--landing-border)] bg-[rgba(15,23,42,0.92)] px-4 py-4">
                  <div className="flex items-start gap-3">
                    <div className="mt-0.5 text-[var(--landing-tech)]">
                      {step.done ? <CheckCircle2 className="h-4.5 w-4.5" /> : <Circle className="h-4.5 w-4.5" />}
                    </div>
                    <div>
                      <div className="text-[15px] font-semibold text-landing-primary">{step.title}</div>
                      <div className="mt-1 text-[14px] text-landing-secondary">{step.detail}</div>
                    </div>
                  </div>
                </div>
              ))}
            </div>

            <div className="mt-8 flex flex-wrap gap-3">
              <Link href="/api-keys" className="landing-accent-button h-11 px-5">
                Open API Keys
              </Link>
              <Link href="/request-logs" className="landing-secondary-button h-11 px-5">
                Continue To Request Logs
              </Link>
            </div>
          </article>

          <article className="landing-surface landing-animated-card bg-[rgba(17,24,43,0.88)] p-7">
            <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">Onboarding Context</div>
            <div className="mt-5 space-y-4">
              <div className="rounded-[12px] border border-[var(--landing-border)] bg-[rgba(15,23,42,0.92)] p-4">
                <div className="flex items-center gap-2 text-[15px] font-semibold text-landing-primary">
                  <Shield className="h-4.5 w-4.5 text-[var(--landing-tech)]" />
                  Security posture
                </div>
                <div className="mt-2 text-[14px] text-landing-secondary">Sandbox access active. Production enablement requires one admin approval.</div>
              </div>
              <div className="rounded-[12px] border border-[var(--landing-border)] bg-[rgba(15,23,42,0.92)] p-4">
                <div className="flex items-center gap-2 text-[15px] font-semibold text-landing-primary">
                  <CircleDashed className="h-4.5 w-4.5 text-[var(--landing-tech)]" />
                  Data readiness
                </div>
                <div className="mt-2 text-[14px] text-landing-secondary">Event ingestion heartbeat healthy. Next check scheduled in 90 seconds.</div>
              </div>
              <div className="rounded-[12px] border border-[var(--landing-border)] bg-[rgba(15,23,42,0.92)] p-4">
                <div className="flex items-center gap-2 text-[15px] font-semibold text-landing-primary">
                  <Rocket className="h-4.5 w-4.5 text-[var(--landing-accent)]" />
                  Recommended next action
                </div>
                <div className="mt-2 text-[14px] text-landing-secondary">Create your first scoped API key, then run a webhook delivery smoke test.</div>
              </div>
            </div>
          </article>
        </div>
      </section>
    </MarketingShell>
  );
}

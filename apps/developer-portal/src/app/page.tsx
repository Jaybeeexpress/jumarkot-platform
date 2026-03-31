import Link from 'next/link';
import {
  ArrowRight,
  BadgeDollarSign,
  CircleDollarSign,
  FileCheck2,
  GitBranch,
  Github,
  Gitlab,
  Layers3,
  Radar,
  Scale,
  Shield,
  Workflow,
} from 'lucide-react';
import { InteractivePreview } from '@/components/marketing/InteractivePreview';
import { MarketingShell } from '@/components/marketing/MarketingShell';

const navItems = [
  { label: 'Product', href: '/product' },
  { label: 'Solutions', href: '/solutions' },
  { label: 'Pricing', href: '/pricing' },
  { label: 'Developers', href: '/developers' },
  { label: 'Resources', href: '/resources' },
  { label: 'Sign In', href: '#access' },
];

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

const productPanels = [
  {
    label: 'Shared Command Surface',
    title: 'One operating model for analysts, platform admins, and developers',
    text: 'Jumarkot keeps queue state, decision logic, environment health, and delivery telemetry aligned in a single structured interface.',
    icon: Layers3,
  },
  {
    label: 'Risk Orchestration',
    title: 'Route every signal through explainable policy and action layers',
    text: 'Pair rule execution, review routing, and downstream controls with the exact signal context that triggered them.',
    icon: Workflow,
  },
];

const solutionCards = [
  {
    title: 'Fraud Operations',
    text: 'Centralize alert review, case ownership, and escalation timing for faster fraud response.',
    icon: Radar,
  },
  {
    title: 'Compliance Review',
    text: 'Track screening actions, policy exceptions, and audit evidence in one governed workflow.',
    icon: FileCheck2,
  },
  {
    title: 'Trust & Platform Risk',
    text: 'Give product, risk, and engineering teams a single place to inspect routing decisions and service health.',
    icon: Scale,
  },
];

const pricingPlans = [
  {
    name: 'Sandbox',
    price: '$0',
    note: 'For evaluation and integration design',
    features: ['Developer portal access', 'Test webhooks and request logs', 'Sample policies and screening data'],
  },
  {
    name: 'Growth',
    price: '$1,250',
    note: 'Per month, billed annually',
    features: ['Live decision routing', 'Analyst queues and case tooling', 'Environment monitoring and webhook governance'],
  },
  {
    name: 'Enterprise',
    price: 'Custom',
    note: 'For large-scale fraud and compliance teams',
    features: ['Multi-team controls and SSO', 'Audit workflows and bespoke policies', 'Dedicated onboarding and operating reviews'],
  },
];

const resources = [
  {
    label: 'Implementation Guide',
    text: 'Roll out ingestion, decisioning, and webhook delivery with a structured launch plan.',
  },
  {
    label: 'Operational Playbooks',
    text: 'Align analysts and compliance reviewers on triage, escalation, and closure patterns.',
  },
  {
    label: 'Product Walkthrough',
    text: 'Review dashboards, queues, and policy surfaces in a guided platform narrative.',
  },
];

const homepageDeveloperCards = [
  {
    title: 'API keys and environment access',
    text: 'Create scoped access and rotate credentials cleanly.',
    cta: 'Open API Keys',
    href: '/api-keys',
    icon: Layers3,
  },
  {
    title: 'Request logs and payload inspection',
    text: 'Review inbound and outbound traffic with request and response context.',
    cta: 'Inspect Logs',
    href: '/request-logs',
    icon: Radar,
  },
  {
    title: 'Webhook governance',
    text: 'Monitor delivery performance and retry operationally sensitive events.',
    cta: 'Open Webhooks',
    href: '/webhooks',
    icon: FileCheck2,
  },
];

const footerLinks = ['Privacy Policy', 'Terms of Service', 'Contact'];

export default function HomePage() {
  return (
    <MarketingShell navItems={navItems} ctaHref="#access" ctaLabel="Enter Platform">
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

              <InteractivePreview />
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

          <div className="mt-8 grid gap-5 xl:grid-cols-2">
            {productPanels.map((panel) => {
              const Icon = panel.icon;

              return (
                <article key={panel.title} className="landing-surface bg-[rgba(17,24,43,0.82)] p-7 transition-all duration-150 ease-in hover:bg-[rgba(26,34,56,0.96)]">
                  <div className="flex items-start gap-4">
                    <div className="flex h-12 w-12 items-center justify-center rounded-[12px] bg-[rgba(255,45,111,0.12)] text-[var(--landing-accent)]">
                      <Icon className="h-5 w-5" />
                    </div>
                    <div>
                      <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">{panel.label}</div>
                      <h3 className="mt-3 text-[28px] font-semibold leading-[1.2] text-landing-primary">{panel.title}</h3>
                      <p className="mt-4 text-[16px] leading-8 text-landing-secondary">{panel.text}</p>
                    </div>
                  </div>
                </article>
              );
            })}
          </div>
        </section>

        <section className="landing-container py-10">
          <div className="mb-6 max-w-[760px]">
            <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">Solutions</div>
            <h3 className="mt-3 text-[28px] font-semibold text-landing-primary">Designed for the teams that own enterprise trust outcomes.</h3>
          </div>
          <div className="grid gap-5 xl:grid-cols-3">
            {solutionCards.map((card) => {
              const Icon = card.icon;

              return (
                <article key={card.title} className="landing-surface bg-[rgba(17,24,43,0.82)] p-6 transition-all duration-150 ease-in hover:bg-[rgba(26,34,56,0.96)]">
                  <div className="flex h-12 w-12 items-center justify-center rounded-[12px] bg-[rgba(56,189,248,0.12)] text-[var(--landing-tech)]">
                    <Icon className="h-5 w-5" />
                  </div>
                  <h3 className="mt-5 text-[22px] font-semibold text-landing-primary">{card.title}</h3>
                  <p className="mt-3 text-[16px] leading-8 text-landing-secondary">{card.text}</p>
                  <Link href="/solutions" className="mt-6 inline-flex items-center gap-2 text-[15px] font-semibold text-[var(--landing-tech)] hover:text-[#7DD3FC]">
                    Explore Solution
                    <ArrowRight className="h-4 w-4" />
                  </Link>
                </article>
              );
            })}
          </div>
        </section>

        <section className="landing-container py-10">
          <div className="mb-6 max-w-[760px]">
            <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">Pricing</div>
            <h3 className="mt-3 text-[28px] font-semibold text-landing-primary">Structured entry points for evaluation, rollout, and scale.</h3>
          </div>
          <div className="grid gap-5 xl:grid-cols-3">
            {pricingPlans.map((plan, index) => (
              <article key={plan.name} className={`landing-surface p-6 transition-all duration-150 ease-in hover:bg-[rgba(26,34,56,0.96)] ${index === 1 ? 'bg-[rgba(255,45,111,0.08)]' : 'bg-[rgba(17,24,43,0.82)]'}`}>
                <div className="flex items-center justify-between gap-3">
                  <div>
                    <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">{plan.name}</div>
                    <div className="mt-3 text-[36px] font-bold leading-none text-landing-primary">{plan.price}</div>
                  </div>
                  {index === 1 && <span className="rounded-full bg-[rgba(255,45,111,0.14)] px-3 py-1 text-[11px] font-semibold uppercase tracking-[0.04em] text-[#FDA4AF]">Recommended</span>}
                </div>
                <div className="mt-3 text-[14px] text-landing-muted">{plan.note}</div>
                <div className="mt-6 space-y-3">
                  {plan.features.map((feature) => (
                    <div key={feature} className="flex items-start gap-3 text-[15px] text-landing-secondary">
                      <CircleDollarSign className="mt-0.5 h-4 w-4 shrink-0 text-[var(--landing-tech)]" />
                      <span>{feature}</span>
                    </div>
                  ))}
                </div>
                <Link href={index === 1 ? '/pricing' : '#access'} className={`mt-8 inline-flex h-11 w-full items-center justify-center rounded-[12px] text-[15px] font-semibold ${index === 1 ? 'landing-accent-button' : 'landing-secondary-button'}`}>
                  {index === 2 ? 'Talk To Sales' : 'Get Started'}
                </Link>
              </article>
            ))}
          </div>
        </section>

        <section className="landing-container py-10">
          <div className="mb-6 max-w-[760px]">
            <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">Developers</div>
            <h3 className="mt-3 text-[28px] font-semibold text-landing-primary">A portal experience built for integration teams, not just demos.</h3>
          </div>
          <div className="grid gap-5 xl:grid-cols-3">
            {homepageDeveloperCards.map((card) => {
              const Icon = card.icon;

              return (
              <article key={card.title} className="landing-surface bg-[rgba(17,24,43,0.82)] p-6 transition-all duration-150 ease-in hover:bg-[rgba(26,34,56,0.96)]">
                <div className="flex h-12 w-12 items-center justify-center rounded-[12px] bg-[rgba(56,189,248,0.12)] text-[var(--landing-tech)]">
                  <Icon className="h-5 w-5" />
                </div>
                <h3 className="mt-5 text-[22px] font-semibold text-landing-primary">{card.title}</h3>
                <p className="mt-3 text-[16px] leading-8 text-landing-secondary">{card.text}</p>
                <Link href={card.href} className="mt-6 inline-flex items-center gap-2 text-[15px] font-semibold text-[var(--landing-tech)] hover:text-[#7DD3FC]">
                  {card.cta}
                  <ArrowRight className="h-4 w-4" />
                </Link>
              </article>
            );})}
          </div>
          <Link href="/developers" className="landing-secondary-button mt-6 inline-flex h-11 px-5">
            View Developer Page
          </Link>
        </section>

        <section className="landing-container py-10">
          <div className="mb-6 max-w-[760px]">
            <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">Resources</div>
            <h3 className="mt-3 text-[28px] font-semibold text-landing-primary">Operational guidance and implementation context for real deployments.</h3>
          </div>
          <div className="grid gap-5 xl:grid-cols-3">
            {resources.map((resource) => (
              <article key={resource.label} className="landing-surface bg-[rgba(17,24,43,0.82)] p-6 transition-all duration-150 ease-in hover:bg-[rgba(26,34,56,0.96)]">
                <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">Resource</div>
                <h3 className="mt-4 text-[22px] font-semibold text-landing-primary">{resource.label}</h3>
                <p className="mt-3 text-[16px] leading-8 text-landing-secondary">{resource.text}</p>
                <Link href="/resources" className="mt-6 inline-flex items-center gap-2 text-[15px] font-semibold text-[var(--landing-tech)] hover:text-[#7DD3FC]">
                  Open Resource Page
                  <ArrowRight className="h-4 w-4" />
                </Link>
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
              <Link href="#access" className="landing-accent-button h-11 px-5">
                Sign In
              </Link>
              <Link href="/api-keys" className="landing-secondary-button h-11 px-5">
                Explore Developer Portal
              </Link>
            </div>
          </div>
        </section>
    </MarketingShell>
  );
}
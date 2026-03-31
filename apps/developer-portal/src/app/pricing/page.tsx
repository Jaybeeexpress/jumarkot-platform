import { CircleDollarSign } from 'lucide-react';
import { MarketingShell } from '@/components/marketing/MarketingShell';

const navItems = [
  { label: 'Product', href: '/product' },
  { label: 'Solutions', href: '/solutions' },
  { label: 'Pricing', href: '/pricing' },
  { label: 'Developers', href: '/developers' },
  { label: 'Resources', href: '/resources' },
  { label: 'Sign In', href: '/#access' },
];

const plans = [
  {
    name: 'Sandbox',
    price: '$0',
    note: 'For evaluation and integration design',
    features: ['Developer portal access', 'Request logs and webhook previews', 'Starter policies and sample environments'],
  },
  {
    name: 'Growth',
    price: '$1,250',
    note: 'Per month, billed annually',
    features: ['Live decision routing', 'Analyst queues and review workflows', 'Environment monitoring and webhook governance'],
  },
  {
    name: 'Enterprise',
    price: 'Custom',
    note: 'For high-volume risk and compliance teams',
    features: ['SSO and advanced governance', 'Dedicated onboarding and policy reviews', 'Custom routing and audit workflows'],
  },
];

export default function PricingPage() {
  return (
    <MarketingShell navItems={navItems} ctaHref="/#access" ctaLabel="Sign In">
      <section className="landing-container py-14">
        <div className="landing-surface bg-[rgba(17,24,43,0.84)] p-8 lg:p-10">
          <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">Pricing</div>
          <h1 className="mt-4 text-[48px] font-bold leading-[1.05] text-landing-primary">Structured plans for evaluation, rollout, and operational scale.</h1>
          <p className="mt-5 max-w-[760px] text-[20px] leading-[1.7] text-landing-secondary">Start in sandbox, move into live decisioning, and expand into enterprise controls without changing your product surface.</p>
        </div>
      </section>

      <section className="landing-container py-4">
        <div className="grid gap-5 xl:grid-cols-3">
          {plans.map((plan, index) => (
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
              <a href="/#access" className={`mt-8 inline-flex h-11 w-full items-center justify-center rounded-[12px] text-[15px] font-semibold ${index === 1 ? 'landing-accent-button' : 'landing-secondary-button'}`}>
                {index === 2 ? 'Talk To Sales' : 'Get Started'}
              </a>
            </article>
          ))}
        </div>
      </section>
    </MarketingShell>
  );
}

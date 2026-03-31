import Link from 'next/link';
import { ArrowRight, GitBranch, Github, Gitlab, ShieldCheck } from 'lucide-react';
import { MarketingShell } from '@/components/marketing/MarketingShell';

const navItems = [
  { label: 'Product', href: '/product' },
  { label: 'Solutions', href: '/solutions' },
  { label: 'Pricing', href: '/pricing' },
  { label: 'Developers', href: '/developers' },
  { label: 'Resources', href: '/resources' },
  { label: 'Sign In', href: '/signin' },
];

const providers = [
  { label: 'Continue with GitHub', href: '/onboarding?provider=github', icon: Github },
  { label: 'Continue with GitLab', href: '/onboarding?provider=gitlab', icon: Gitlab },
  { label: 'Continue with Bitbucket', href: '/onboarding?provider=bitbucket', icon: GitBranch },
];

export default function SignInPage() {
  return (
    <MarketingShell navItems={navItems} ctaHref="/signin" ctaLabel="Sign In">
      <section className="landing-container py-14">
        <div className="mx-auto max-w-[760px] landing-surface landing-animated-card bg-[rgba(17,24,43,0.9)] p-8 lg:p-10">
          <div className="flex justify-center">
            <div className="flex h-14 w-14 items-center justify-center rounded-[14px] border border-[rgba(255,45,111,0.25)] bg-[rgba(255,45,111,0.12)] text-[var(--landing-accent)]">
              <ShieldCheck className="h-7 w-7" />
            </div>
          </div>

          <div className="mt-6 text-center">
            <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">Account Access</div>
            <h1 className="mt-3 text-[48px] font-bold leading-[1.05] text-landing-primary">Sign in to Jumarkot</h1>
            <p className="mt-4 text-[20px] leading-[1.7] text-landing-secondary">
              Continue with your provider to enter the platform and complete environment onboarding.
            </p>
          </div>

          <div className="mt-8 space-y-4">
            {providers.map((provider) => {
              const Icon = provider.icon;

              return (
                <Link key={provider.label} href={provider.href} className="landing-auth-button">
                  <span className="flex items-center gap-3">
                    <Icon className="h-5 w-5 text-landing-secondary" />
                    <span className="text-[15px] font-semibold text-landing-primary">{provider.label}</span>
                  </span>
                  <ArrowRight className="h-4 w-4 text-landing-muted" />
                </Link>
              );
            })}
          </div>

          <div className="mt-8 rounded-[12px] border border-[var(--landing-border)] bg-[rgba(15,23,42,0.92)] p-4 text-[14px] text-landing-secondary">
            After sign-in, we will guide you through workspace setup, environment verification, and first policy routing checks.
          </div>
        </div>
      </section>
    </MarketingShell>
  );
}

import Link from 'next/link';
import { BookOpenText, FileCheck2, Layers3 } from 'lucide-react';
import { MarketingShell } from '@/components/marketing/MarketingShell';

const navItems = [
  { label: 'Product', href: '/product' },
  { label: 'Solutions', href: '/solutions' },
  { label: 'Pricing', href: '/pricing' },
  { label: 'Developers', href: '/developers' },
  { label: 'Resources', href: '/resources' },
  { label: 'Sign In', href: '/signin' },
];

const resources = [
  {
    title: 'Implementation Guide',
    text: 'Plan ingestion, decisioning, and delivery rollout with a structured deployment narrative.',
    icon: BookOpenText,
  },
  {
    title: 'Operational Playbooks',
    text: 'Align analysts and compliance teams on triage, escalation, and closure patterns.',
    icon: FileCheck2,
  },
  {
    title: 'Product Walkthrough',
    text: 'Review dashboards, queues, and governance surfaces in a guided product story.',
    icon: Layers3,
  },
];

export default function ResourcesPage() {
  return (
    <MarketingShell navItems={navItems} ctaHref="/signin" ctaLabel="Sign In">
      <section className="landing-container py-14">
        <div className="landing-surface bg-[rgba(17,24,43,0.84)] p-8 lg:p-10">
          <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">Resources</div>
          <h1 className="mt-4 text-[48px] font-bold leading-[1.05] text-landing-primary">Guidance for teams rolling out risk and compliance operations deliberately.</h1>
          <p className="mt-5 max-w-[760px] text-[20px] leading-[1.7] text-landing-secondary">Everything here is designed to help technical and operational teams understand the platform and adopt it cleanly.</p>
        </div>
      </section>

      <section className="landing-container py-4">
        <div className="grid gap-5 xl:grid-cols-3">
          {resources.map((resource) => {
            const Icon = resource.icon;
            return (
              <article key={resource.title} className="landing-surface landing-animated-card bg-[rgba(17,24,43,0.82)] p-6 transition-all duration-150 ease-in hover:bg-[rgba(26,34,56,0.96)]">
                <div className="flex h-12 w-12 items-center justify-center rounded-[12px] bg-[rgba(56,189,248,0.12)] text-[var(--landing-tech)]">
                  <Icon className="h-5 w-5" />
                </div>
                <h2 className="mt-5 text-[22px] font-semibold text-landing-primary">{resource.title}</h2>
                <p className="mt-3 text-[16px] leading-8 text-landing-secondary">{resource.text}</p>
                <Link href="/signin" className="mt-6 inline-flex items-center gap-2 text-[15px] font-semibold text-[var(--landing-tech)] hover:text-[#7DD3FC]">
                  Request Access
                </Link>
              </article>
            );
          })}
        </div>
      </section>

      <section className="landing-container py-6">
        <article className="landing-surface landing-animated-card bg-[rgba(17,24,43,0.82)] p-7">
          <div className="text-[12px] font-semibold uppercase tracking-[0.04em] text-landing-muted">Knowledge Streams</div>
          <div className="mt-4 grid gap-4 md:grid-cols-3">
            {[
              ['Launch Guides', 'Deployment sequences and ownership checklists for rollout teams.'],
              ['Operational References', 'Playbooks for triage, escalation, and queue balancing.'],
              ['Audit Materials', 'Policy rationale and reviewer actions for compliance evidence.'],
            ].map(([title, text]) => (
              <div key={title} className="rounded-[12px] border border-[var(--landing-border)] bg-[rgba(15,23,42,0.92)] p-4">
                <div className="text-[15px] font-semibold text-landing-primary">{title}</div>
                <div className="mt-2 text-[14px] leading-7 text-landing-secondary">{text}</div>
              </div>
            ))}
          </div>
        </article>
      </section>
    </MarketingShell>
  );
}

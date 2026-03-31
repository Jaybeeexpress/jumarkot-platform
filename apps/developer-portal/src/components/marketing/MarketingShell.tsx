"use client";

import { useState } from 'react';
import type { ReactNode } from 'react';
import Link from 'next/link';
import type { Route } from 'next';
import { Github, Gitlab, GitBranch, Menu, Settings2, X } from 'lucide-react';

type NavItem = {
  label: string;
  href: Route | string;
};

type MarketingShellProps = {
  children: ReactNode;
  navItems: NavItem[];
  ctaHref?: Route | string;
  ctaLabel?: string;
};

const footerLinks = ['Privacy Policy', 'Terms of Service', 'Contact'];

export function MarketingShell({ children, navItems, ctaHref = '#access', ctaLabel = 'Enter Platform' }: MarketingShellProps) {
  const [mobileOpen, setMobileOpen] = useState(false);

  return (
    <div className="landing-page">
      <header className="landing-nav">
        <div className="landing-container flex h-full items-center justify-between gap-6">
          <Link href="/" className="text-[24px] font-bold tracking-[-0.02em] text-landing-primary">
            Jumarkot
          </Link>

          <nav className="hidden items-center gap-7 lg:flex">
            {navItems.map((item) => (
              <Link key={item.label} href={item.href} className="landing-nav-link">
                {item.label}
              </Link>
            ))}
          </nav>

          <div className="flex items-center gap-3">
            <button
              type="button"
              className="landing-settings-button lg:hidden"
              aria-label={mobileOpen ? 'Close navigation' : 'Open navigation'}
              onClick={() => setMobileOpen((value) => !value)}
            >
              {mobileOpen ? <X className="h-4 w-4" /> : <Menu className="h-4 w-4" />}
            </button>
            <button type="button" className="landing-settings-button" aria-label="Open settings">
              <Settings2 className="h-4 w-4" />
            </button>
            <Link href={ctaHref} className="landing-secondary-button hidden h-10 px-4 md:inline-flex" onClick={() => setMobileOpen(false)}>
              {ctaLabel}
            </Link>
          </div>
        </div>

        {mobileOpen && (
          <div className="landing-mobile-panel lg:hidden">
            <div className="landing-container py-4">
              <nav className="space-y-2">
                {navItems.map((item) => (
                  <Link
                    key={item.label}
                    href={item.href}
                    className="landing-mobile-link"
                    onClick={() => setMobileOpen(false)}
                  >
                    {item.label}
                  </Link>
                ))}
              </nav>
              <Link
                href={ctaHref}
                className="landing-accent-button mt-4 inline-flex h-10 w-full"
                onClick={() => setMobileOpen(false)}
              >
                {ctaLabel}
              </Link>
            </div>
          </div>
        )}
      </header>

      <main>{children}</main>

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
          <div className="flex items-center gap-3 text-[var(--landing-text-muted)]">
            <Github className="h-4 w-4" />
            <Gitlab className="h-4 w-4" />
            <GitBranch className="h-4 w-4" />
          </div>
        </div>
      </footer>
    </div>
  );
}

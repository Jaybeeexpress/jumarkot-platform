'use client';

import { useMemo, useState } from 'react';
import type { Route } from 'next';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { clsx } from 'clsx';
import { Activity, Bell, Menu, X } from 'lucide-react';

const links: Array<{ href: Route; label: string }> = [
  { href: '/', label: 'Home' },
  { href: '/decisions', label: 'Decisions' },
  { href: '/rules',     label: 'Rules'      },
  { href: '/tenants',   label: 'Tenants'    },
  { href: '/alerts',    label: 'Alerts'     },
];

export function Nav() {
  const pathname = usePathname();
  const [mobileOpen, setMobileOpen] = useState(false);

  const activeLabel = useMemo(
    () => links.find((l) => pathname?.startsWith(l.href))?.label ?? 'Workspace',
    [pathname],
  );

  const navLinkClass = (href: Route) =>
    clsx(
      'nav-pill rounded-full px-3.5 py-2 text-sm font-semibold transition-all',
      pathname?.startsWith(href)
        ? 'bg-cyan-900 text-cyan-50 shadow-md shadow-cyan-900/20'
        : 'text-slate-600 hover:-translate-y-0.5 hover:bg-white hover:text-cyan-900 hover:shadow-md hover:shadow-cyan-900/10',
    );

  return (
    <header className="sticky top-0 z-40 border-b border-cyan-900/10 bg-white/75 px-4 py-3 backdrop-blur md:px-6">
      <div className="mx-auto w-full max-w-7xl">
        <div className="flex items-center justify-between gap-3">
          <div className="flex items-center gap-3">
            <Link
              href="/"
              className="font-heading text-xl font-extrabold tracking-tight text-cyan-950 transition hover:-translate-y-0.5 hover:text-cyan-700"
            >
              Jumarkot
            </Link>
            <span className="hidden rounded-full border border-cyan-200 bg-cyan-50 px-3 py-1 text-[11px] font-semibold uppercase tracking-[0.14em] text-cyan-700 sm:inline-flex">
              Ops Console
            </span>
            <span className="hidden text-sm font-medium text-slate-500 lg:inline-flex">{activeLabel}</span>
          </div>

          <div className="hidden items-center gap-2 lg:flex">
            <div className="inline-flex items-center gap-2 rounded-full border border-emerald-200 bg-emerald-50 px-3 py-1 text-xs font-semibold text-emerald-700">
              <Activity className="h-3.5 w-3.5" />
              System Healthy
            </div>
            <button
              type="button"
              aria-label="Notifications"
              className="rounded-full border border-slate-200 bg-white p-2 text-slate-600 transition hover:-translate-y-0.5 hover:border-cyan-300 hover:text-cyan-800"
            >
              <Bell className="h-4 w-4" />
            </button>
            <Link
              href="/rules"
              className="btn-glow rounded-2xl px-4 py-2 text-sm font-semibold text-cyan-50 transition hover:-translate-y-0.5"
            >
              Quick Action
            </Link>
          </div>

          <button
            type="button"
            aria-label={mobileOpen ? 'Close menu' : 'Open menu'}
            onClick={() => setMobileOpen((v) => !v)}
            className="rounded-full border border-slate-200 bg-white p-2 text-slate-600 transition hover:border-cyan-300 hover:text-cyan-800 lg:hidden"
          >
            {mobileOpen ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
          </button>
        </div>

        <nav className="mt-3 hidden items-center gap-2 lg:flex">
          {links.map((l) => (
            <Link key={l.href} href={l.href} className={navLinkClass(l.href)}>
              {l.label}
            </Link>
          ))}
        </nav>

        {mobileOpen && (
          <div className="mt-3 space-y-2 rounded-2xl border border-cyan-100 bg-white/90 p-3 shadow-xl shadow-cyan-900/10 lg:hidden">
            {links.map((l) => (
              <Link
                key={l.href}
                href={l.href}
                className={clsx(
                  'block rounded-xl px-3 py-2 text-sm font-semibold transition',
                  pathname?.startsWith(l.href)
                    ? 'bg-cyan-900 text-cyan-50'
                    : 'text-slate-700 hover:bg-cyan-50 hover:text-cyan-900',
                )}
                onClick={() => setMobileOpen(false)}
              >
                {l.label}
              </Link>
            ))}

            <div className="flex items-center justify-between rounded-xl border border-emerald-200 bg-emerald-50 px-3 py-2 text-xs font-semibold text-emerald-700">
              <span className="inline-flex items-center gap-1.5">
                <Activity className="h-3.5 w-3.5" />
                System Healthy
              </span>
              <Bell className="h-4 w-4" />
            </div>
          </div>
        )}
      </div>
    </header>
  );
}

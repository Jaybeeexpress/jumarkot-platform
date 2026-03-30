'use client';

import type { Route } from 'next';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { clsx } from 'clsx';

const links: Array<{ href: Route; label: string }> = [
  { href: '/decisions', label: 'Decisions' },
  { href: '/rules',     label: 'Rules'      },
  { href: '/tenants',   label: 'Tenants'    },
  { href: '/alerts',    label: 'Alerts'     },
];

export function Nav() {
  const pathname = usePathname();
  return (
    <header className="sticky top-0 z-40 border-b border-cyan-900/10 bg-white/70 px-4 py-3 backdrop-blur md:px-6">
      <div className="mx-auto flex w-full max-w-7xl flex-col gap-3 md:flex-row md:items-center md:justify-between">
        <div className="flex items-center gap-3">
          <Link
            href="/"
            className="font-heading text-lg font-bold tracking-tight text-cyan-950 transition hover:text-cyan-700"
          >
            Jumarkot
          </Link>
          <span className="rounded-full border border-cyan-200 bg-cyan-50 px-3 py-1 text-[11px] font-semibold uppercase tracking-[0.14em] text-cyan-700">
            Ops Console
          </span>
        </div>
        <nav className="flex flex-wrap items-center gap-2 text-sm font-semibold text-slate-600 md:gap-3">
          {links.map((l) => (
            <Link
              key={l.href}
              href={l.href}
              className={clsx(
                'rounded-full px-3 py-1.5 transition-all',
                pathname?.startsWith(l.href)
                  ? 'bg-cyan-900 text-cyan-50 shadow-sm shadow-cyan-900/20'
                  : 'hover:bg-white hover:text-cyan-900',
              )}
            >
              {l.label}
            </Link>
          ))}
        </nav>
      </div>
    </header>
  );
}

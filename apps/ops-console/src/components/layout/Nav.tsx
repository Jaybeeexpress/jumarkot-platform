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
    <header className="border-b bg-white px-6 py-4">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-3">
          <Link href="/" className="text-lg font-bold tracking-tight text-slate-900">
            Jumarkot
          </Link>
          <span className="rounded-full bg-slate-100 px-2 py-0.5 text-xs font-medium text-slate-500">
            Ops Console
          </span>
        </div>
        <nav className="flex items-center gap-6 text-sm font-medium">
          {links.map((l) => (
            <Link
              key={l.href}
              href={l.href}
              className={clsx(
                'transition-colors',
                pathname?.startsWith(l.href)
                  ? 'font-semibold text-slate-900'
                  : 'text-slate-600 hover:text-slate-900',
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

'use client';

import type { ReactNode } from 'react';
import type { Route } from 'next';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { clsx } from 'clsx';
import { Bell, KeyRound, LineChart, Search, UserCircle2, Webhook, Workflow } from 'lucide-react';

const items: Array<{ href: Route; label: string; icon: typeof Workflow }> = [
  { href: '/', label: 'Overview', icon: Workflow },
  { href: '/api-keys', label: 'API Keys', icon: KeyRound },
  { href: '/request-logs', label: 'Request Logs', icon: Search },
  { href: '/webhooks', label: 'Webhooks', icon: Webhook },
  { href: '/usage', label: 'Usage', icon: LineChart },
];

export function PortalShell({ title, breadcrumb, children }: { title: string; breadcrumb: string[]; children: ReactNode }) {
  const pathname = usePathname();

  return (
    <div className="portal-shell">
      <aside className="portal-sidebar">
        <div className="flex h-16 items-center border-b border-[#374151] px-4">
          <div>
            <div className="text-[15px] font-semibold text-white">Jumarkot</div>
            <div className="text-[11px] uppercase tracking-[0.04em] text-[#9ca3af]">Developer Portal</div>
          </div>
        </div>
        <nav className="px-3 py-4">
          <div className="space-y-1">
            {items.map((item) => {
              const Icon = item.icon;
              const active = pathname === item.href || (item.href !== '/' && pathname?.startsWith(item.href));

              return (
                <Link
                  key={item.href}
                  href={item.href}
                  className={clsx(
                    'flex h-11 items-center gap-3 rounded-r-md border-l-[3px] px-3 text-[14px] font-medium',
                    active
                      ? 'border-l-[#6366F1] bg-[#1F2937] text-white'
                      : 'border-l-transparent text-[#9CA3AF] hover:bg-[#1F2937] hover:text-white',
                  )}
                >
                  <Icon className="h-5 w-5" />
                  <span>{item.label}</span>
                </Link>
              );
            })}
          </div>
        </nav>
      </aside>
      <div className="flex min-w-0 flex-1 flex-col">
        <header className="portal-topbar flex items-center">
          <div className="portal-content-max flex h-full items-center justify-between gap-4">
            <div>
              <div className="text-[12px] text-[#6B7280]">{breadcrumb.join(' / ')}</div>
              <h2>{title}</h2>
            </div>
            <div className="hidden xl:flex">
              <input className="portal-input w-[400px]" placeholder="Search keys, requests, webhooks..." />
            </div>
            <div className="flex items-center gap-3">
              <button type="button" className="portal-button portal-button-secondary w-9 px-0" aria-label="Notifications">
                <Bell className="h-4 w-4" />
              </button>
              <select aria-label="Environment switcher" title="Environment switcher" className="portal-input hidden w-[140px] md:block">
                <option>Sandbox</option>
                <option>Production</option>
              </select>
              <div className="flex h-9 w-9 items-center justify-center rounded-full bg-[#1F2937] text-[#9CA3AF]">
                <UserCircle2 className="h-5 w-5" />
              </div>
            </div>
          </div>
        </header>
        <div className="flex-1 overflow-auto">
          <div className="portal-content-max py-6">{children}</div>
        </div>
      </div>
    </div>
  );
}
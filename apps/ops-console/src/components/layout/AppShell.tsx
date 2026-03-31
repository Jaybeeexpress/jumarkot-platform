'use client';

import { useMemo, useState } from 'react';
import type { ReactNode } from 'react';
import type { Route } from 'next';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { clsx } from 'clsx';
import {
  Bell,
  ChevronLeft,
  ChevronRight,
  FileText,
  GanttChartSquare,
  LayoutDashboard,
  Scale,
  Search,
  Settings,
  ShieldAlert,
  ShieldCheck,
  UserCircle2,
  Users,
  Waypoints,
} from 'lucide-react';

type ShellProps = {
  title: string;
  breadcrumb: string[];
  children: ReactNode;
  rightPanel?: ReactNode;
};

const items: Array<{ href: Route; label: string; icon: typeof LayoutDashboard }> = [
  { href: '/' as Route, label: 'Dashboard', icon: LayoutDashboard },
  { href: '/alerts' as Route, label: 'Alerts', icon: ShieldAlert },
  { href: '/cases' as Route, label: 'Cases', icon: Scale },
  { href: '/decisions' as Route, label: 'Decisions', icon: ShieldCheck },
  { href: '/entities' as Route, label: 'Entities', icon: Users },
  { href: '/rules' as Route, label: 'Rules', icon: GanttChartSquare },
  { href: '/screening' as Route, label: 'Screening', icon: Waypoints },
  { href: '/reports' as Route, label: 'Reports', icon: FileText },
  { href: '/admin' as Route, label: 'Admin', icon: Settings },
];

export function AppShell({ title, breadcrumb, children, rightPanel }: ShellProps) {
  const pathname = usePathname();
  const [collapsed, setCollapsed] = useState(false);

  const breadcrumbText = useMemo(() => breadcrumb.join(' / '), [breadcrumb]);

  return (
    <div className="enterprise-shell">
      <aside
        className={clsx(
          'enterprise-sidebar',
          collapsed ? 'enterprise-sidebar-collapsed' : 'enterprise-sidebar-expanded',
        )}
      >
        <div className="flex h-16 items-center justify-between border-b border-light px-4">
          <div className={clsx('overflow-hidden', collapsed && 'hidden')}>
            <div className="text-[15px] font-semibold text-primary">Jumarkot</div>
            <div className="text-[11px] uppercase tracking-[0.04em] text-secondary">Ops Console</div>
          </div>
          <button
            type="button"
            onClick={() => setCollapsed((value) => !value)}
            className="enterprise-button enterprise-button-secondary w-9 px-0"
            aria-label={collapsed ? 'Expand sidebar' : 'Collapse sidebar'}
          >
            {collapsed ? <ChevronRight className="h-4 w-4" /> : <ChevronLeft className="h-4 w-4" />}
          </button>
        </div>

        <nav className="flex-1 px-3 py-4">
          <div className="space-y-1">
            {items.map((item) => {
              const Icon = item.icon;
              const active = pathname === item.href || (item.href !== '/' && pathname?.startsWith(item.href));

              return (
                <Link
                  key={item.href}
                  href={item.href}
                  className={clsx(
                    'flex h-11 items-center gap-3 rounded-r-md border-l-3 px-3 text-[14px] font-medium transition-colors',
                    active
                      ? 'border-l-[var(--brand-primary)] bg-panel text-primary'
                      : 'border-l-transparent text-secondary hover:bg-panel hover:text-primary',
                    collapsed && 'justify-center px-0',
                  )}
                >
                  <Icon className="h-5 w-5 shrink-0" />
                  {!collapsed && <span>{item.label}</span>}
                </Link>
              );
            })}
          </div>
        </nav>
      </aside>

      <div className="flex min-w-0 flex-1 flex-col">
        <header className="enterprise-topbar flex items-center">
          <div className="content-max flex h-full items-center justify-between gap-4">
            <div className="min-w-0">
              <div className="text-[12px] text-muted">{breadcrumbText}</div>
              <h2>{title}</h2>
            </div>

            <div className="hidden flex-1 justify-center xl:flex">
              <label className="relative w-full max-w-[400px]">
                <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted" />
                <input className="enterprise-input pl-9" placeholder="Search alerts, cases, entities..." />
              </label>
            </div>

            <div className="flex items-center gap-3">
              <button type="button" className="enterprise-button enterprise-button-secondary w-9 px-0" aria-label="Notifications">
                <Bell className="h-4 w-4" />
              </button>
              <select aria-label="Environment switcher" title="Environment switcher" className="enterprise-input hidden w-[140px] md:block">
                <option>Production</option>
                <option>Sandbox</option>
              </select>
              <div className="flex h-9 w-9 items-center justify-center rounded-full bg-panel text-secondary">
                <UserCircle2 className="h-5 w-5" />
              </div>
            </div>
          </div>
        </header>

        <div className="flex-1 overflow-auto">
          <div className="content-max py-6">
            {rightPanel ? (
              <div className="flex gap-6">
                <div className="min-w-0 flex-1">{children}</div>
                <aside className="hidden w-[360px] shrink-0 xl:block">{rightPanel}</aside>
              </div>
            ) : (
              children
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
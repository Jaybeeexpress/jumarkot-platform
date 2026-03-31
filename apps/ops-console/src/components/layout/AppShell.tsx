'use client';

import { useMemo, useState } from 'react';
import type { ReactNode } from 'react';
import type { Route } from 'next';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { clsx } from 'clsx';
import {
  Bell,
  CalendarDays,
  ChevronLeft,
  ChevronDown,
  ChevronRight,
  FilePlus2,
  FileText,
  GanttChartSquare,
  LayoutDashboard,
  Scale,
  Search,
  Settings,
  ShieldAlert,
  ShieldCheck,
  RefreshCcw,
  UserCircle2,
  Users,
  Waypoints,
} from 'lucide-react';

type ShellProps = {
  title: string;
  breadcrumb: string[];
  subtitle?: string;
  children: ReactNode;
  rightPanel?: ReactNode;
};

const NAV_ANIMATION_DELAY_CLASSES = [
  'enterprise-nav-delay-0',
  'enterprise-nav-delay-1',
  'enterprise-nav-delay-2',
  'enterprise-nav-delay-3',
  'enterprise-nav-delay-4',
  'enterprise-nav-delay-5',
  'enterprise-nav-delay-6',
  'enterprise-nav-delay-7',
  'enterprise-nav-delay-8',
  'enterprise-nav-delay-9',
  'enterprise-nav-delay-10',
  'enterprise-nav-delay-11',
];

const sections: Array<{
  label: 'MAIN' | 'ANALYSIS' | 'COMPLIANCE' | 'SYSTEM';
  items: Array<{ href: Route; label: string; icon: typeof LayoutDashboard }>;
}> = [
  {
    label: 'MAIN',
    items: [
      { href: '/' as Route, label: 'Dashboard', icon: LayoutDashboard },
      { href: '/alerts' as Route, label: 'Alerts', icon: ShieldAlert },
      { href: '/cases' as Route, label: 'Cases', icon: Scale },
    ],
  },
  {
    label: 'ANALYSIS',
    items: [
      { href: '/decisions' as Route, label: 'Decisions', icon: ShieldCheck },
      { href: '/entities' as Route, label: 'Entities', icon: Users },
      { href: '/rules' as Route, label: 'Rules', icon: GanttChartSquare },
    ],
  },
  {
    label: 'COMPLIANCE',
    items: [
      { href: '/screening' as Route, label: 'Screening', icon: Waypoints },
      { href: '/reports' as Route, label: 'Reports', icon: FileText },
    ],
  },
  {
    label: 'SYSTEM',
    items: [{ href: '/admin' as Route, label: 'Admin', icon: Settings }],
  },
];

export function AppShell({ title, breadcrumb, subtitle, children, rightPanel }: ShellProps) {
  const pathname = usePathname();
  const [collapsed, setCollapsed] = useState(false);
  const [isSidebarAnimating, setIsSidebarAnimating] = useState(false);

  const breadcrumbText = useMemo(() => breadcrumb.join(' / '), [breadcrumb]);
  const totalNavItems = useMemo(() => sections.reduce((count, section) => count + section.items.length, 0), []);

  const handleSidebarToggle = () => {
    setCollapsed((previous) => {
      const nextCollapsed = !previous;
      if (previous && !nextCollapsed) {
        setIsSidebarAnimating(true);
        window.setTimeout(() => setIsSidebarAnimating(false), 520);
      }
      return nextCollapsed;
    });
  };

  return (
    <div className="enterprise-shell">
      <aside className={clsx('enterprise-sidebar', collapsed ? 'enterprise-sidebar-collapsed' : 'enterprise-sidebar-expanded')}>
        <div className="enterprise-sidebar-brand">
          <div className={clsx('overflow-hidden', collapsed && 'hidden')}>
            <div className="text-[18px] font-semibold leading-none text-primary">Jumarkot</div>
            <div className="mt-1 text-[11px] font-medium uppercase tracking-[0.08em] text-muted">Ops Console</div>
          </div>
          <div className={clsx('mt-3 flex', collapsed ? 'justify-center' : 'justify-end')}>
            <button
              type="button"
              onClick={handleSidebarToggle}
              className="enterprise-sidebar-collapse"
              aria-label={collapsed ? 'Expand sidebar' : 'Collapse sidebar'}
            >
              {collapsed ? <ChevronRight className="h-4 w-4" /> : <ChevronLeft className="h-4 w-4" />}
            </button>
          </div>
        </div>

        <nav className="flex-1 overflow-auto px-4 py-4">
          <div className="space-y-[18px]">
            {sections.map((section) => (
              <div key={section.label} className="space-y-2">
                {!collapsed && <div className="enterprise-label px-2 py-1 text-[10px] text-muted">{section.label}</div>}
                <div className="space-y-2">
                  {section.items.map((item, itemIndex) => {
                    const Icon = item.icon;
                    const active = pathname === item.href || (item.href !== '/' && pathname?.startsWith(item.href));
                    const priorItemsCount = sections
                      .slice(0, sections.findIndex((candidate) => candidate.label === section.label))
                      .reduce((count, candidate) => count + candidate.items.length, 0);
                    const sequenceIndex = priorItemsCount + itemIndex;
                    const clampedIndex = Math.min(sequenceIndex, NAV_ANIMATION_DELAY_CLASSES.length - 1);
                    const delayClass = NAV_ANIMATION_DELAY_CLASSES[clampedIndex];

                    return (
                      <Link
                        key={item.href}
                        href={item.href}
                        className={clsx(
                          'flex h-10 items-center gap-[10px] rounded-[10px] border-l-[3px] px-3 text-[14px] font-medium transition-all duration-150 ease-in',
                          active
                            ? 'border-l-[var(--brand-primary)] bg-panel text-primary font-semibold shadow-[0_0_0_1px_#1F2937,0_8px_24px_rgba(0,0,0,0.18)]'
                            : 'border-l-transparent text-secondary hover:bg-[#1E293B] hover:text-primary',
                          !collapsed && isSidebarAnimating && sequenceIndex < totalNavItems && 'enterprise-nav-reveal',
                          !collapsed && isSidebarAnimating && delayClass,
                          collapsed && 'justify-center px-0',
                        )}
                      >
                        <Icon className={clsx('h-5 w-5 shrink-0', active ? 'text-primary' : 'text-muted')} />
                        {!collapsed && <span>{item.label}</span>}
                      </Link>
                    );
                  })}
                </div>
              </div>
            ))}
          </div>
        </nav>
      </aside>

      <div className="flex min-w-0 flex-1 flex-col">
        <header className="enterprise-topbar enterprise-topbar-sticky flex items-center">
          <div className="content-max flex h-full items-center justify-between gap-4">
            <div className="flex items-center gap-3">
              <span className="enterprise-chip enterprise-chip-success">Production</span>
              <button type="button" className="enterprise-button enterprise-button-secondary h-9 gap-2 px-3">
                <CalendarDays className="h-3.5 w-3.5" />
                Last 24 Hours
                <ChevronDown className="h-3.5 w-3.5" />
              </button>
              <span className="enterprise-chip enterprise-chip-neutral">
                <RefreshCcw className="h-3.5 w-3.5" />
                Synced 38s ago
              </span>
            </div>

            <div className="flex items-center gap-3">
              <button
                type="button"
                className="h-10 items-center gap-2 rounded-[10px] border border-[rgba(99,102,241,0.4)] bg-[var(--brand-primary)] px-3 text-[12px] font-semibold text-white transition-all duration-150 ease-in hover:bg-[var(--brand-hover)] inline-flex"
              >
                <FilePlus2 className="h-3.5 w-3.5" />
                Create Case
              </button>
              <button
                type="button"
                className="enterprise-button enterprise-button-secondary h-10 w-10 px-0"
                aria-label="Notifications"
              >
                <Bell className="h-4 w-4" />
              </button>
              <button
                type="button"
                className="flex h-10 w-10 items-center justify-center rounded-full border border-light bg-panel text-secondary transition-all duration-150 ease-in hover:bg-[#243245] hover:text-primary"
                aria-label="Profile"
              >
                <UserCircle2 className="h-5 w-5" />
              </button>
            </div>
          </div>
        </header>

        <div className="flex-1 overflow-auto">
          <div className="content-max pb-6 pt-4">
            <div className="mb-4">
              <label className="relative block w-full">
                <Search className="pointer-events-none absolute left-4 top-1/2 h-4 w-4 -translate-y-1/2 text-muted" />
                <input
                  className="enterprise-input enterprise-search-input"
                  placeholder="Search alerts, cases, entities..."
                />
              </label>
            </div>

            <section className="enterprise-page-header mb-4">
              <div className="min-w-0">
                <div className="text-[11px] font-medium uppercase tracking-[0.04em] text-muted">{breadcrumbText}</div>
                <h1 className="mt-[6px] text-[28px] font-bold leading-[1.05] text-primary">{title}</h1>
                {subtitle ? <p className="mt-2 text-[13px] text-secondary">{subtitle}</p> : null}
              </div>
            </section>
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
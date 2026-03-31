import type { NavSection } from '@/components/layout/EnterpriseShell';
import {
  Bell,
  ChevronDown,
  FileText,
  GanttChartSquare,
  LayoutDashboard,
  Scale,
  ShieldAlert,
  ShieldCheck,
  Users,
  Waypoints,
  Settings,
} from 'lucide-react';

export const OPS_CONSOLE_NAV_SECTIONS: NavSection[] = [
  {
    label: 'MAIN',
    items: [
      { href: '/', label: 'Dashboard', icon: LayoutDashboard },
      { href: '/alerts', label: 'Alerts', icon: ShieldAlert },
      { href: '/cases', label: 'Cases', icon: Scale },
    ],
  },
  {
    label: 'ANALYSIS',
    items: [
      { href: '/decisions', label: 'Decisions', icon: ShieldCheck },
      { href: '/entities', label: 'Entities', icon: Users },
      { href: '/rules', label: 'Rules', icon: GanttChartSquare },
    ],
  },
  {
    label: 'COMPLIANCE',
    items: [
      { href: '/screening', label: 'Screening', icon: Waypoints },
      { href: '/reports', label: 'Reports', icon: FileText },
    ],
  },
  {
    label: 'SYSTEM',
    items: [{ href: '/admin', label: 'Admin', icon: Settings }],
  },
];

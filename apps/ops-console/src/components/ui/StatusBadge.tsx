import { clsx } from 'clsx';

const variants: Record<string, string> = {
  // Rule / generic status
  ACTIVE:      'bg-emerald-500/12 text-emerald-400 border border-emerald-500/30',
  INACTIVE:    'bg-slate-500/12 text-slate-300 border border-slate-500/30',
  DRAFT:       'bg-amber-500/12 text-amber-400 border border-amber-500/30',
  SUSPENDED:   'bg-rose-500/12 text-rose-400 border border-rose-500/30',
  // RiskDecision
  APPROVE:     'bg-emerald-500/12 text-emerald-400 border border-emerald-500/30',
  REVIEW:      'bg-amber-500/12 text-amber-400 border border-amber-500/30',
  DECLINE:     'bg-rose-500/12 text-rose-400 border border-rose-500/30',
  BLOCK:       'bg-rose-500/18 text-rose-300 border border-rose-500/30',
  // RiskLevel
  LOW:         'bg-emerald-500/12 text-emerald-400 border border-emerald-500/30',
  MEDIUM:      'bg-amber-500/12 text-amber-400 border border-amber-500/30',
  HIGH:        'bg-orange-500/12 text-orange-400 border border-orange-500/30',
  CRITICAL:    'bg-rose-500/12 text-rose-400 border border-rose-500/30',
  // Environments
  PRODUCTION:  'bg-sky-500/12 text-sky-400 border border-sky-500/30',
  SANDBOX:     'bg-indigo-500/12 text-indigo-400 border border-indigo-500/30',
  OPEN:        'bg-sky-500/12 text-sky-400 border border-sky-500/30',
  IN_PROGRESS: 'bg-amber-500/12 text-amber-400 border border-amber-500/30',
  RESOLVED:    'bg-emerald-500/12 text-emerald-400 border border-emerald-500/30',
  P1:          'bg-rose-500/12 text-rose-400 border border-rose-500/30',
  P2:          'bg-orange-500/12 text-orange-400 border border-orange-500/30',
  P3:          'bg-sky-500/12 text-sky-400 border border-sky-500/30',
};

export function StatusBadge({ value }: { value: string }) {
  return (
    <span
      className={clsx(
        'inline-flex h-6 items-center rounded-md px-2.5 text-[11px] font-semibold tracking-[0.04em]',
        variants[value] ?? 'bg-slate-500/12 text-slate-300 border border-slate-500/30',
      )}
    >
      {value}
    </span>
  );
}

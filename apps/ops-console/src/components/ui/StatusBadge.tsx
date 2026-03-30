import { clsx } from 'clsx';

const variants: Record<string, string> = {
  // Rule / generic status
  ACTIVE:      'bg-emerald-100 text-emerald-800 ring-1 ring-emerald-300/70',
  INACTIVE:    'bg-slate-100 text-slate-700 ring-1 ring-slate-300/70',
  DRAFT:       'bg-amber-100 text-amber-800 ring-1 ring-amber-300/70',
  SUSPENDED:   'bg-rose-100 text-rose-700 ring-1 ring-rose-300/70',
  // RiskDecision
  APPROVE:     'bg-emerald-100 text-emerald-800 ring-1 ring-emerald-300/70',
  REVIEW:      'bg-amber-100 text-amber-800 ring-1 ring-amber-300/70',
  DECLINE:     'bg-rose-100 text-rose-800 ring-1 ring-rose-300/70',
  BLOCK:       'bg-rose-200 text-rose-900 ring-1 ring-rose-400/70',
  // RiskLevel
  LOW:         'bg-emerald-50 text-emerald-700 ring-1 ring-emerald-200/80',
  MEDIUM:      'bg-amber-50 text-amber-700 ring-1 ring-amber-200/80',
  HIGH:        'bg-orange-100 text-orange-800 ring-1 ring-orange-300/80',
  CRITICAL:    'bg-rose-100 text-rose-800 ring-1 ring-rose-300/80',
  // Environments
  PRODUCTION:  'bg-sky-100 text-sky-800 ring-1 ring-sky-300/70',
  SANDBOX:     'bg-teal-100 text-teal-800 ring-1 ring-teal-300/70',
};

export function StatusBadge({ value }: { value: string }) {
  return (
    <span
      className={clsx(
        'inline-flex items-center rounded-full px-2.5 py-1 text-[11px] font-semibold tracking-wide',
        variants[value] ?? 'bg-slate-100 text-slate-700 ring-1 ring-slate-300/70',
      )}
    >
      {value}
    </span>
  );
}

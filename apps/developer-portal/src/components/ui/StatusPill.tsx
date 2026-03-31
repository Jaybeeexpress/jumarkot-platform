import { clsx } from 'clsx';

const variants: Record<string, string> = {
  ACTIVE: 'bg-emerald-500/12 text-emerald-400 border border-emerald-500/30',
  DEGRADED: 'bg-amber-500/12 text-amber-400 border border-amber-500/30',
  FAILED: 'bg-rose-500/12 text-rose-400 border border-rose-500/30',
  SANDBOX: 'bg-indigo-500/12 text-indigo-400 border border-indigo-500/30',
  PRODUCTION: 'bg-sky-500/12 text-sky-400 border border-sky-500/30',
};

export function StatusPill({ value }: { value: string }) {
  return (
    <span className={clsx('inline-flex h-6 items-center rounded-md px-2.5 text-[11px] font-semibold tracking-[0.04em]', variants[value] ?? 'bg-slate-500/12 text-slate-300 border border-slate-500/30')}>
      {value}
    </span>
  );
}
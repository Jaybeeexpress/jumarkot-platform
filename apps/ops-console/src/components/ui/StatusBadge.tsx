import { clsx } from 'clsx';

const variants: Record<string, string> = {
  // Rule / generic status
  ACTIVE:      'bg-green-100 text-green-800',
  INACTIVE:    'bg-slate-100 text-slate-600',
  DRAFT:       'bg-yellow-100 text-yellow-800',
  SUSPENDED:   'bg-red-100 text-red-700',
  // RiskDecision
  APPROVE:     'bg-green-100 text-green-800',
  REVIEW:      'bg-amber-100 text-amber-800',
  DECLINE:     'bg-red-100 text-red-800',
  BLOCK:       'bg-red-200 text-red-900',
  // RiskLevel
  LOW:         'bg-green-50 text-green-700',
  MEDIUM:      'bg-yellow-50 text-yellow-700',
  HIGH:        'bg-orange-100 text-orange-800',
  CRITICAL:    'bg-red-100 text-red-800',
  // Environments
  PRODUCTION:  'bg-blue-100 text-blue-800',
  SANDBOX:     'bg-purple-100 text-purple-800',
};

export function StatusBadge({ value }: { value: string }) {
  return (
    <span
      className={clsx(
        'inline-flex items-center rounded-full px-2 py-0.5 text-xs font-medium',
        variants[value] ?? 'bg-slate-100 text-slate-700',
      )}
    >
      {value}
    </span>
  );
}

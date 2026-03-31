import { clsx } from 'clsx';

type MetricCardProps = {
  label: string;
  value: string;
  delta: string;
  accent: 'brand' | 'danger' | 'warning';
};

const accentClass: Record<MetricCardProps['accent'], string> = {
  brand: 'border-l-[var(--brand-primary)]',
  danger: 'border-l-[var(--danger)]',
  warning: 'border-l-[var(--warning)]',
};

export function MetricCard({ label, value, delta, accent }: MetricCardProps) {
  return (
    <section
      className={clsx(
        'enterprise-card-dense enterprise-metric-card row-span-4 h-[136px] border-l-[3px] !pl-[17px]',
        accentClass[accent],
      )}
    >
      <div className="enterprise-label">{label}</div>
      <div className="mt-3 text-[34px] font-extrabold leading-none tracking-[-0.02em] text-primary">{value}</div>
      <div className="mt-3 text-[13px] text-secondary">{delta}</div>
    </section>
  );
}

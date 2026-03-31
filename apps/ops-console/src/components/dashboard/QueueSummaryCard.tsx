import { StatusBadge } from '@/components/ui/StatusBadge';

type QueueSummaryCardProps = {
  label: string;
  value: string;
  updatedAt: string;
  status: 'OPEN' | 'IN_PROGRESS' | 'RESOLVED';
};

export function QueueSummaryCard({ label, value, updatedAt, status }: QueueSummaryCardProps) {
  return (
    <article className="flex h-[92px] flex-col justify-between rounded-[10px] border border-[#334155] bg-panel p-[14px] shadow-[0_0_0_1px_#1F2937] transition-all duration-150 ease-in hover:bg-[#243245]">
      <div className="flex items-start justify-between gap-2">
        <div>
          <div className="text-[13px] font-medium text-primary">{label}</div>
          <div className="mt-1 text-[11px] font-medium text-muted">{updatedAt}</div>
        </div>
        <StatusBadge value={status} />
      </div>
      <div className="text-[30px] font-bold leading-none text-primary">{value}</div>
    </article>
  );
}

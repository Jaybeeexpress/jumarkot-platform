import type { Route } from 'next';
import Link from 'next/link';
import { StatusBadge } from '@/components/ui/StatusBadge';

type DashboardTableProps = {
  rows: string[][];
  isLoading?: boolean;
};

function scoreTone(score: number): string {
  if (score >= 90) {
    return 'bg-[#EF4444]';
  }
  if (score >= 75) {
    return 'bg-[#F59E0B]';
  }
  return 'bg-[#3B82F6]';
}

export function DashboardTable({ rows, isLoading = false }: DashboardTableProps) {
  if (isLoading) {
    return (
      <div className="overflow-hidden rounded-[10px] border border-light bg-[#0F172A]">
        <div className="h-12 border-b border-light" />
        <div className="space-y-2 p-3">
          {Array.from({ length: 6 }).map((_, index) => (
            <div key={index} className="h-10 animate-pulse rounded bg-panel" />
          ))}
        </div>
      </div>
    );
  }

  if (rows.length === 0) {
    return (
      <div className="flex min-h-[320px] items-center justify-center rounded-[10px] border border-light bg-[#0F172A]">
        <div className="text-center">
          <div className="text-[13px] font-medium text-secondary">No alerts in the current window</div>
          <div className="mt-1 text-[11px] text-muted">Queue is clear. Refresh in a few minutes for new activity.</div>
        </div>
      </div>
    );
  }

  return (
    <div className="overflow-hidden rounded-[10px] border border-light bg-[#0F172A]">
      <table className="enterprise-table">
        <thead className="bg-[#0F172A]">
          <tr>
            <th>Alert ID</th>
            <th>Time</th>
            <th>Entity</th>
            <th>Event Type</th>
            <th>Risk Score</th>
            <th>Severity</th>
            <th>Status</th>
            <th>Assignee</th>
          </tr>
        </thead>
        <tbody>
          {rows.map((row) => {
            const score = Number(row[4]);
            return (
              <tr key={row[0]}>
                <td>
                  <Link href={`/alerts/${row[0]}` as Route} className="text-[#3B82F6] hover:text-[#60A5FA] hover:underline">
                    {row[0]}
                  </Link>
                </td>
                <td>{row[1]}</td>
                <td>{row[2]}</td>
                <td>{row[3]}</td>
                <td>
                  <div className="inline-flex items-center gap-2">
                    <span className={`h-2 w-2 rounded-full ${scoreTone(score)}`} />
                    <span className="font-semibold text-primary">{row[4]}</span>
                  </div>
                </td>
                <td><StatusBadge value={row[5]} /></td>
                <td><StatusBadge value={row[6]} /></td>
                <td>{row[7]}</td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}

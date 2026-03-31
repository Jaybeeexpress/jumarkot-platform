'use client';

import {
  Area,
  AreaChart,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts';

type TrendPoint = {
  day: string;
  pressure: number;
};

type DashboardTrendChartProps = {
  data: TrendPoint[];
  isLoading?: boolean;
};

function TrendTooltip({ active, payload, label }: { active?: boolean; payload?: Array<{ value: number }>; label?: string }) {
  if (!active || !payload || payload.length === 0) {
    return null;
  }

  return (
    <div className="rounded-[10px] border border-light bg-card px-3 py-2 shadow-[0_0_0_1px_#1F2937,0_8px_24px_rgba(0,0,0,0.18)]">
      <div className="text-[11px] font-medium text-muted">{label}</div>
      <div className="mt-1 text-[13px] font-semibold text-primary">{payload[0]?.value} review signals</div>
    </div>
  );
}

function ChartSkeleton() {
  return (
    <div className="flex h-[320px] items-end gap-2 rounded-[10px] border border-light bg-[#0F172A] p-4 shadow-[0_0_0_1px_#1F2937]">
      {Array.from({ length: 14 }).map((_, index) => (
        <div
          key={index}
          className="w-full animate-pulse rounded-t bg-panel"
          style={{ height: `${30 + ((index * 13) % 60)}%` }}
        />
      ))}
    </div>
  );
}

export function DashboardTrendChart({ data, isLoading = false }: DashboardTrendChartProps) {
  if (isLoading) {
    return <ChartSkeleton />;
  }

  if (data.length === 0) {
    return (
      <div className="flex h-[320px] items-center justify-center rounded-[10px] border border-light bg-[#0F172A] shadow-[0_0_0_1px_#1F2937]">
        <div className="text-center">
          <div className="text-[13px] font-medium text-secondary">No trend data available</div>
          <div className="mt-1 text-[11px] text-muted">New telemetry will appear after the next ingestion cycle.</div>
        </div>
      </div>
    );
  }

  return (
    <div className="h-[320px] rounded-[10px] border border-light bg-[#0F172A] p-3 shadow-[0_0_0_1px_#1F2937]">
      <ResponsiveContainer width="100%" height="100%">
        <AreaChart data={data} margin={{ top: 8, right: 12, left: 0, bottom: 0 }}>
          <defs>
            <linearGradient id="pressureArea" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stopColor="#3B82F6" stopOpacity={0.28} />
              <stop offset="100%" stopColor="#3B82F6" stopOpacity={0.04} />
            </linearGradient>
          </defs>
          <CartesianGrid stroke="#1F2937" strokeDasharray="3 3" vertical={false} />
          <XAxis
            dataKey="day"
            tickLine={false}
            axisLine={false}
            tick={{ fill: '#94A3B8', fontSize: 11, fontWeight: 500 }}
          />
          <YAxis
            tickLine={false}
            axisLine={false}
            width={34}
            tick={{ fill: '#94A3B8', fontSize: 11, fontWeight: 500 }}
          />
          <Tooltip cursor={{ stroke: '#334155', strokeWidth: 1 }} content={<TrendTooltip />} />
          <Area
            type="monotone"
            dataKey="pressure"
            stroke="#3B82F6"
            strokeWidth={2.25}
            fill="url(#pressureArea)"
            activeDot={{ r: 4, fill: '#3B82F6', stroke: '#111827', strokeWidth: 2 }}
          />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  );
}

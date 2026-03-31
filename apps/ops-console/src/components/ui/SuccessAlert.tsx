import { CheckCircle2 } from 'lucide-react';

export function SuccessAlert({ message }: { message: string }) {
  return (
    <div className="enterprise-panel flex items-start gap-3 border-emerald-500/30 bg-emerald-500/10 p-4">
      <CheckCircle2 className="mt-0.5 h-4 w-4 shrink-0 text-emerald-400" />
      <p className="text-sm text-emerald-200">{message}</p>
    </div>
  );
}

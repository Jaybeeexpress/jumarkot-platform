import { CheckCircle2 } from 'lucide-react';

export function SuccessAlert({ message }: { message: string }) {
  return (
    <div className="flex items-start gap-3 rounded-lg border border-emerald-200 bg-emerald-50 p-4">
      <CheckCircle2 className="mt-0.5 h-4 w-4 shrink-0 text-emerald-600" />
      <p className="text-sm text-emerald-800">{message}</p>
    </div>
  );
}

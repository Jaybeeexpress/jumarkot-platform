import { AlertCircle } from 'lucide-react';

export function ErrorAlert({ message }: { message: string }) {
  return (
    <div className="enterprise-panel flex items-start gap-3 border-rose-500/30 bg-rose-500/10 p-4">
      <AlertCircle className="mt-0.5 h-4 w-4 shrink-0 text-rose-400" />
      <p className="text-sm text-rose-200">{message}</p>
    </div>
  );
}

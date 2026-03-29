import { AlertCircle } from 'lucide-react';

export function ErrorAlert({ message }: { message: string }) {
  return (
    <div className="flex items-start gap-3 rounded-lg border border-red-200 bg-red-50 p-4">
      <AlertCircle className="mt-0.5 h-4 w-4 shrink-0 text-red-500" />
      <p className="text-sm text-red-700">{message}</p>
    </div>
  );
}

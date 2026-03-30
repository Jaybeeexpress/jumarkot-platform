'use client';

import { startTransition, useDeferredValue, useState } from 'react';

type SandboxResponse = {
  success: boolean;
  message?: string;
  errorCode?: string;
  data?: {
    eventId?: string;
    status?: string;
    acceptedAt?: string;
    duplicate?: boolean;
  };
};

type RecentEventsResponse = {
  success: boolean;
  message?: string;
  errorCode?: string;
  data?: RecentEvent[];
};

type RecentEvent = {
  eventId: string;
  idempotencyKey: string;
  eventType: string;
  entityId: string;
  entityType: string;
  ingestionStatus: string;
  deliveryStatus: string;
  acceptedAt: string;
  publishedAt?: string;
};

type ApiKeySummary = {
  id: string;
  maskedKey: string;
  name: string;
  environmentType: string;
  scopes: string[];
  active: boolean;
  createdAt: string;
};

type ApiKeyListResponse = {
  success: boolean;
  message?: string;
  errorCode?: string;
  data?: ApiKeySummary[];
};

type CreateKeyResponse = {
  success: boolean;
  message?: string;
  errorCode?: string;
  data?: {
    keyId: string;
    key: string;
    keyPrefix: string;
  };
};

type EventDraft = {
  idempotencyKey: string;
  eventType: string;
  entityId: string;
  entityType: string;
  occurredAt: string;
  properties: string;
  ipAddress: string;
  deviceId: string;
  sessionId: string;
  userAgent: string;
};

type ApiKeyDraft = {
  tenantId: string;
  environmentId: string;
  environmentType: 'SANDBOX' | 'PRODUCTION';
  name: string;
  scopes: string[];
};

const availableScopes = ['events:write', 'decisions:write', 'api-keys:read'];

const eventTypes = [
  'LOGIN_ATTEMPT',
  'LOGIN_SUCCESS',
  'TRANSACTION_CREATED',
  'TRANSACTION_FAILED',
  'PROFILE_UPDATED',
  'DOCUMENT_SUBMITTED',
];

function createDefaultDraft(): EventDraft {
  return {
    idempotencyKey: `evt_${crypto.randomUUID()}`,
    eventType: 'LOGIN_ATTEMPT',
    entityId: 'user_001',
    entityType: 'USER',
    occurredAt: new Date().toISOString(),
    properties: JSON.stringify(
      {
        channel: 'web',
        amount: 12500,
        riskBand: 'medium',
      },
      null,
      2,
    ),
    ipAddress: '203.0.113.42',
    deviceId: 'device_web_01',
    sessionId: 'session_7b6d1',
    userAgent: 'Mozilla/5.0 Jumarkot Sandbox',
  };
}

function buildCurlSnippet(apiKey: string, draft: EventDraft): string {
  const payload = {
    idempotencyKey: draft.idempotencyKey,
    eventType: draft.eventType,
    entityId: draft.entityId,
    entityType: draft.entityType,
    occurredAt: draft.occurredAt,
    properties: safeParseProperties(draft.properties),
    ipAddress: draft.ipAddress,
    deviceId: draft.deviceId,
    sessionId: draft.sessionId,
    userAgent: draft.userAgent,
  };

  return [
    'curl -X POST http://localhost:8084/v1/events \\',
    '  -H "Content-Type: application/json" \\',
    `  -H "X-API-Key: ${apiKey || 'jk_test_your_key_here'}" \\`,
    `  -d '${JSON.stringify(payload, null, 2)}'`,
  ].join('\n');
}

function safeParseProperties(value: string): Record<string, unknown> {
  try {
    const parsed = JSON.parse(value);
    return parsed && typeof parsed === 'object' ? parsed : {};
  } catch {
    return {};
  }
}

function createDefaultApiKeyDraft(): ApiKeyDraft {
  return {
    tenantId: '',
    environmentId: '',
    environmentType: 'SANDBOX',
    name: 'Developer Portal Sandbox Key',
    scopes: ['events:write'],
  };
}

export default function HomePage() {
  const [apiKey, setApiKey] = useState('');
  const [draft, setDraft] = useState<EventDraft>(() => createDefaultDraft());
  const [apiKeyDraft, setApiKeyDraft] = useState<ApiKeyDraft>(() => createDefaultApiKeyDraft());
  const [recentEvents, setRecentEvents] = useState<RecentEvent[]>([]);
  const [keys, setKeys] = useState<ApiKeySummary[]>([]);
  const [result, setResult] = useState<SandboxResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [keyError, setKeyError] = useState<string | null>(null);
  const [historyError, setHistoryError] = useState<string | null>(null);
  const [createdKey, setCreatedKey] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isLoadingKeys, setIsLoadingKeys] = useState(false);
  const [isCreatingKey, setIsCreatingKey] = useState(false);
  const [isLoadingHistory, setIsLoadingHistory] = useState(false);

  const requestPreview = useDeferredValue(buildCurlSnippet(apiKey, draft));

  async function loadRecentEvents() {
    if (!apiKey.trim()) {
      setHistoryError('Provide a sandbox API key before loading recent events.');
      return;
    }

    setIsLoadingHistory(true);
    setHistoryError(null);

    try {
      const response = await fetch(`/api/events?apiKey=${encodeURIComponent(apiKey)}&limit=10`, {
        cache: 'no-store',
      });
      const data = (await response.json()) as RecentEventsResponse;
      if (!response.ok || !data.success) {
        setHistoryError(data.message ?? 'Could not load recent events.');
        setRecentEvents([]);
        return;
      }
      setRecentEvents(data.data ?? []);
    } catch {
      setHistoryError('The developer portal could not load recent events.');
      setRecentEvents([]);
    } finally {
      setIsLoadingHistory(false);
    }
  }

  async function loadKeys() {
    if (!apiKeyDraft.tenantId.trim()) {
      setKeyError('Tenant ID is required to list API keys.');
      return;
    }

    setIsLoadingKeys(true);
    setKeyError(null);

    try {
      const response = await fetch(`/api/keys?tenantId=${encodeURIComponent(apiKeyDraft.tenantId)}`, {
        cache: 'no-store',
      });
      const data = (await response.json()) as ApiKeyListResponse;
      if (!response.ok || !data.success) {
        setKeyError(data.message ?? 'Could not load API keys.');
        setKeys([]);
        return;
      }
      setKeys(data.data ?? []);
    } catch {
      setKeyError('The developer portal could not load API keys.');
      setKeys([]);
    } finally {
      setIsLoadingKeys(false);
    }
  }

  async function createKey() {
    if (!apiKeyDraft.tenantId.trim() || !apiKeyDraft.environmentId.trim()) {
      setKeyError('Tenant ID and environment ID are required to create an API key.');
      return;
    }

    setIsCreatingKey(true);
    setKeyError(null);
    setCreatedKey(null);

    try {
      const response = await fetch('/api/keys', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(apiKeyDraft),
      });
      const data = (await response.json()) as CreateKeyResponse;

      if (!response.ok || !data.success || !data.data) {
        setKeyError(data.message ?? 'Could not create the API key.');
        return;
      }

      setCreatedKey(data.data.key);
      setApiKey(data.data.key);
      await loadKeys();
    } catch {
      setKeyError('The developer portal could not create the API key.');
    } finally {
      setIsCreatingKey(false);
    }
  }

  async function revokeKey(keyId: string) {
    if (!apiKeyDraft.tenantId.trim()) {
      setKeyError('Tenant ID is required to revoke an API key.');
      return;
    }

    setKeyError(null);
    try {
      const response = await fetch(
        `/api/keys/${encodeURIComponent(keyId)}?tenantId=${encodeURIComponent(apiKeyDraft.tenantId)}&reason=${encodeURIComponent('Revoked via developer portal')}`,
        {
          method: 'DELETE',
        },
      );
      if (!response.ok && response.status !== 204) {
        const data = (await response.json()) as { message?: string };
        setKeyError(data.message ?? 'Could not revoke the API key.');
        return;
      }
      await loadKeys();
    } catch {
      setKeyError('The developer portal could not revoke the API key.');
    }
  }

  async function submitSandboxEvent() {
    let properties: Record<string, unknown>;

    try {
      properties = JSON.parse(draft.properties);
    } catch {
      setError('Properties must be valid JSON before you can send the event.');
      setResult(null);
      return;
    }

    setIsSubmitting(true);
    setError(null);

    startTransition(async () => {
      try {
        const response = await fetch('/api/events', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            apiKey,
            event: {
              idempotencyKey: draft.idempotencyKey,
              eventType: draft.eventType,
              entityId: draft.entityId,
              entityType: draft.entityType,
              occurredAt: draft.occurredAt,
              properties,
              ipAddress: draft.ipAddress || undefined,
              deviceId: draft.deviceId || undefined,
              sessionId: draft.sessionId || undefined,
              userAgent: draft.userAgent || undefined,
            },
          }),
        });

        const data = (await response.json()) as SandboxResponse;
        setResult(data);
        setError(response.ok && data.success ? null : data.message ?? 'Sandbox request failed.');

        if (response.ok && data.success) {
          setDraft((current) => ({
            ...current,
            idempotencyKey: `evt_${crypto.randomUUID()}`,
          }));
          void loadRecentEvents();
        }
      } catch {
        setError('The developer portal could not reach the local API route.');
        setResult(null);
      } finally {
        setIsSubmitting(false);
      }
    });
  }

  return (
    <main className="px-5 py-6 md:px-8 md:py-10">
      <div className="mx-auto grid max-w-7xl gap-6 lg:grid-cols-[1.1fr_0.9fr]">
        <section className="overflow-hidden rounded-[2rem] border border-[var(--line)] bg-[var(--card)] shadow-[0_24px_80px_rgba(15,23,42,0.08)] backdrop-blur">
          <div className="border-b border-[var(--line)] px-6 py-5 md:px-8">
            <div className="flex flex-wrap items-center gap-3 text-sm uppercase tracking-[0.22em] text-slate-500">
              <span className="rounded-full bg-[rgba(15,118,110,0.12)] px-3 py-1 text-[11px] font-semibold text-[var(--brand-strong)]">
                Stage-3
              </span>
              <span>Developer Portal Sandbox</span>
            </div>
            <h1 className="mt-4 max-w-3xl text-4xl font-semibold leading-tight text-slate-900 md:text-6xl">
              Ship an event into Jumarkot before you wire the real client.
            </h1>
            <p className="mt-4 max-w-2xl text-base leading-7 text-slate-600 md:text-lg">
              This first Stage-3 slice exposes live event ingestion for developers: API-key scoped writes,
              idempotent acceptance, and a browser sandbox that mirrors the production contract.
            </p>
          </div>

          <div className="grid gap-6 px-6 py-6 md:px-8 lg:grid-cols-[0.95fr_1.05fr]">
            <div className="space-y-4">
              <div className="rounded-[1.5rem] border border-[var(--line)] bg-[var(--card-strong)] p-5">
                <div className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-500">Scope</div>
                <ul className="mt-4 space-y-3 text-sm leading-6 text-slate-700">
                  <li>Durable event acceptance in event-ingestion-service with tenant-scoped idempotency.</li>
                  <li>Kafka fan-out on accepted events with persisted delivery state tracking.</li>
                  <li>Recent event history and API key workflows exposed directly in the developer portal.</li>
                </ul>
              </div>

              <div className="rounded-[1.5rem] border border-[var(--line)] bg-slate-950 p-5 text-slate-100">
                <div className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">What to provision</div>
                <ol className="mt-4 space-y-3 text-sm leading-6 text-slate-300">
                  <li>Set identity and ingestion service URLs in the environment for the portal server routes.</li>
                  <li>Create a sandbox API key with <span className="font-mono">events:write</span> directly in the portal.</li>
                  <li>Submit a sample event and refresh the recent-event history to confirm delivery state.</li>
                </ol>
              </div>
            </div>

            <div className="rounded-[1.5rem] border border-[var(--line)] bg-[var(--card-strong)] p-5">
              <div className="flex flex-wrap items-center justify-between gap-3">
                <div>
                  <div className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-500">Live preview</div>
                  <h2 className="mt-2 text-2xl font-semibold text-slate-900">Sandbox curl</h2>
                </div>
                <button
                  type="button"
                  onClick={() => setDraft(createDefaultDraft())}
                  className="rounded-full border border-[var(--line)] px-4 py-2 text-sm font-medium text-slate-700 transition hover:border-slate-400 hover:bg-white"
                >
                  Reset sample
                </button>
              </div>
              <pre className="mt-4 overflow-x-auto rounded-[1.25rem] bg-slate-950 p-4 text-xs leading-6 text-emerald-200">
                <code className="font-mono">{requestPreview}</code>
              </pre>
            </div>
          </div>
        </section>

        <section className="rounded-[2rem] border border-[var(--line)] bg-[var(--card)] p-6 shadow-[0_24px_80px_rgba(15,23,42,0.08)] backdrop-blur md:p-8">
          <div className="grid gap-6 xl:grid-cols-[0.95fr_1.05fr]">
            <div className="space-y-6">
              <div className="flex items-center justify-between gap-3">
                <div>
                  <div className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-500">Key management</div>
                  <h2 className="mt-2 text-3xl font-semibold text-slate-900">Create and manage API keys</h2>
                </div>
                <div className="rounded-full bg-[rgba(15,118,110,0.12)] px-3 py-1 text-xs font-semibold uppercase tracking-[0.18em] text-[var(--brand-strong)]">
                  identity-access-service
                </div>
              </div>

              <div className="grid gap-4 md:grid-cols-2">
                <Field label="Tenant ID" value={apiKeyDraft.tenantId} onChange={(value) => setApiKeyDraft((current) => ({ ...current, tenantId: value }))} />
                <Field label="Environment ID" value={apiKeyDraft.environmentId} onChange={(value) => setApiKeyDraft((current) => ({ ...current, environmentId: value }))} />
                <SelectField label="Environment type" value={apiKeyDraft.environmentType} onChange={(value) => setApiKeyDraft((current) => ({ ...current, environmentType: value as 'SANDBOX' | 'PRODUCTION' }))} options={['SANDBOX', 'PRODUCTION']} />
                <Field label="Key name" value={apiKeyDraft.name} onChange={(value) => setApiKeyDraft((current) => ({ ...current, name: value }))} />
              </div>

              <div>
                <div className="mb-2 block text-sm font-medium text-slate-700">Scopes</div>
                <div className="flex flex-wrap gap-2">
                  {availableScopes.map((scope) => {
                    const selected = apiKeyDraft.scopes.includes(scope);
                    return (
                      <button
                        key={scope}
                        type="button"
                        onClick={() =>
                          setApiKeyDraft((current) => ({
                            ...current,
                            scopes: selected
                              ? current.scopes.filter((candidate) => candidate !== scope)
                              : [...current.scopes, scope],
                          }))
                        }
                        className={`rounded-full px-4 py-2 text-sm font-medium transition ${selected ? 'bg-[var(--brand-strong)] text-white' : 'border border-[var(--line)] bg-white text-slate-700 hover:bg-slate-50'}`}
                      >
                        {scope}
                      </button>
                    );
                  })}
                </div>
              </div>

              <div className="flex flex-wrap gap-3">
                <button
                  type="button"
                  onClick={createKey}
                  disabled={isCreatingKey}
                  className="rounded-[1.25rem] bg-[var(--brand-strong)] px-5 py-3 text-sm font-semibold uppercase tracking-[0.16em] text-white transition hover:bg-[var(--brand)] disabled:cursor-not-allowed disabled:opacity-60"
                >
                  {isCreatingKey ? 'Creating key...' : 'Create key'}
                </button>
                <button
                  type="button"
                  onClick={loadKeys}
                  disabled={isLoadingKeys}
                  className="rounded-[1.25rem] border border-[var(--line)] px-5 py-3 text-sm font-semibold uppercase tracking-[0.16em] text-slate-700 transition hover:bg-white disabled:cursor-not-allowed disabled:opacity-60"
                >
                  {isLoadingKeys ? 'Loading...' : 'Load keys'}
                </button>
              </div>

              {(keyError || createdKey) && (
                <div className={`rounded-[1.5rem] border p-4 ${keyError ? 'border-rose-200 bg-rose-50 text-rose-800' : 'border-emerald-200 bg-emerald-50 text-emerald-900'}`}>
                  <div className="text-xs font-semibold uppercase tracking-[0.18em]">
                    {keyError ? 'API key workflow failed' : 'API key created'}
                  </div>
                  <p className="mt-2 text-sm leading-6">{keyError ?? 'The raw key is shown once and was copied into the sandbox field below.'}</p>
                  {createdKey && (
                    <pre className="mt-3 overflow-x-auto rounded-xl bg-slate-950 p-3 text-xs text-emerald-200">
                      <code className="font-mono">{createdKey}</code>
                    </pre>
                  )}
                </div>
              )}

              <div className="rounded-[1.5rem] border border-[var(--line)] bg-[var(--card-strong)] p-4">
                <div className="flex items-center justify-between gap-3">
                  <div>
                    <div className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-500">Current keys</div>
                    <h3 className="mt-2 text-xl font-semibold text-slate-900">Tenant API keys</h3>
                  </div>
                  <div className="text-sm text-slate-500">{keys.length} loaded</div>
                </div>

                <div className="mt-4 space-y-3">
                  {keys.length === 0 ? (
                    <div className="rounded-xl border border-dashed border-[var(--line)] px-4 py-6 text-sm text-slate-500">
                      No API keys loaded yet. Provide a tenant ID and load keys.
                    </div>
                  ) : (
                    keys.map((key) => (
                      <div key={key.id} className="rounded-xl border border-[var(--line)] bg-white px-4 py-4">
                        <div className="flex flex-wrap items-start justify-between gap-3">
                          <div>
                            <div className="text-sm font-semibold text-slate-900">{key.name}</div>
                            <div className="mt-1 font-mono text-xs text-slate-500">{key.maskedKey}</div>
                            <div className="mt-2 flex flex-wrap gap-2">
                              {key.scopes.map((scope) => (
                                <span key={scope} className="rounded-full bg-slate-100 px-2.5 py-1 text-[11px] font-semibold uppercase tracking-[0.12em] text-slate-600">
                                  {scope}
                                </span>
                              ))}
                            </div>
                          </div>
                          <button
                            type="button"
                            onClick={() => revokeKey(key.id)}
                            className="rounded-full border border-rose-200 px-3 py-1.5 text-xs font-semibold uppercase tracking-[0.14em] text-rose-700 transition hover:bg-rose-50"
                          >
                            Revoke
                          </button>
                        </div>
                        <div className="mt-3 text-xs text-slate-500">
                          {key.environmentType} · {key.active ? 'Active' : 'Inactive'} · Created {new Date(key.createdAt).toLocaleString()}
                        </div>
                      </div>
                    ))
                  )}
                </div>
              </div>
            </div>

            <div>
              <div className="flex items-center justify-between gap-3">
                <div>
                  <div className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-500">Event sandbox</div>
                  <h2 className="mt-2 text-3xl font-semibold text-slate-900">Send a real payload</h2>
                </div>
                <div className="rounded-full bg-[rgba(245,158,11,0.14)] px-3 py-1 text-xs font-semibold uppercase tracking-[0.18em] text-amber-700">
                  POST /v1/events
                </div>
              </div>

              <div className="mt-6 space-y-4">
                <label className="block">
                  <span className="mb-2 block text-sm font-medium text-slate-700">Sandbox API key</span>
                  <input
                    value={apiKey}
                    onChange={(event) => setApiKey(event.target.value)}
                    placeholder="jk_test_..."
                    className="w-full rounded-2xl border border-[var(--line)] bg-white px-4 py-3 text-sm text-slate-900 outline-none transition focus:border-[var(--brand)] focus:ring-2 focus:ring-[rgba(15,118,110,0.12)]"
                  />
                </label>

                <div className="grid gap-4 md:grid-cols-2">
                  <Field label="Idempotency key" value={draft.idempotencyKey} onChange={(value) => setDraft((current) => ({ ...current, idempotencyKey: value }))} />
                  <Field label="Occurred at" value={draft.occurredAt} onChange={(value) => setDraft((current) => ({ ...current, occurredAt: value }))} />
                  <SelectField label="Event type" value={draft.eventType} onChange={(value) => setDraft((current) => ({ ...current, eventType: value }))} options={eventTypes} />
                  <Field label="Entity type" value={draft.entityType} onChange={(value) => setDraft((current) => ({ ...current, entityType: value }))} />
                  <Field label="Entity ID" value={draft.entityId} onChange={(value) => setDraft((current) => ({ ...current, entityId: value }))} />
                  <Field label="IP address" value={draft.ipAddress} onChange={(value) => setDraft((current) => ({ ...current, ipAddress: value }))} />
                  <Field label="Device ID" value={draft.deviceId} onChange={(value) => setDraft((current) => ({ ...current, deviceId: value }))} />
                  <Field label="Session ID" value={draft.sessionId} onChange={(value) => setDraft((current) => ({ ...current, sessionId: value }))} />
                </div>

                <Field label="User agent" value={draft.userAgent} onChange={(value) => setDraft((current) => ({ ...current, userAgent: value }))} />

                <label className="block">
                  <span className="mb-2 block text-sm font-medium text-slate-700">Properties JSON</span>
                  <textarea
                    value={draft.properties}
                    onChange={(event) => setDraft((current) => ({ ...current, properties: event.target.value }))}
                    rows={10}
                    className="font-mono w-full rounded-[1.5rem] border border-[var(--line)] bg-white px-4 py-3 text-sm text-slate-900 outline-none transition focus:border-[var(--brand)] focus:ring-2 focus:ring-[rgba(15,118,110,0.12)]"
                  />
                </label>

                <div className="flex flex-wrap gap-3">
                  <button
                    type="button"
                    onClick={submitSandboxEvent}
                    disabled={isSubmitting}
                    className="flex-1 rounded-[1.5rem] bg-[var(--brand-strong)] px-5 py-4 text-sm font-semibold uppercase tracking-[0.16em] text-white transition hover:bg-[var(--brand)] disabled:cursor-not-allowed disabled:opacity-60"
                  >
                    {isSubmitting ? 'Submitting event...' : 'Submit sandbox event'}
                  </button>
                  <button
                    type="button"
                    onClick={loadRecentEvents}
                    disabled={isLoadingHistory}
                    className="rounded-[1.5rem] border border-[var(--line)] px-5 py-4 text-sm font-semibold uppercase tracking-[0.16em] text-slate-700 transition hover:bg-white disabled:cursor-not-allowed disabled:opacity-60"
                  >
                    {isLoadingHistory ? 'Refreshing...' : 'Refresh history'}
                  </button>
                </div>

                {(error || result) && (
                  <div className={`rounded-[1.5rem] border p-4 ${error ? 'border-rose-200 bg-rose-50 text-rose-800' : 'border-emerald-200 bg-emerald-50 text-emerald-900'}`}>
                    <div className="text-xs font-semibold uppercase tracking-[0.18em]">
                      {error ? 'Submission failed' : 'Submission accepted'}
                    </div>
                    <p className="mt-2 text-sm leading-6">{error ?? result?.message ?? 'Event accepted by the ingestion service.'}</p>
                    {result?.data && (
                      <dl className="mt-4 grid gap-2 text-sm md:grid-cols-2">
                        <div>
                          <dt className="text-slate-500">Event ID</dt>
                          <dd className="font-mono text-[13px]">{result.data.eventId}</dd>
                        </div>
                        <div>
                          <dt className="text-slate-500">Status</dt>
                          <dd>{result.data.status}</dd>
                        </div>
                        <div>
                          <dt className="text-slate-500">Accepted at</dt>
                          <dd>{result.data.acceptedAt}</dd>
                        </div>
                        <div>
                          <dt className="text-slate-500">Duplicate</dt>
                          <dd>{String(result.data.duplicate)}</dd>
                        </div>
                      </dl>
                    )}
                  </div>
                )}

                <div className="rounded-[1.5rem] border border-[var(--line)] bg-[var(--card-strong)] p-4">
                  <div className="flex items-center justify-between gap-3">
                    <div>
                      <div className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-500">Recent ingestions</div>
                      <h3 className="mt-2 text-xl font-semibold text-slate-900">Latest tenant events</h3>
                    </div>
                    <div className="text-sm text-slate-500">{recentEvents.length} loaded</div>
                  </div>

                  {historyError && (
                    <div className="mt-4 rounded-xl border border-rose-200 bg-rose-50 px-4 py-3 text-sm text-rose-800">
                      {historyError}
                    </div>
                  )}

                  <div className="mt-4 space-y-3">
                    {recentEvents.length === 0 ? (
                      <div className="rounded-xl border border-dashed border-[var(--line)] px-4 py-6 text-sm text-slate-500">
                        No recent events loaded yet. Submit an event or refresh history with a valid API key.
                      </div>
                    ) : (
                      recentEvents.map((event) => (
                        <div key={event.eventId} className="rounded-xl border border-[var(--line)] bg-white px-4 py-4">
                          <div className="flex flex-wrap items-start justify-between gap-3">
                            <div>
                              <div className="text-sm font-semibold text-slate-900">{event.eventType}</div>
                              <div className="mt-1 font-mono text-xs text-slate-500">{event.eventId}</div>
                            </div>
                            <div className={`rounded-full px-3 py-1 text-[11px] font-semibold uppercase tracking-[0.14em] ${event.deliveryStatus === 'PUBLISHED' ? 'bg-emerald-100 text-emerald-800' : event.deliveryStatus === 'FAILED' ? 'bg-rose-100 text-rose-800' : 'bg-amber-100 text-amber-800'}`}>
                              {event.deliveryStatus}
                            </div>
                          </div>
                          <div className="mt-3 grid gap-2 text-sm text-slate-600 md:grid-cols-2">
                            <div>Entity: {event.entityType} / {event.entityId}</div>
                            <div>Accepted: {new Date(event.acceptedAt).toLocaleString()}</div>
                            <div className="font-mono text-xs">Idempotency: {event.idempotencyKey}</div>
                            <div>Published: {event.publishedAt ? new Date(event.publishedAt).toLocaleString() : 'Pending'}</div>
                          </div>
                        </div>
                      ))
                    )}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>
    </main>
  );
}

function Field({
  label,
  value,
  onChange,
}: {
  label: string;
  value: string;
  onChange: (value: string) => void;
}) {
  return (
    <label className="block">
      <span className="mb-2 block text-sm font-medium text-slate-700">{label}</span>
      <input
        value={value}
        onChange={(event) => onChange(event.target.value)}
        className="w-full rounded-2xl border border-[var(--line)] bg-white px-4 py-3 text-sm text-slate-900 outline-none transition focus:border-[var(--brand)] focus:ring-2 focus:ring-[rgba(15,118,110,0.12)]"
      />
    </label>
  );
}

function SelectField({
  label,
  value,
  onChange,
  options,
}: {
  label: string;
  value: string;
  onChange: (value: string) => void;
  options: string[];
}) {
  return (
    <label className="block">
      <span className="mb-2 block text-sm font-medium text-slate-700">{label}</span>
      <select
        value={value}
        onChange={(event) => onChange(event.target.value)}
        className="w-full rounded-2xl border border-[var(--line)] bg-white px-4 py-3 text-sm text-slate-900 outline-none transition focus:border-[var(--brand)] focus:ring-2 focus:ring-[rgba(15,118,110,0.12)]"
      >
        {options.map((option) => (
          <option key={option} value={option}>
            {option}
          </option>
        ))}
      </select>
    </label>
  );
}

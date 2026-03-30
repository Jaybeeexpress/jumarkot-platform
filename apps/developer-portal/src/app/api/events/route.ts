import { NextRequest, NextResponse } from 'next/server';

const EVENT_INGESTION_SERVICE_URL = process.env.EVENT_INGESTION_SERVICE_URL;

function jsonParseOrFallback(value: string, fallback: unknown) {
  try {
    return value ? JSON.parse(value) : fallback;
  } catch {
    return fallback;
  }
}

export async function GET(request: NextRequest) {
  if (!EVENT_INGESTION_SERVICE_URL) {
    return NextResponse.json(
      {
        success: false,
        errorCode: 'MISSING_SERVICE_CONFIG',
        message: 'EVENT_INGESTION_SERVICE_URL is not set in the environment.',
      },
      { status: 503 },
    );
  }

  const searchParams = request.nextUrl.searchParams;
  const apiKey = searchParams.get('apiKey')?.trim() ?? '';
  const limit = searchParams.get('limit')?.trim() ?? '10';

  if (!apiKey) {
    return NextResponse.json(
      {
        success: false,
        errorCode: 'API_KEY_REQUIRED',
        message: 'Provide a valid API key to load recent events.',
      },
      { status: 400 },
    );
  }

  try {
    const upstream = await fetch(`${EVENT_INGESTION_SERVICE_URL}/v1/events?limit=${encodeURIComponent(limit)}`, {
      headers: {
        'X-API-Key': apiKey,
      },
      cache: 'no-store',
    });

    const responseText = await upstream.text();
    const data = jsonParseOrFallback(responseText, { success: upstream.ok });
    return NextResponse.json(data, { status: upstream.status });
  } catch {
    return NextResponse.json(
      {
        success: false,
        errorCode: 'UPSTREAM_ERROR',
        message: 'Could not reach event-ingestion-service.',
      },
      { status: 502 },
    );
  }
}

export async function POST(request: NextRequest) {
  if (!EVENT_INGESTION_SERVICE_URL) {
    return NextResponse.json(
      {
        success: false,
        errorCode: 'MISSING_SERVICE_CONFIG',
        message: 'EVENT_INGESTION_SERVICE_URL is not set in the environment.',
      },
      { status: 503 },
    );
  }

  let body: unknown;
  try {
    body = await request.json();
  } catch {
    return NextResponse.json(
      {
        success: false,
        errorCode: 'INVALID_JSON_PAYLOAD',
        message: 'Request body must be valid JSON.',
      },
      { status: 400 },
    );
  }

  const candidate = body as { apiKey?: unknown; event?: unknown };
  const apiKey = typeof candidate.apiKey === 'string' ? candidate.apiKey.trim() : '';

  if (!apiKey) {
    return NextResponse.json(
      {
        success: false,
        errorCode: 'API_KEY_REQUIRED',
        message: 'Provide a valid API key to submit a sandbox event.',
      },
      { status: 400 },
    );
  }

  if (!candidate.event || typeof candidate.event !== 'object') {
    return NextResponse.json(
      {
        success: false,
        errorCode: 'INVALID_JSON_PAYLOAD',
        message: 'Event payload is required and must be a JSON object.',
      },
      { status: 400 },
    );
  }

  try {
    const upstream = await fetch(`${EVENT_INGESTION_SERVICE_URL}/v1/events`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-API-Key': apiKey,
      },
      body: JSON.stringify(candidate.event),
    });

    const responseText = await upstream.text();
    const data = jsonParseOrFallback(responseText, { success: upstream.ok });
    return NextResponse.json(data, { status: upstream.status });
  } catch {
    return NextResponse.json(
      {
        success: false,
        errorCode: 'UPSTREAM_ERROR',
        message: 'Could not reach event-ingestion-service.',
      },
      { status: 502 },
    );
  }
}
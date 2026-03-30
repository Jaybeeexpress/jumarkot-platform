import { NextRequest, NextResponse } from 'next/server';

const DECISION_SERVICE_URL = process.env.DECISION_SERVICE_URL;
const DECISION_API_KEY = process.env.DECISION_API_KEY ?? '';

export async function POST(request: NextRequest) {
  if (!DECISION_SERVICE_URL) {
    return NextResponse.json(
      {
        success: false,
        errorCode: 'MISSING_SERVICE_CONFIG',
        message: 'DECISION_SERVICE_URL is not set in .env.local.',
      },
      { status: 503 },
    );
  }

  if (!DECISION_API_KEY) {
    return NextResponse.json(
      {
        success: false,
        errorCode: 'NO_API_KEY',
        message:
          'DECISION_API_KEY is not set. Add it to .env.local — ' +
          'provision a key via POST /v1/api-keys on identity-access-service (port 8081).',
      },
      { status: 503 },
    );
  }

  try {
    const body = await request.json();
    const upstream = await fetch(`${DECISION_SERVICE_URL}/v1/decisions`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-API-Key': DECISION_API_KEY,
      },
      body: JSON.stringify(body),
    });
    const data = await upstream.json();
    return NextResponse.json(data, { status: upstream.status });
  } catch {
    return NextResponse.json(
      {
        success: false,
        errorCode: 'UPSTREAM_ERROR',
        message: 'Could not reach decision-engine-service.',
      },
      { status: 502 },
    );
  }
}

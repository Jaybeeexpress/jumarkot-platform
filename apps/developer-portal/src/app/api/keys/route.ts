import { NextRequest, NextResponse } from 'next/server';

const IDENTITY_SERVICE_URL = process.env.IDENTITY_SERVICE_URL;
const IDENTITY_SERVICE_USER = process.env.IDENTITY_SERVICE_USER;
const IDENTITY_SERVICE_PASSWORD = process.env.IDENTITY_SERVICE_PASSWORD;

function basicAuth() {
  return 'Basic ' + Buffer.from(`${IDENTITY_SERVICE_USER}:${IDENTITY_SERVICE_PASSWORD}`).toString('base64');
}

function missingConfigResponse() {
  return NextResponse.json(
    {
      success: false,
      errorCode: 'MISSING_SERVICE_CONFIG',
      message:
        'Identity service config is incomplete. Set IDENTITY_SERVICE_URL, IDENTITY_SERVICE_USER, and IDENTITY_SERVICE_PASSWORD in the environment.',
    },
    { status: 503 },
  );
}

function jsonParseOrFallback(value: string, fallback: unknown) {
  try {
    return value ? JSON.parse(value) : fallback;
  } catch {
    return fallback;
  }
}

export async function GET(request: NextRequest) {
  if (!IDENTITY_SERVICE_URL || !IDENTITY_SERVICE_USER || !IDENTITY_SERVICE_PASSWORD) {
    return missingConfigResponse();
  }

  const tenantId = request.nextUrl.searchParams.get('tenantId')?.trim() ?? '';
  if (!tenantId) {
    return NextResponse.json(
      {
        success: false,
        errorCode: 'INVALID_REQUEST',
        message: 'tenantId is required to list API keys.',
      },
      { status: 400 },
    );
  }

  try {
    const upstream = await fetch(`${IDENTITY_SERVICE_URL}/v1/api-keys?tenantId=${encodeURIComponent(tenantId)}`, {
      headers: {
        Authorization: basicAuth(),
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
        message: 'Could not reach identity-access-service.',
      },
      { status: 502 },
    );
  }
}

export async function POST(request: NextRequest) {
  if (!IDENTITY_SERVICE_URL || !IDENTITY_SERVICE_USER || !IDENTITY_SERVICE_PASSWORD) {
    return missingConfigResponse();
  }

  try {
    const body = await request.json();
    const upstream = await fetch(`${IDENTITY_SERVICE_URL}/v1/api-keys`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: basicAuth(),
      },
      body: JSON.stringify(body),
    });
    const responseText = await upstream.text();
    const data = jsonParseOrFallback(responseText, { success: upstream.ok });
    return NextResponse.json(data, { status: upstream.status });
  } catch {
    return NextResponse.json(
      {
        success: false,
        errorCode: 'UPSTREAM_ERROR',
        message: 'Could not reach identity-access-service.',
      },
      { status: 502 },
    );
  }
}
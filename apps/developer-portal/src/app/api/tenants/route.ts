import { NextRequest, NextResponse } from 'next/server';

const TENANT_SERVICE_URL = process.env.TENANT_SERVICE_URL;
const TENANT_SERVICE_USER = process.env.TENANT_SERVICE_USER;
const TENANT_SERVICE_PASSWORD = process.env.TENANT_SERVICE_PASSWORD;

function basicAuth() {
  return 'Basic ' + Buffer.from(`${TENANT_SERVICE_USER}:${TENANT_SERVICE_PASSWORD}`).toString('base64');
}

function missingConfigResponse() {
  return NextResponse.json(
    {
      success: false,
      errorCode: 'MISSING_SERVICE_CONFIG',
      message:
        'Tenant service config is incomplete. Set TENANT_SERVICE_URL, TENANT_SERVICE_USER, and TENANT_SERVICE_PASSWORD in the environment.',
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
  if (!TENANT_SERVICE_URL || !TENANT_SERVICE_USER || !TENANT_SERVICE_PASSWORD) {
    return missingConfigResponse();
  }

  const limit = request.nextUrl.searchParams.get('limit')?.trim() ?? '25';

  try {
    const upstream = await fetch(`${TENANT_SERVICE_URL}/v1/tenants?limit=${encodeURIComponent(limit)}`, {
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
        message: 'Could not reach tenant-service.',
      },
      { status: 502 },
    );
  }
}

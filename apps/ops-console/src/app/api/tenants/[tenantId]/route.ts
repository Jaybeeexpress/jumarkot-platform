import { NextRequest, NextResponse } from 'next/server';

const TENANT_SERVICE_URL = process.env.TENANT_SERVICE_URL;
const TENANT_USER = process.env.TENANT_SERVICE_USER;
const TENANT_PASS = process.env.TENANT_SERVICE_PASSWORD;

function basicAuth() {
  return (
    'Basic ' +
    Buffer.from(`${TENANT_USER}:${TENANT_PASS}`).toString('base64')
  );
}

export async function GET(
  _request: NextRequest,
  { params }: { params: { tenantId: string } },
) {
  if (!TENANT_SERVICE_URL || !TENANT_USER || !TENANT_PASS) {
    return NextResponse.json(
      {
        success: false,
        errorCode: 'MISSING_SERVICE_CONFIG',
        message:
          'Tenant service config is incomplete. Set TENANT_SERVICE_URL, TENANT_SERVICE_USER, and TENANT_SERVICE_PASSWORD in .env.local.',
      },
      { status: 503 },
    );
  }

  try {
    const upstream = await fetch(
      `${TENANT_SERVICE_URL}/v1/tenants/${params.tenantId}`,
      { headers: { Authorization: basicAuth() } },
    );
    const data = await upstream.json();
    return NextResponse.json(data, { status: upstream.status });
  } catch {
    return NextResponse.json(
      { success: false, errorCode: 'UPSTREAM_ERROR', message: 'Could not reach tenant-service.' },
      { status: 502 },
    );
  }
}

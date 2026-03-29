import { NextRequest, NextResponse } from 'next/server';

const TENANT_SERVICE_URL =
  process.env.TENANT_SERVICE_URL ?? 'http://localhost:8082';
const TENANT_USER = process.env.TENANT_SERVICE_USER ?? 'admin';
const TENANT_PASS = process.env.TENANT_SERVICE_PASSWORD ?? 'changeme';

function basicAuth() {
  return (
    'Basic ' +
    Buffer.from(`${TENANT_USER}:${TENANT_PASS}`).toString('base64')
  );
}

export async function POST(request: NextRequest) {
  try {
    const body = await request.json();
    const upstream = await fetch(`${TENANT_SERVICE_URL}/v1/tenants`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: basicAuth(),
      },
      body: JSON.stringify(body),
    });
    const data = await upstream.json();
    return NextResponse.json(data, { status: upstream.status });
  } catch {
    return NextResponse.json(
      { success: false, errorCode: 'UPSTREAM_ERROR', message: 'Could not reach tenant-service.' },
      { status: 502 },
    );
  }
}

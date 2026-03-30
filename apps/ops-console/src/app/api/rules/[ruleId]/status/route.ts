import { NextRequest, NextResponse } from 'next/server';

const RULES_SERVICE_URL = process.env.RULES_SERVICE_URL;
const RULES_USER = process.env.RULES_SERVICE_USER;
const RULES_PASS = process.env.RULES_SERVICE_PASSWORD;

function basicAuth() {
  return (
    'Basic ' +
    Buffer.from(`${RULES_USER}:${RULES_PASS}`).toString('base64')
  );
}

export async function PUT(
  request: NextRequest,
  { params }: { params: { ruleId: string } },
) {
  if (!RULES_SERVICE_URL || !RULES_USER || !RULES_PASS) {
    return NextResponse.json(
      {
        success: false,
        errorCode: 'MISSING_SERVICE_CONFIG',
        message:
          'Rules service config is incomplete. Set RULES_SERVICE_URL, RULES_SERVICE_USER, and RULES_SERVICE_PASSWORD in .env.local.',
      },
      { status: 503 },
    );
  }

  const query = new URL(request.url).searchParams.toString();
  try {
    const upstream = await fetch(
      `${RULES_SERVICE_URL}/v1/rules/${params.ruleId}/status${query ? '?' + query : ''}`,
      { method: 'PUT', headers: { Authorization: basicAuth() } },
    );
    if (upstream.status === 204) {
      return new NextResponse(null, { status: 204 });
    }
    const data = await upstream.json();
    return NextResponse.json(data, { status: upstream.status });
  } catch {
    return NextResponse.json(
      { success: false, errorCode: 'UPSTREAM_ERROR', message: 'Could not reach rules-service.' },
      { status: 502 },
    );
  }
}

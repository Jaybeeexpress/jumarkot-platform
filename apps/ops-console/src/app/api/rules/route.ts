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

function missingConfigResponse() {
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

export async function GET(request: NextRequest) {
  if (!RULES_SERVICE_URL || !RULES_USER || !RULES_PASS) {
    return missingConfigResponse();
  }
  const query = new URL(request.url).searchParams.toString();
  try {
    const upstream = await fetch(
      `${RULES_SERVICE_URL}/v1/rules${query ? '?' + query : ''}`,
      { headers: { Authorization: basicAuth() } },
    );
    const data = await upstream.json();
    return NextResponse.json(data, { status: upstream.status });
  } catch {
    return NextResponse.json(
      { success: false, errorCode: 'UPSTREAM_ERROR', message: 'Could not reach rules-service.' },
      { status: 502 },
    );
  }
}

export async function POST(request: NextRequest) {
  if (!RULES_SERVICE_URL || !RULES_USER || !RULES_PASS) {
    return missingConfigResponse();
  }
  try {
    const body = await request.json();
    const upstream = await fetch(`${RULES_SERVICE_URL}/v1/rules`, {
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
      { success: false, errorCode: 'UPSTREAM_ERROR', message: 'Could not reach rules-service.' },
      { status: 502 },
    );
  }
}

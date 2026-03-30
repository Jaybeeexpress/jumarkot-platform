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

export async function DELETE(
  request: NextRequest,
  { params }: { params: { keyId: string } },
) {
  if (!IDENTITY_SERVICE_URL || !IDENTITY_SERVICE_USER || !IDENTITY_SERVICE_PASSWORD) {
    return missingConfigResponse();
  }

  const tenantId = request.nextUrl.searchParams.get('tenantId')?.trim() ?? '';
  const reason = request.nextUrl.searchParams.get('reason')?.trim() ?? 'Revoked via developer portal';

  if (!tenantId) {
    return NextResponse.json(
      {
        success: false,
        errorCode: 'INVALID_REQUEST',
        message: 'tenantId is required to revoke an API key.',
      },
      { status: 400 },
    );
  }

  try {
    const upstream = await fetch(
      `${IDENTITY_SERVICE_URL}/v1/api-keys/${encodeURIComponent(params.keyId)}?tenantId=${encodeURIComponent(tenantId)}&reason=${encodeURIComponent(reason)}`,
      {
        method: 'DELETE',
        headers: {
          Authorization: basicAuth(),
        },
      },
    );

    if (upstream.status === 204) {
      return new NextResponse(null, { status: 204 });
    }

    const responseText = await upstream.text();
    const data = responseText ? JSON.parse(responseText) : { success: upstream.ok };
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
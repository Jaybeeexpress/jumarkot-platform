param(
    [string]$TenantBaseUrl = "http://localhost:8082",
    [string]$IdentityBaseUrl = "http://localhost:8081",
    [string]$RulesBaseUrl = "http://localhost:8087",
    [string]$DecisionBaseUrl = "http://localhost:8085",

    # Basic auth credentials matching spring.security.user defaults in each service's application.yml.
    # Override via -TenantBasicPassword etc. when running against non-default environments.
    [string]$TenantBasicUser = "admin",
    [string]$TenantBasicPassword = "changeme",
    [string]$IdentityBasicUser = "admin",
    [string]$IdentityBasicPassword = "changeme",
    [string]$RulesBasicUser = "admin",
    [string]$RulesBasicPassword = "changeme"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function New-BasicAuthHeader {
    param([string]$User, [string]$Password)

    if ([string]::IsNullOrWhiteSpace($User) -or [string]::IsNullOrWhiteSpace($Password)) {
        return @{}
    }

    $pair = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("$User`:$Password"))
    return @{ Authorization = "Basic $pair" }
}

function Write-Step {
    param([string]$Message)
    Write-Host "`n==> $Message" -ForegroundColor Cyan
}

function Invoke-ApiJson {
    param(
        [ValidateSet("GET", "POST", "PUT", "DELETE")]
        [string]$Method,
        [string]$Uri,
        [hashtable]$Headers,
        [object]$Body = $null
    )

    $params = @{
        Method      = $Method
        Uri         = $Uri
        Headers     = $Headers
        ContentType = "application/json"
    }

    if ($null -ne $Body) {
        $params.Body = ($Body | ConvertTo-Json -Depth 10)
    }

    try {
        return Invoke-RestMethod @params
    } catch {
        $status = $null
        $detail = ""
        if ($_.Exception.Response) {
            $status = [int]$_.Exception.Response.StatusCode
            $reader = New-Object IO.StreamReader($_.Exception.Response.GetResponseStream())
            $detail = $reader.ReadToEnd()
        }

        throw "HTTP $Method $Uri failed. Status=$status Body=$detail"
    }
}

# Create a run-specific suffix so repeated script runs do not collide.
$runId = (Get-Date -Format "yyyyMMddHHmmss")
$tenantSlug = "smoke-$runId"

$tenantHeaders = New-BasicAuthHeader -User $TenantBasicUser -Password $TenantBasicPassword
$identityHeaders = New-BasicAuthHeader -User $IdentityBasicUser -Password $IdentityBasicPassword
$rulesHeaders = New-BasicAuthHeader -User $RulesBasicUser -Password $RulesBasicPassword

Write-Step "Provision tenant + environments"
$tenantRequest = @{
    slug         = $tenantSlug
    name         = "Smoke Tenant $runId"
    plan         = "starter"
    contactEmail = "smoke+$runId@example.com"
}
$tenantResp = Invoke-ApiJson -Method POST -Uri "$TenantBaseUrl/v1/tenants" -Headers $tenantHeaders -Body $tenantRequest
$tenantId = $tenantResp.data.id
Write-Host "tenantId=$tenantId"

$envResp = Invoke-ApiJson -Method GET -Uri "$TenantBaseUrl/v1/tenants/$tenantId/environments" -Headers $tenantHeaders
$prodEnv = $envResp.data | Where-Object { $_.type -eq "PRODUCTION" } | Select-Object -First 1
if (-not $prodEnv) {
    throw "No PRODUCTION environment found for tenant $tenantId"
}
$environmentId = $prodEnv.id
Write-Host "environmentId=$environmentId"

Write-Step "Create API key in identity service"
$keyRequest = @{
    tenantId        = $tenantId
    environmentId   = $environmentId
    environmentType = "PRODUCTION"
    name            = "stage1-smoke-key"
    scopes          = @("*")
}
$keyResp = Invoke-ApiJson -Method POST -Uri "$IdentityBaseUrl/v1/api-keys" -Headers $identityHeaders -Body $keyRequest
$apiKey = $keyResp.data.key
if ([string]::IsNullOrWhiteSpace($apiKey)) {
    throw "Identity service did not return a raw API key"
}
Write-Host "apiKeyPrefix=$($keyResp.data.keyPrefix)"

Write-Step "Create one active rule"
$ruleRequest = @{
    tenantId        = $tenantId
    environmentType = "PRODUCTION"
    name            = "High Amount Review"
    description     = "Review high amount transactions"
    category        = "AMOUNT"
    priority        = 100
    conditions      = @(
        @{
            field    = "payload.amount"
            operator = "GREATER_THAN"
            value    = "1000"
        }
    )
    conditionLogic  = "AND"
    action          = "REVIEW"
    scoreAdjustment = 40
    reasonCode      = "AMOUNT_HIGH"
}
$ruleResp = Invoke-ApiJson -Method POST -Uri "$RulesBaseUrl/v1/rules" -Headers $rulesHeaders -Body $ruleRequest
Write-Host "ruleId=$($ruleResp.data)"

Write-Step "Call decision engine with the issued API key"
$decisionHeaders = @{ "X-API-Key" = $apiKey }
$decisionRequest = @{
    idempotencyKey = "smoke-decision-$runId"
    entityId       = "user-smoke-001"
    entityType     = "USER"
    eventType      = "LOGIN_ATTEMPT"
    payload        = @{
        amount = 1500
    }
    ipAddress      = "203.0.113.10"
    deviceId       = "smoke-device"
    sessionId      = "smoke-session"
    userAgent      = "stage1-smoke-script"
}
$decisionResp = Invoke-ApiJson -Method POST -Uri "$DecisionBaseUrl/v1/decisions" -Headers $decisionHeaders -Body $decisionRequest

Write-Step "Smoke flow result"
$decisionResp | ConvertTo-Json -Depth 10

# Print compact summary for CI logs and quick copy-paste debugging.
$summary = [PSCustomObject]@{
    tenantId   = $tenantId
    envId      = $environmentId
    apiKeyId   = $keyResp.data.keyId
    ruleId     = $ruleResp.data
    decisionId = $decisionResp.data.decisionId
    decision   = $decisionResp.data.decision
    riskScore  = $decisionResp.data.riskScore
}
Write-Host "`nSUMMARY:" -ForegroundColor Green
$summary | Format-List

# jumarkot-platform

## Stage 1 Real-Flow Smoke Script

Use the script below to run a real end-to-end Stage 1 flow:

1. Provision tenant and environments.
2. Create API key from identity-access-service.
3. Create rule in rules-service.
4. Call decision-engine-service with the issued API key.

Script path:

`scripts/stage1/real-flow-smoke.ps1`

### Run (no basic auth)

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\stage1\real-flow-smoke.ps1
```

### Run (with basic auth credentials)

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\stage1\real-flow-smoke.ps1 \
	-TenantBasicUser admin -TenantBasicPassword secret \
	-IdentityBasicUser admin -IdentityBasicPassword secret \
	-RulesBasicUser admin -RulesBasicPassword secret
```

### Optional base URL overrides

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\stage1\real-flow-smoke.ps1 \
	-TenantBaseUrl http://localhost:8082 \
	-IdentityBaseUrl http://localhost:8081 \
	-RulesBaseUrl http://localhost:8087 \
	-DecisionBaseUrl http://localhost:8085
```
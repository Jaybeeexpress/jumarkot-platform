# Stage 4 Execution Plan

This file is the working contract for Stage-4 delivery.

## Scope

- Rebuild shared design tokens
- Create enterprise app shell
- Implement ops console pages
- Implement developer portal pages
- Validate and push changes

## Delivery Checklist

- [x] Rebuild shared design tokens
- [x] Create enterprise app shell
- [ ] Implement ops console pages
- [ ] Implement developer portal pages
- [ ] Validate and push changes

## Quality Gates

- [x] Frontend typecheck passes for ops-console
- [x] Frontend typecheck passes for developer-portal
- [ ] Relevant tests pass
- [ ] Visual regression check completed on key screens
- [ ] Changes committed and pushed

## Notes

- Shared tokens extracted to packages/design-tokens/src/tokens.css and imported by both frontend apps.
- Created `EnterpriseShell` component in ops-console that both apps can use for consistent enterprise UI patterns.
- Migrated all ops-console pages to use the new EnterpriseShell with configurable nav sections.
- EnterpriseShell is now in apps/ops-console/src/components/layout/ for easy import and TypeScript support.

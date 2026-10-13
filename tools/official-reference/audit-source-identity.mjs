import { createHash } from 'node:crypto';

export const auditCaseIdentityPrefix = 'cmp-mermaid-audit-case:';

export function sourceSha256(source) {
  return createHash('sha256').update(source).digest('hex');
}

export function validateAuditCaseIdentity(actual, auditCase, auditSource) {
  if (actual === null || typeof actual !== 'object') {
    throw new Error(`${auditCase.id} did not expose an audit case identity`);
  }
  if (actual.caseId !== auditCase.id) {
    throw new Error(
      `${auditCase.id} resolved browser case ${actual.caseId ?? '<missing>'}`,
    );
  }
  if (actual.auditSource !== auditSource) {
    throw new Error(
      `${auditCase.id} resolved browser audit source ` +
        `${actual.auditSource ?? '<missing>'}; expected ${auditSource}`,
    );
  }
  if (actual.source !== auditCase.source) {
    throw new Error(
      `${auditCase.id} browser source SHA-256 ${sourceSha256(
        typeof actual.source === 'string' ? actual.source : '',
      )} does not match expected ${sourceSha256(auditCase.source)}`,
    );
  }
  return {
    auditCaseId: actual.caseId,
    auditSource: actual.auditSource,
    sourceSha256: sourceSha256(actual.source),
  };
}

export function manifestMatchesAuditCase(
  manifest,
  auditCase,
  auditSource,
) {
  return manifest.auditCaseId === auditCase.id &&
    manifest.auditSource === auditSource &&
    manifest.sourceSha256 === sourceSha256(auditCase.source);
}

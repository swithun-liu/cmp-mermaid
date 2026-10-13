import assert from 'node:assert/strict';
import test from 'node:test';
import {
  manifestMatchesAuditCase,
  sourceSha256,
  validateAuditCaseIdentity,
} from './audit-source-identity.mjs';

const auditCase = {
  id: 'visual_flowchart_129',
  source: 'flowchart TB\n  Start --> A & B\n  A & B --> Finish\n',
};

test('browser audit identity validates the exact corpus source', () => {
  const identity = validateAuditCaseIdentity(
    {
      caseId: auditCase.id,
      auditSource: 'visual-parity',
      source: auditCase.source,
    },
    auditCase,
    'visual-parity',
  );

  assert.deepEqual(identity, {
    auditCaseId: auditCase.id,
    auditSource: 'visual-parity',
    sourceSha256: sourceSha256(auditCase.source),
  });
});

test('browser audit identity rejects a same-id source from another corpus', () => {
  assert.throws(
    () => validateAuditCaseIdentity(
      {
        caseId: auditCase.id,
        auditSource: 'gallery',
        source: 'flowchart LR\n  A --> B --> C --> D\n',
      },
      auditCase,
      'visual-parity',
    ),
    /resolved browser audit source gallery; expected visual-parity/,
  );
});

test('browser audit identity rejects a stale bundle with the expected corpus', () => {
  assert.throws(
    () => validateAuditCaseIdentity(
      {
        caseId: auditCase.id,
        auditSource: 'visual-parity',
        source: 'flowchart LR\n  A --> B --> C --> D\n',
      },
      auditCase,
      'visual-parity',
    ),
    /browser source SHA-256 .* does not match expected/,
  );
});

test('reusable manifests require the exact source identity', () => {
  const validManifest = {
    auditCaseId: auditCase.id,
    auditSource: 'visual-parity',
    sourceSha256: sourceSha256(auditCase.source),
  };

  assert.equal(
    manifestMatchesAuditCase(validManifest, auditCase, 'visual-parity'),
    true,
  );
  assert.equal(
    manifestMatchesAuditCase(
      { ...validManifest, sourceSha256: sourceSha256('flowchart LR\nA --> B') },
      auditCase,
      'visual-parity',
    ),
    false,
  );
});

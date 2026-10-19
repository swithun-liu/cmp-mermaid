import assert from 'node:assert/strict';
import {
  chmodSync,
  copyFileSync,
  mkdirSync,
  mkdtempSync,
  readFileSync,
  rmSync,
  writeFileSync,
} from 'node:fs';
import { tmpdir } from 'node:os';
import { dirname, join } from 'node:path';
import { fileURLToPath } from 'node:url';
import { spawnSync } from 'node:child_process';
import test from 'node:test';

const sourceDirectory = dirname(fileURLToPath(import.meta.url));
const publishScript = join(sourceDirectory, 'publish-cocoapods-if-needed.sh');

test('skips a version that is already published without requiring a token', () => {
  const result = runFixture({
    publishedChecks: ['published'],
  });

  assert.equal(result.status, 0, result.stderr);
  assert.equal(result.pushCount, 0);
  assert.match(result.stdout, /already published/);
});

test('retries transient failures with bounded backoff', () => {
  const result = runFixture({
    podAttempts: [
      { status: 1, message: 'HTTP 503 Service Unavailable' },
      { status: 1, message: 'execution expired while connecting' },
      { status: 0, message: 'Published CMPMermaid' },
    ],
    publishedChecks: [
      'missing',
      'missing',
      'missing',
      'missing',
      'missing',
      'published',
    ],
  });

  assert.equal(result.status, 0, result.stderr);
  assert.equal(result.pushCount, 3);
  assert.match(result.stderr, /retrying in 0 second/);
});

test('does not retry a permanent validation failure', () => {
  const result = runFixture({
    podAttempts: [
      { status: 1, message: '[!] The spec did not pass validation' },
      { status: 0, message: 'must not run' },
    ],
    publishedChecks: ['missing', 'missing', 'missing'],
  });

  assert.equal(result.status, 1);
  assert.equal(result.pushCount, 1);
  assert.match(result.stderr, /refusing to retry/);
});

test('accepts a failed client response when trunk committed the version', () => {
  const result = runFixture({
    podAttempts: [
      { status: 1, message: 'HTTP 500 Internal Server Error' },
    ],
    publishedChecks: ['missing', 'published'],
  });

  assert.equal(result.status, 0, result.stderr);
  assert.equal(result.pushCount, 1);
  assert.match(result.stdout, /published despite the client-side error/);
});

test('stops after three transient failures', () => {
  const result = runFixture({
    podAttempts: [
      { status: 1, message: 'HTTP 503 Service Unavailable' },
      { status: 1, message: 'HTTP 503 Service Unavailable' },
      { status: 1, message: 'HTTP 503 Service Unavailable' },
      { status: 0, message: 'must not run' },
    ],
    publishedChecks: Array(8).fill('missing'),
  });

  assert.equal(result.status, 1);
  assert.equal(result.pushCount, 3);
  assert.match(result.stderr, /after 3 transient failures/);
});

function runFixture({
  podAttempts = [],
  publishedChecks = [],
}) {
  const fixture = mkdtempSync(join(tmpdir(), 'cmp-mermaid-cocoapods-test-'));
  try {
    const fixtureScript = join(fixture, 'publish-cocoapods-if-needed.sh');
    const checkScript = join(fixture, 'cocoapods-version-published.sh');
    const binDirectory = join(fixture, 'bin');
    const podCommand = join(binDirectory, 'pod');
    const podspec = join(fixture, 'CMPMermaid.podspec');
    const pushCountFile = join(fixture, 'push-count');
    const checkCountFile = join(fixture, 'check-count');

    mkdirSync(binDirectory);
    copyFileSync(publishScript, fixtureScript);
    writeFileSync(podspec, 'Pod::Spec.new do |spec|\nend\n');
    writeFileSync(checkScript, `#!/usr/bin/env bash
set -euo pipefail
count_file="${checkCountFile}"
count=0
if [[ -f "$count_file" ]]; then
  count="$(cat "$count_file")"
fi
count=$((count + 1))
printf '%s' "$count" > "$count_file"
variable="MOCK_PUBLISHED_CHECK_$count"
[[ "\${!variable:-missing}" == "published" ]]
`);
    writeFileSync(podCommand, `#!/usr/bin/env bash
set -euo pipefail
count_file="${pushCountFile}"
count=0
if [[ -f "$count_file" ]]; then
  count="$(cat "$count_file")"
fi
count=$((count + 1))
printf '%s' "$count" > "$count_file"
status_variable="MOCK_POD_STATUS_$count"
message_variable="MOCK_POD_MESSAGE_$count"
printf '%s\\n' "\${!message_variable:-unexpected pod attempt}"
exit "\${!status_variable:-99}"
`);
    chmodSync(fixtureScript, 0o755);
    chmodSync(checkScript, 0o755);
    chmodSync(podCommand, 0o755);

    const env = {
      ...process.env,
      PATH: `${binDirectory}:${process.env.PATH}`,
      COCOAPODS_TRUNK_TOKEN: 'test-token',
      COCOAPODS_PUSH_ATTEMPTS: '3',
      COCOAPODS_RETRY_DELAY_SECONDS: '0',
    };
    podAttempts.forEach(({ status, message }, index) => {
      env[`MOCK_POD_STATUS_${index + 1}`] = String(status);
      env[`MOCK_POD_MESSAGE_${index + 1}`] = message;
    });
    publishedChecks.forEach((state, index) => {
      env[`MOCK_PUBLISHED_CHECK_${index + 1}`] = state;
    });

    const result = spawnSync(
      fixtureScript,
      [podspec, '0.1.99'],
      {
        cwd: fixture,
        env,
        encoding: 'utf8',
      },
    );
    return {
      ...result,
      pushCount: readCounter(pushCountFile),
      checkCount: readCounter(checkCountFile),
    };
  } finally {
    rmSync(fixture, { recursive: true, force: true });
  }
}

function readCounter(path) {
  try {
    return Number(readFileSync(path, 'utf8'));
  } catch {
    return 0;
  }
}

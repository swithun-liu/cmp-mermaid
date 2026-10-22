import assert from 'node:assert/strict';
import { readFileSync } from 'node:fs';
import test from 'node:test';
import { requiredQualityJobs, verifiedQualityIosArtifact } from './verified-quality-ios-artifact.mjs';

const sha = 'a'.repeat(40);
function snapshot() {
  const run = {
    id: 42, run_attempt: 2, head_sha: sha, head_branch: 'main', event: 'push',
    path: '.github/workflows/quality.yml', status: 'in_progress', conclusion: null,
  };
  const pages = [{
    total_count: requiredQualityJobs.length,
    jobs: requiredQualityJobs.map(name => ({
      name, run_id: 42, run_attempt: 2, status: 'completed', conclusion: 'success',
    })),
  }];
  const artifacts = { artifacts: [{
    id: 99, name: 'verified-ios-publication', expired: false,
    workflow_run: { id: 42, head_sha: sha },
  }] };
  return { run, pages, artifacts };
}

test('accepts complete job evidence when the run summary is stale or finalized', () => {
  const { run, pages, artifacts } = snapshot();
  assert.equal(verifiedQualityIosArtifact(run, pages, artifacts, sha), 99);
  run.status = 'completed';
  run.conclusion = 'success';
  assert.equal(verifiedQualityIosArtifact(run, pages, artifacts, sha), 99);
});

test('rejects every missing, failed, skipped, unfinished, duplicate, or foreign job', () => {
  for (let index = 0; index < requiredQualityJobs.length; index += 1) {
    for (const mutate of [
      state => state.pages[0].jobs.splice(index, 1),
      state => { state.pages[0].jobs[index].conclusion = 'failure'; },
      state => { state.pages[0].jobs[index].conclusion = 'skipped'; },
      state => { state.pages[0].jobs[index].status = 'in_progress'; },
      state => { state.pages[0].jobs[index].name = 'unrecognized-job'; },
      state => { state.pages[0].jobs[index].run_id = 43; },
      state => { state.pages[0].jobs[index].run_attempt = 1; },
    ]) {
      const state = snapshot();
      mutate(state);
      assert.equal(verifiedQualityIosArtifact(state.run, state.pages, state.artifacts, sha), null);
    }
  }
  const state = snapshot();
  state.pages[0].jobs[1].name = state.pages[0].jobs[0].name;
  assert.equal(verifiedQualityIosArtifact(state.run, state.pages, state.artifacts, sha), null);
});

test('rejects wrong source, branch, workflow, event, summary failure, or incomplete pagination', () => {
  for (const [field, value] of [
    ['head_sha', 'b'.repeat(40)], ['head_branch', 'other'], ['event', 'pull_request'],
    ['path', '.github/workflows/other.yml'], ['conclusion', 'failure'],
    ['conclusion', 'cancelled'], ['status', 'queued'],
  ]) {
    const state = snapshot();
    state.run[field] = value;
    assert.equal(verifiedQualityIosArtifact(state.run, state.pages, state.artifacts, sha), null);
  }
  const state = snapshot();
  state.pages[0].total_count += 1;
  assert.equal(verifiedQualityIosArtifact(state.run, state.pages, state.artifacts, sha), null);
  assert.equal(verifiedQualityIosArtifact(state.run, [], state.artifacts, sha), null);
});

test('rejects absent, expired, ambiguous, or foreign artifacts', () => {
  for (const mutate of [
    state => { state.artifacts.artifacts = []; },
    state => { state.artifacts.artifacts[0].expired = true; },
    state => { state.artifacts.artifacts[0].workflow_run.id = 43; },
    state => { state.artifacts.artifacts[0].workflow_run.head_sha = 'b'.repeat(40); },
    state => { state.artifacts.artifacts[0].id = -1; },
    state => state.artifacts.artifacts.push(structuredClone(state.artifacts.artifacts[0])),
  ]) {
    const state = snapshot();
    mutate(state);
    assert.equal(verifiedQualityIosArtifact(state.run, state.pages, state.artifacts, sha), null);
  }
});

test('inventory covers the current workflow jobs and every matrix member', () => {
  const yaml = readFileSync(new URL('../../.github/workflows/quality.yml', import.meta.url), 'utf8');
  const jobNames = [...yaml.split('\njobs:\n')[1].matchAll(/^  ([\w-]+):$/gm)].map(match => match[1]);
  const ordinary = jobNames.filter(name => !['ios-frameworks', 'visual-audit-capture'].includes(name));
  const architectures = [...yaml.matchAll(
    /- target: (\w+)\n\s+directory: (\w+)\n\s+architecture: (\w+)/g,
  )].map(match => `ios-frameworks (${match[1]}, ${match[2]}, ${match[3]})`);
  const shards = yaml.match(/shard: \[([\d, ]+)\]/)[1].split(',').map(value =>
    `visual-audit-capture (${Number(value.trim())})`);
  assert.deepEqual([...requiredQualityJobs].sort(), [...ordinary, ...architectures, ...shards].sort());
});

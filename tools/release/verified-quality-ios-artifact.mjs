import { execFileSync } from 'node:child_process';
import { pathToFileURL } from 'node:url';

// Keep this inventory synchronized with quality.yml. Unknown or missing jobs fail closed.
export const requiredQualityJobs = [
  'android-kotlin17',
  'multiplatform',
  'ios-frameworks (IosSimulatorArm64, iosSimulatorArm64, arm64)',
  'ios-frameworks (IosArm64, iosArm64, arm64)',
  'ios-frameworks (IosX64, iosX64, x86_64)',
  'ios-binary',
  'visual-audit-build',
  ...Array.from({ length: 6 }, (_, shard) => `visual-audit-capture (${shard})`),
  'visual-regression',
];

export function verifiedQualityIosArtifact(run, jobPages, artifacts, sha) {
  if (run.head_sha !== sha || run.head_branch !== 'main' || run.event !== 'push'
      || run.path !== '.github/workflows/quality.yml'
      || !['completed', 'in_progress'].includes(run.status)
      || (run.conclusion !== null && run.conclusion !== 'success')) return null;
  const jobs = jobPages.flatMap(page => page.jobs);
  if (jobPages.length === 0 || jobPages.some(page => page.total_count !== requiredQualityJobs.length)
      || jobs.length !== requiredQualityJobs.length) return null;
  const names = jobs.map(job => job.name);
  if (new Set(names).size !== names.length
      || requiredQualityJobs.some(name => !names.includes(name))
      || jobs.some(job => job.run_id !== run.id || job.run_attempt !== run.run_attempt
        || job.status !== 'completed' || job.conclusion !== 'success')) return null;
  const matches = artifacts.artifacts.filter(artifact =>
    artifact.name === 'verified-ios-publication' && artifact.expired === false
    && artifact.workflow_run?.id === run.id && artifact.workflow_run?.head_sha === sha
    && Number.isSafeInteger(artifact.id) && artifact.id > 0);
  return matches.length === 1 ? matches[0].id : null;
}

if (process.argv[1] && import.meta.url === pathToFileURL(process.argv[1]).href) {
  try {
    const [repository, runId, sha] = process.argv.slice(2);
    if (!/^[\w.-]+\/[\w.-]+$/.test(repository ?? '')
        || !/^\d+$/.test(runId ?? '') || !/^[a-f0-9]{40}$/.test(sha ?? '')) {
      throw new Error('Expected repository, run ID, and full source SHA');
    }
    const api = (endpoint, paginate = false) => JSON.parse(execFileSync(
      'gh', ['api', endpoint, ...(paginate ? ['--paginate', '--slurp'] : [])],
      { encoding: 'utf8', maxBuffer: 16 * 1024 * 1024 },
    ));
    const base = `repos/${repository}/actions/runs/${runId}`;
    const run = api(base);
    const pages = api(`${base}/attempts/${run.run_attempt}/jobs?per_page=100`, true);
    const artifacts = api(`${base}/artifacts?per_page=100`);
    const artifactId = verifiedQualityIosArtifact(run, pages, artifacts, sha);
    if (artifactId !== null) console.log(artifactId);
  } catch (error) {
    console.error('Unable to verify the complete Quality Gate job inventory:', error.message);
    process.exitCode = 1;
  }
}

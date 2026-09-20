#!/usr/bin/env node

import { readFile, rename, writeFile } from 'node:fs/promises';
import path from 'node:path';
import process from 'node:process';
import { pathToFileURL } from 'node:url';

const STABLE_WORKSPACE = '/src/cmp-mermaid';

export function normalizeSbomPaths(document, workspace) {
  const normalizedWorkspace = path.resolve(workspace).replace(/\/+$/, '');
  if (normalizedWorkspace === path.parse(normalizedWorkspace).root) {
    throw new Error('Refusing to normalize a filesystem root');
  }

  let replacementCount = 0;
  const replaceWorkspace = (value) => {
    const parts = value.split(normalizedWorkspace);
    if (parts.length === 1) {
      return value;
    }
    replacementCount += parts.length - 1;
    return parts.join(STABLE_WORKSPACE);
  };

  const visit = (value) => {
    if (typeof value === 'string') {
      return replaceWorkspace(value);
    }
    if (Array.isArray(value)) {
      return value.map(visit);
    }
    if (value !== null && typeof value === 'object') {
      return Object.fromEntries(Object.entries(value).map(([key, child]) => [
        replaceWorkspace(key),
        visit(child),
      ]));
    }
    return value;
  };

  return {
    document: visit(document),
    replacementCount,
  };
}

async function main() {
  const [sbomPath, workspace] = process.argv.slice(2);
  if (!sbomPath || !workspace) {
    throw new Error(
      'Usage: node tools/release/normalize-sbom-paths.mjs <sbom.json> <workspace>',
    );
  }
  if (!path.isAbsolute(workspace)) {
    throw new Error(`Workspace must be an absolute path: ${workspace}`);
  }

  const source = await readFile(sbomPath, 'utf8');
  const parsed = JSON.parse(source);
  const { document, replacementCount } = normalizeSbomPaths(parsed, workspace);
  const temporaryPath = `${sbomPath}.tmp`;
  await writeFile(temporaryPath, `${JSON.stringify(document, null, 2)}\n`, 'utf8');
  await rename(temporaryPath, sbomPath);
  console.log(`Normalized ${replacementCount} private workspace path occurrence(s) in ${sbomPath}`);
}

if (process.argv[1] && import.meta.url === pathToFileURL(process.argv[1]).href) {
  main().catch((error) => {
    console.error(error.message);
    process.exitCode = 1;
  });
}

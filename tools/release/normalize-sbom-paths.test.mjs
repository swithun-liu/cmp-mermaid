import assert from 'node:assert/strict';
import test from 'node:test';

import { normalizeSbomPaths } from './normalize-sbom-paths.mjs';

const workspace = '/Users/runner/work/cmp-mermaid/cmp-mermaid';

test('replaces workspace paths in CycloneDX string values and keys', () => {
  const source = {
    bomFormat: 'CycloneDX',
    metadata: {
      component: {
        name: workspace,
        purl: 'pkg:maven/io.github.swithun-liu/mermaid-core@0.1.0',
      },
    },
    evidence: [
      `${workspace}/mermaid-core/build.gradle.kts`,
      `file://${workspace}/gradle.properties`,
      { [`${workspace}/settings.gradle.kts`]: 'workspace key' },
    ],
  };

  const { document, replacementCount } = normalizeSbomPaths(source, workspace);

  assert.equal(replacementCount, 4);
  assert.equal(document.metadata.component.name, '/src/cmp-mermaid');
  assert.equal(
    document.metadata.component.purl,
    'pkg:maven/io.github.swithun-liu/mermaid-core@0.1.0',
  );
  assert.deepEqual(document.evidence, [
    '/src/cmp-mermaid/mermaid-core/build.gradle.kts',
    'file:///src/cmp-mermaid/gradle.properties',
    { '/src/cmp-mermaid/settings.gradle.kts': 'workspace key' },
  ]);
  assert.ok(!JSON.stringify(document).includes(workspace));
});

test('leaves a path-free SBOM unchanged', () => {
  const source = { bomFormat: 'CycloneDX', components: [] };
  const result = normalizeSbomPaths(source, workspace);

  assert.equal(result.replacementCount, 0);
  assert.deepEqual(result.document, source);
});

test('refuses to normalize a filesystem root', () => {
  assert.throws(
    () => normalizeSbomPaths({ bomFormat: 'CycloneDX' }, '/'),
    /filesystem root/,
  );
});

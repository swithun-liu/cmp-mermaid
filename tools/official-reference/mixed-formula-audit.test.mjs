import assert from 'node:assert/strict';
import crypto from 'node:crypto';
import fs from 'node:fs';
import os from 'node:os';
import path from 'node:path';
import { spawnSync } from 'node:child_process';
import test from 'node:test';
import { fileURLToPath } from 'node:url';

const scripts = path.dirname(fileURLToPath(import.meta.url));
const sha256 = bytes => crypto.createHash('sha256').update(bytes).digest('hex');

function fixture(t) {
  const directory = fs.mkdtempSync(path.join(os.tmpdir(), 'mixed-formula-test-'));
  t.after(() => fs.rmSync(directory, { recursive: true, force: true }));
  // Independent browser/scene coordinates, including a padded outer label.
  const source = 'flowchart TD\nA["value $$x^2$$"] --> B';
  const official = {
    generatedAt: '2026-10-22T00:00:00.000Z',
    mermaidVersion: '12.0.0', mermaidSourceCommit: 'fixture',
    cases: [{
      id: 'flow-label', source,
      svgRect: { x: 100, y: 200, width: 100, height: 100 },
      viewBox: { x: 10, y: 20, width: 100, height: 100 },
      formulas: [{ source: 'x^2', fontSize: 14, color: 'rgb(40, 37, 61)',
        image: 'flow-label-formula-0.png' }],
      labels: [{
        rect: { x: 112, y: 215, width: 40, height: 30 },
        row: { x: 117, y: 220, width: 30, height: 20 },
        items: [
          { kind: 'text', text: 'value', lineHeight: 20,
            glyphRect: { x: 117, y: 225, width: 10, height: 8 } },
          { kind: 'formula', text: 'x2',
            rect: { x: 127, y: 222, width: 20, height: 16 } },
        ],
      }],
      edges: [{ points: [[10, 20], [10, 40]] }],
    }],
  };
  const native = {
    generatedAt: '2026-10-22T00:01:00.000Z',
    cases: [{
      id: 'flow-label', source, providerFailures: 0,
      requests: [{ source: 'x^2', fontSize: 14, colorArgb: 'ff28253d' }],
      labels: [{
        bounds: { x: 12, y: 15, width: 40, height: 30 },
        intrinsicWidth: 30, intrinsicHeight: 20, origin: [17, 20],
        items: [
          { kind: 'text', text: 'value',
            bounds: { x: 0, y: 0, width: 10, height: 20 } },
          { kind: 'formula', text: 'x^2',
            bounds: { x: 10, y: 2, width: 20, height: 16 } },
        ],
      }],
      edges: [{ points: [[0, 0], [0, 20]] }],
    }],
  };
  // Raster intentionally stays perfect, isolating the geometry gate.
  const raster = {
    officialGeneratedAt: official.generatedAt, nativeGeneratedAt: native.generatedAt,
    caseCount: 1, screenshotCount: 2, contactSheets: [],
    cases: [{ id: 'flow-label', ssim: 1 }],
  };
  const write = (name, value) => fs.writeFileSync(
    path.join(directory, `${name}.json`), JSON.stringify(value, null, 2),
  );
  const save = () => {
    write('official', official);
    write('native', native);
    write('raster', raster);
  };
  const run = (script, ...args) => spawnSync(process.execPath,
    [path.join(scripts, script), directory, ...args],
    { encoding: 'utf8', timeout: 10_000 });
  const audit = () => {
    save();
    const result = run('audit-mixed-formula.mjs');
    assert.equal(result.status, 0, result.stderr);
    return JSON.parse(fs.readFileSync(path.join(directory, 'geometry.json')));
  };
  save();
  return { directory, official, native, raster, write, save, run, audit };
}

test('audit mixed formula geometry', async t => {
  await t.test('matches centered content in independent browser and scene coordinates', t => {
    const report = fixture(t).audit();
    assert.deepEqual(report.counts, { pass: 1, review: 0, fail: 0 });
    assert.equal(report.cases[0].maximumLabelDelta, 0);
    assert.equal(report.cases[0].maximumItemPlacementDelta, 0);
    assert.deepEqual(report.cases[0].labels[0].official.absoluteItems.map(i => i.bounds), [
      { x: 17, y: 20, width: 10, height: 20 },
      { x: 27, y: 22, width: 20, height: 16 },
    ]);
  });

  await t.test('preserves a real diagonal detour against a straight route', t => {
    const f = fixture(t);
    f.native.cases[0].edges[0].points = [[0, 0], [10, 6], [-5, 14], [0, 20]];
    const result = f.audit().cases[0];
    assert.equal(result.status, 'fail');
    assert.equal(result.topology.status, 'fail');
    assert.equal(result.topology.edges[0].native.bendCount, 2);
    assert.deepEqual(result.topology.edges[0].native.directionSequence,
      ['Diagonal', 'Diagonal', 'Diagonal']);
    assert.ok(result.topology.findings.some(f => f.code === 'bend-count-mismatch'));
  });

  await t.test('preserves changed slopes when both routes are diagonal', t => {
    const f = fixture(t);
    f.official.cases[0].edges[0].points = [[10, 20], [30, 40]];
    f.native.cases[0].edges[0].points = [[0, 0], [5, 15], [20, 20]];
    const result = f.audit().cases[0];
    assert.equal(result.status, 'fail');
    assert.equal(result.topology.edges[0].native.bendCount, 1);
    assert.equal(result.topology.edges[0].official.bendCount, 0);
  });

  await t.test('collapses only forward collinear diagonal subdivisions', t => {
    const f = fixture(t);
    f.official.cases[0].edges[0].points = [[10, 20], [30, 40]];
    f.native.cases[0].edges[0].points = [[0, 0], [0, 0], [5, 5], [10, 10], [20, 20]];
    const result = f.audit().cases[0];
    assert.equal(result.status, 'pass');
    assert.equal(result.maximumRouteDelta, 0);
    assert.equal(result.topology.edges[0].native.bendCount, 0);
    assert.deepEqual(result.topology.edges[0].native.directionSequence, ['Diagonal']);
  });

  for (const [offset, status, bendCount] of [[0.0005, 'pass', 0], [0.01, 'fail', 1]]) {
    await t.test(`respects the collinearity distance tolerance for offset ${offset}`, t => {
      const f = fixture(t);
      f.official.cases[0].edges[0].points = [[10, 20], [30, 40]];
      f.native.cases[0].edges[0].points = [[0, 0], [10, 10 + offset], [20, 20]];
      const report = f.audit();
      assert.equal(report.topologyCoordinateTolerancePx, 0.001);
      assert.equal(report.cases[0].status, status);
      assert.equal(report.cases[0].topology.edges[0].native.bendCount, bendCount);
    });
  }

  await t.test('preserves collinear diagonal reversals', t => {
    const f = fixture(t);
    f.official.cases[0].edges[0].points = [[10, 20], [30, 40]];
    f.native.cases[0].edges[0].points = [[0, 0], [15, 15], [5, 5], [20, 20]];
    const result = f.audit().cases[0];
    assert.equal(result.status, 'fail');
    assert.equal(result.topology.edges[0].native.bendCount, 2);
  });

  for (const axis of ['x', 'y']) {
    await t.test(`does not pass a translated outer label on ${axis}`, t => {
      const f = fixture(t);
      const label = f.native.cases[0].labels[0];
      label.bounds[axis] += 40;
      label.origin[axis === 'x' ? 0 : 1] += 40;
      const result = f.audit().cases[0];
      assert.equal(result.status, 'review');
      assert.equal(result.maximumLabelDelta, 40);
      assert.equal(result.maximumItemPlacementDelta, 40);
      assert.equal(result.maximumItemDelta, 0);
      assert.equal(result.ssim, 1);
    });
  }

  for (const [offset, status] of [[0.07, 'pass'], [0.09, 'review']]) {
    await t.test(`respects the absolute placement tolerance for offset ${offset}`, t => {
      const f = fixture(t);
      f.native.cases[0].labels[0].bounds.x += offset;
      f.native.cases[0].labels[0].origin[0] += offset;
      const report = f.audit();
      assert.equal(report.coordinateTolerancePx, 0.08);
      assert.equal(report.cases[0].status, status);
      assert.ok(Math.abs(report.cases[0].maximumItemPlacementDelta - offset) < 1e-10);
    });
  }

  await t.test('detects a moved official row even when local item geometry matches', t => {
    const f = fixture(t);
    const label = f.official.cases[0].labels[0];
    label.row.x += 7;
    label.items[0].glyphRect.x += 7;
    label.items[1].rect.x += 7;
    const result = f.audit().cases[0];
    assert.equal(result.status, 'review');
    assert.equal(result.maximumLabelDelta, 0);
    assert.equal(result.maximumItemDelta, 0);
    assert.equal(result.maximumItemPlacementDelta, 7);
  });

  for (const index of [0, 1]) {
    await t.test(`compares final ${index === 0 ? 'text' : 'formula'} item positions`, t => {
      const f = fixture(t);
      f.native.cases[0].labels[0].items[index].bounds.y += 9;
      const result = f.audit().cases[0];
      assert.equal(result.status, 'review');
      assert.equal(result.maximumItemPlacementDelta, 9);
    });
  }

  await t.test('detects an inconsistent recorded intrinsic origin', t => {
    const f = fixture(t);
    f.native.cases[0].labels[0].origin[0] += 11;
    const result = f.audit().cases[0];
    assert.notEqual(result.status, 'pass');
    assert.equal(result.maximumItemPlacementDelta, 11);
  });

  await t.test('binds geometry to exact input bytes and available generation fields', t => {
    const f = fixture(t);
    f.raster.generatedAt = '2026-10-22T00:02:00.000Z';
    const report = f.audit();
    for (const name of ['official', 'native', 'raster']) {
      assert.equal(report.inputs[name].generatedAt, f[name].generatedAt);
      assert.equal(report.inputs[name].sha256,
        sha256(fs.readFileSync(path.join(f.directory, `${name}.json`))));
    }
    assert.equal(report.inputs.raster.officialGeneratedAt, f.official.generatedAt);
    assert.equal(report.inputs.raster.nativeGeneratedAt, f.native.generatedAt);
  });

  await t.test('records legacy raster generation absence without inventing a timestamp', t => {
    const report = fixture(t).audit();
    assert.equal(report.inputs.raster.generatedAt, null);
    assert.match(report.inputs.raster.sha256, /^[0-9a-f]{64}$/);
  });

  await t.test('rejects raster from a different native generation', t => {
    const f = fixture(t);
    f.raster.nativeGeneratedAt = 'old-native';
    f.save();
    const result = f.run('audit-mixed-formula.mjs');
    assert.notEqual(result.status, 0);
    assert.match(result.stderr, /Raster evidence is stale/);
  });

  await t.test('reports a Native render failure without label geometry', t => {
    const f = fixture(t);
    f.native.cases[0].error = 'Native scene resolution timed out';
    delete f.native.cases[0].labels;
    delete f.native.cases[0].edges;
    const result = f.audit().cases[0];
    assert.equal(result.status, 'fail');
    assert.ok(result.findings.includes('Native scene resolution timed out'));
  });
});

function publicationFixture(t) {
  const f = fixture(t);
  const geometry = f.audit();
  const destination = path.join(f.directory, 'published');
  const pixel = Buffer.from(
    'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVQIHWP4z8DwHwAFgAI/ScLttAAAAABJRU5ErkJggg==',
    'base64',
  );
  for (const file of ['flow-label-native.png', 'flow-label-official.png', 'flow-label-formula-0.png']) {
    fs.writeFileSync(path.join(f.directory, file), pixel);
  }
  const publish = () => f.run('publish-mixed-formula-report.mjs', destination);
  const reject = (pattern = /failed|incomplete|stale|mismatch|Duplicate/i) => {
    const result = publish();
    assert.notEqual(result.status, 0, result.stdout);
    assert.match(result.stderr, pattern);
    assert.equal(fs.existsSync(destination), false, 'Reject before copying evidence');
  };
  return { ...f, geometry, destination, publish, reject };
}

test('publish mixed formula evidence', async t => {
  await t.test('publishes matching generations with independently verified counts and hashes', t => {
    const f = publicationFixture(t);
    const result = f.publish();
    assert.equal(result.status, 0, result.stderr);
    const manifest = JSON.parse(fs.readFileSync(
      path.join(f.destination, 'visual-parity-manifest.json'),
    ));
    assert.deepEqual(manifest.counts, { pass: 1, review: 0, fail: 0 });
    assert.equal(manifest.caseCount, 1);
    assert.equal(manifest.screenshotCount, 2);
    assert.equal(manifest.geometryGeneratedAt, f.geometry.generatedAt);
    assert.deepEqual(manifest.inputs, f.geometry.inputs);
    assert.equal(manifest.records[0].sourceSha256, sha256(f.official.cases[0].source));
    for (const name of ['official', 'native', 'raster']) {
      assert.equal(sha256(fs.readFileSync(path.join(f.destination, `${name}.json`))),
        f.geometry.inputs[name].sha256);
    }
  });

  await t.test('publishes Review counts and an explicitly declared raster generation', t => {
    const f = publicationFixture(t);
    f.native.cases[0].labels[0].bounds.x += 1;
    f.native.cases[0].labels[0].origin[0] += 1;
    f.raster.generatedAt = '2026-10-22T00:02:00.000Z';
    f.audit();
    const result = f.publish();
    assert.equal(result.status, 0, result.stderr);
    const manifest = JSON.parse(fs.readFileSync(
      path.join(f.destination, 'visual-parity-manifest.json'),
    ));
    assert.deepEqual(manifest.counts, { pass: 0, review: 1, fail: 0 });
    assert.equal(manifest.rasterGeneratedAt, f.raster.generatedAt);
    assert.equal(manifest.inputs.raster.generatedAt, f.raster.generatedAt);
  });

  await t.test('rejects a newer native and raster generation retaining old geometry', t => {
    const f = publicationFixture(t);
    f.native.generatedAt = '2026-10-22T01:01:00.000Z';
    f.raster.nativeGeneratedAt = f.native.generatedAt;
    f.save();
    f.reject(/stale/i);
  });

  for (const name of ['official', 'native', 'raster']) {
    await t.test(`rejects changed ${name} bytes even with the same generation`, t => {
      const f = publicationFixture(t);
      f[name].diagnostics = 'changed after geometry audit';
      f.save();
      f.reject(/stale/i);
    });
    await t.test(`rejects mismatched ${name} generation metadata even with matching hashes`, t => {
      const f = publicationFixture(t);
      f.geometry.inputs[name].generatedAt = 'old-generation';
      f.write('geometry', f.geometry);
      f.reject(/stale/i);
    });
  }

  await t.test('rejects geometry without an input binding', t => {
    const f = publicationFixture(t);
    delete f.geometry.inputs;
    f.write('geometry', f.geometry);
    f.reject(/stale/i);
  });

  await t.test('rejects counts that disagree with the actual geometry cases', t => {
    const f = publicationFixture(t);
    f.geometry.counts.pass = 2;
    f.write('geometry', f.geometry);
    f.reject();
  });

  await t.test('rejects a failed geometry case hidden by a pass summary', t => {
    const f = publicationFixture(t);
    f.geometry.cases[0].status = 'fail';
    f.write('geometry', f.geometry);
    f.reject();
  });

  await t.test('rejects duplicate geometry IDs even when the summary count is unchanged', t => {
    const f = publicationFixture(t);
    f.geometry.cases.push(structuredClone(f.geometry.cases[0]));
    f.write('geometry', f.geometry);
    f.reject();
  });

  await t.test('rejects a geometry case from another source', t => {
    const f = publicationFixture(t);
    f.geometry.cases[0].source = 'flowchart TD\nX --> Y';
    f.write('geometry', f.geometry);
    f.reject(/source mismatch/i);
  });

  await t.test('rejects an unknown geometry case ID', t => {
    const f = publicationFixture(t);
    f.geometry.cases[0].id = 'other-case';
    f.write('geometry', f.geometry);
    f.reject();
  });

  for (const failure of ['error', 'providerFailures']) {
    await t.test(`rejects current Native ${failure} even with fresh provenance and old Pass counts`, t => {
      const f = publicationFixture(t);
      f.native.cases[0][failure] = failure === 'error' ? 'Native render failed' : 1;
      f.save();
      f.geometry.inputs.native.sha256 = sha256(
        fs.readFileSync(path.join(f.directory, 'native.json')),
      );
      f.write('geometry', f.geometry);
      f.reject(/failed/i);
    });
  }
});

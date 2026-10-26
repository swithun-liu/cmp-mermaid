import assert from 'node:assert/strict';
import test from 'node:test';
import rough from 'roughjs/bundled/rough.cjs.js';
import { isSvgRectanglePath } from './svg-rectangle-path.mjs';

test('recognizes the solid rectangle emitted by the installed Rough.js', () => {
  const generator = rough.generator();
  const drawable = generator.rectangle(10, 20, 50, 30, {
    roughness: 0,
    fill: '#ffffff',
    fillStyle: 'solid',
  });
  const paths = generator.toPaths(drawable);
  assert.equal(isSvgRectanglePath(paths[0].d), true);
  assert.equal(isSvgRectanglePath(paths[1].d), false);
});

test('recognizes implicit, explicit and repeated-endpoint closure', () => {
  for (const path of [
    'M-1e2 20 L-50 20 L-50 50 L-1e2 50',
    'M10,20 L60,20 L60,50 L10,50Z',
    'M10 20 L60 20 L60 50 L10 50 L10 20',
  ]) {
    assert.equal(isSvgRectanglePath(path), true, path);
  }
});

test('rejects curves, diagonals, missing coordinates and multiple subpaths', () => {
  for (const path of [
    null, '', 'M10 20 L60 20', 'M10 20 L60 20 L60 50 L10',
    'M10 20 C20 20 40 20 60 20 L60 50 L10 50',
    'M10 20 L60 20 L60 50 L10 50 M10 20 L60 20',
    'M10 20 L60 25 L60 50 L10 50',
    'M10 20 L60 20 L10 20 L60 20',
  ]) {
    assert.equal(isSvgRectanglePath(path), false, String(path));
  }
});

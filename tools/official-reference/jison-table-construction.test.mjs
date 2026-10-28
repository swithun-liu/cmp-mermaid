import assert from 'node:assert/strict';
import test from 'node:test';
import { emitJisonStates } from './jison-table-construction.mjs';

const cellSource = (cell) => typeof cell === 'number'
  ? `Cell.Goto(${cell})`
  : cell[0] === 3 ? 'Cell.Accept' : `Cell.${cell[0] === 1 ? 'Shift' : 'Reduce'}(${cell[1]})`;

test('empty and distinct rows keep their original keys and typed actions', () => {
  const text = emitJisonStates([{}, { 2: 3, 4: [1, 5], 7: [2, 9], 11: [3] }], cellSource, 20).join('\n');
  assert.match(text, /emptyMap\(\)/);
  assert.match(text, /action0: Cell = Cell.Goto\(3\)/);
  assert.match(text, /action1: Cell = Cell.Shift\(5\)/);
  assert.match(text, /action2: Cell = Cell.Reduce\(9\)/);
  assert.match(text, /action3: Cell = Cell.Accept/);
  assert.match(text, /2 to action0,[\s\S]*4 to action1,[\s\S]*7 to action2,[\s\S]*11 to action3,/);
});

test('repeated actions and key sets are shared with ordered sparse overrides', () => {
  const table = [
    { 19: [1, 8], 3: [2, 7], 11: [2, 7], 2: [2, 7] },
    { 19: [1, 8], 3: [2, 9], 11: [2, 9], 2: [2, 9] },
    { 2: [2, 7], 3: [2, 7], 11: [2, 7] },
  ];
  const text = emitJisonStates(table, cellSource, 2).join('\n');
  assert.equal((text.match(/Cell.Reduce\(7\)/g) ?? []).length, 1);
  assert.equal((text.match(/intArrayOf\(2, 3, 11, 19\)/g) ?? []).length, 1);
  assert.match(text, /buildJisonState\(symbols0, action0, mapOf\(\s+19 to action1,/);
  assert.match(text, /buildJisonState\(symbols1, action0\)/);
  assert.match(text, /addAll\(stateChunk1\(\)\)/);
  assert.equal(emitJisonStates(table, cellSource, 2).join('\n'), text);
});

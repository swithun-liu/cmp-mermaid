import { createHash } from 'node:crypto';
import { mkdirSync, writeFileSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const sourceUrl = 'https://html.spec.whatwg.org/entities.json';
const expectedSha256 = 'd741d877ac77c4194c4ad526b5b4a19aef8dfe411ab840a466891cdbb9f362e6';
const root = dirname(fileURLToPath(import.meta.url));
const output = resolve(
  root,
  '../../mermaid-core/src/commonMain/kotlin/com/swithun/cmpmermaid/core/flowchart/upstream/mermaid/Html5NamedEntities.kt',
);

const response = await fetch(sourceUrl);
if (!response.ok) {
  throw new Error(`Failed to fetch ${sourceUrl}: HTTP ${response.status}`);
}
const bytes = Buffer.from(await response.arrayBuffer());
const actualSha256 = createHash('sha256').update(bytes).digest('hex');
if (actualSha256 !== expectedSha256) {
  throw new Error(
    `WHATWG entity source changed: expected ${expectedSha256}, found ${actualSha256}`,
  );
}

const source = JSON.parse(bytes.toString('utf8'));
const entries = Object.entries(source)
  .map(([name, definition]) => [name.slice(1), definition.characters])
  .sort(([left], [right]) => (left < right ? -1 : left > right ? 1 : 0));
if (entries.length !== 2231) {
  throw new Error(`Expected 2231 WHATWG entities, found ${entries.length}`);
}

const kotlinString = (value) => {
  let result = '"';
  for (let index = 0; index < value.length; index += 1) {
    const codeUnit = value.charCodeAt(index);
    if (codeUnit === 0x22) {
      result += '\\"';
    } else if (codeUnit === 0x5c) {
      result += '\\\\';
    } else if (codeUnit === 0x24) {
      result += '\\$';
    } else if (codeUnit >= 0x20 && codeUnit <= 0x7e) {
      result += String.fromCharCode(codeUnit);
    } else {
      result += `\\u${codeUnit.toString(16).toUpperCase().padStart(4, '0')}`;
    }
  }
  return `${result}"`;
};

const names = entries.map(([name]) => `        ${kotlinString(name)},`).join('\n');
const values = entries.map(([, value]) => `        ${kotlinString(value)},`).join('\n');
const maxNameLength = Math.max(...entries.map(([name]) => name.length));

mkdirSync(dirname(output), { recursive: true });
writeFileSync(
  output,
  `package com.swithun.cmpmermaid.core.flowchart.upstream.mermaid

/**
 * Generated from the WHATWG named character references used by browser
 * innerHTML parsing. Regenerate with tools/official-reference/generate-html-entities.mjs.
 */
internal object Html5NamedEntities {
    const val MAX_NAME_LENGTH: Int = ${maxNameLength}

    fun valueOf(name: String): String? {
        var low = 0
        var high = names.lastIndex
        while (low <= high) {
            val middle = (low + high) ushr 1
            when {
                names[middle] < name -> low = middle + 1
                names[middle] > name -> high = middle - 1
                else -> return values[middle]
            }
        }
        return null
    }

    private val names = arrayOf(
${names}
    )

    private val values = arrayOf(
${values}
    )
}
`,
);

console.log(`Generated ${entries.length} WHATWG HTML entities.`);

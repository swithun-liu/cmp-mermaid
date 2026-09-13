import { readdir, readFile, writeFile } from 'node:fs/promises';
import { resolve } from 'node:path';

const CHUNK_SIZE_BYTES = 256 * 1024;
const outputDirectory = process.argv[2];

if (!outputDirectory) {
  throw new Error('Expected the Web distribution directory');
}

const wasmFiles = (await readdir(outputDirectory))
  .filter((fileName) => fileName.endsWith('.wasm'))
  .sort();

if (wasmFiles.length === 0) {
  throw new Error(`No Wasm files found in ${outputDirectory}`);
}

for (const wasmFile of wasmFiles) {
  const wasmPath = resolve(outputDirectory, wasmFile);
  const wasmBytes = await readFile(wasmPath);
  const chunks = [];

  for (
    let offset = 0, index = 0;
    offset < wasmBytes.length;
    offset += CHUNK_SIZE_BYTES, index += 1
  ) {
    const chunkName = `${wasmFile}.part-${index.toString().padStart(3, '0')}`;
    await writeFile(
      resolve(outputDirectory, chunkName),
      wasmBytes.subarray(offset, offset + CHUNK_SIZE_BYTES),
    );
    chunks.push(chunkName);
  }

  await writeFile(
    `${wasmPath}.chunks.json`,
    `${JSON.stringify({ byteLength: wasmBytes.length, chunks })}\n`,
  );
  await writeFile(wasmPath, new Uint8Array());
  console.log(`Split ${wasmFile} into ${chunks.length} chunks.`);
}

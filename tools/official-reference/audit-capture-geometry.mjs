import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import puppeteer from 'puppeteer';
import { cases } from './stability-corpus.mjs';

const repositoryRoot = path.resolve(
  path.dirname(fileURLToPath(import.meta.url)),
  '../..',
);
const inputDirectory = path.resolve(
  repositoryRoot,
  process.env.INPUT_DIR ?? 'captures/local/rc-stability-corpus',
);
const outputFile = process.env.OUTPUT_FILE
  ? path.resolve(repositoryRoot, process.env.OUTPUT_FILE)
  : null;
const thresholds = {
  minimumInkPixels: readNumber('MINIMUM_INK_PIXELS', 100),
  minimumContentSize: readNumber('MINIMUM_CONTENT_SIZE', 20),
  minimumWidthRatio: readNumber('MINIMUM_WIDTH_RATIO', 0.8),
  maximumWidthRatio: readNumber('MAXIMUM_WIDTH_RATIO', 1.35),
  minimumHeightRatio: readNumber('MINIMUM_HEIGHT_RATIO', 0.8),
  maximumHeightRatio: readNumber('MAXIMUM_HEIGHT_RATIO', 1.25),
  minimumInkRatio: readNumber('MINIMUM_INK_RATIO', 0.4),
  maximumInkRatio: readNumber('MAXIMUM_INK_RATIO', 1.7),
};

const browser = await puppeteer.launch({ headless: 'shell' });
const results = [];
const failures = [];

try {
  const page = await browser.newPage();
  for (const auditCase of cases) {
    const native = await analyzePng(
      page,
      path.join(inputDirectory, `${auditCase.id}_native.png`),
    );
    const official = await analyzePng(
      page,
      path.join(inputDirectory, `${auditCase.id}_official.png`),
    );
    const result = {
      id: auditCase.id,
      kind: auditCase.kind,
      native,
      official,
      widthRatio: ratio(native.contentBounds.width, official.contentBounds.width),
      heightRatio: ratio(
        native.contentBounds.height,
        official.contentBounds.height,
      ),
      inkRatio: ratio(native.inkRatio, official.inkRatio),
    };
    results.push(result);
    validateCapture(`${auditCase.id}/Native`, native);
    validateCapture(`${auditCase.id}/Official`, official);
    validateRatio(
      `${auditCase.id}/width`,
      result.widthRatio,
      thresholds.minimumWidthRatio,
      thresholds.maximumWidthRatio,
    );
    validateRatio(
      `${auditCase.id}/height`,
      result.heightRatio,
      thresholds.minimumHeightRatio,
      thresholds.maximumHeightRatio,
    );
    validateRatio(
      `${auditCase.id}/ink`,
      result.inkRatio,
      thresholds.minimumInkRatio,
      thresholds.maximumInkRatio,
    );
  }
} finally {
  await browser.close();
}

const report = {
  schemaVersion: 1,
  generatedAt: new Date().toISOString(),
  inputDirectory,
  caseCount: results.length,
  thresholds,
  failures,
  cases: results,
};

if (outputFile !== null) {
  fs.mkdirSync(path.dirname(outputFile), { recursive: true });
  fs.writeFileSync(outputFile, `${JSON.stringify(report, null, 2)}\n`);
}

console.log(
  `Audited ${results.length} Native/Official pairs from ${inputDirectory}`,
);
console.log(
  `Width ${range(results.map((result) => result.widthRatio))}; ` +
    `height ${range(results.map((result) => result.heightRatio))}; ` +
    `ink ${range(results.map((result) => result.inkRatio))}`,
);

if (failures.length > 0) {
  for (const failure of failures) {
    console.error(`- ${failure}`);
  }
  process.exitCode = 1;
}

function validateCapture(label, capture) {
  if (capture.inkPixels < thresholds.minimumInkPixels) {
    failures.push(
      `${label} contains ${capture.inkPixels} foreground pixels; ` +
        `expected at least ${thresholds.minimumInkPixels}`,
    );
  }
  if (
    capture.contentBounds.width < thresholds.minimumContentSize ||
    capture.contentBounds.height < thresholds.minimumContentSize
  ) {
    failures.push(
      `${label} content bounds are ` +
        `${capture.contentBounds.width}x${capture.contentBounds.height}`,
    );
  }
}

function validateRatio(label, value, minimum, maximum) {
  if (!Number.isFinite(value) || value < minimum || value > maximum) {
    failures.push(
      `${label} ratio ${value.toFixed(3)} is outside ` +
        `[${minimum.toFixed(3)}, ${maximum.toFixed(3)}]`,
    );
  }
}

async function analyzePng(page, fileName) {
  if (!fs.existsSync(fileName)) {
    throw new Error(`Missing capture: ${fileName}`);
  }
  const source = `data:image/png;base64,${fs.readFileSync(fileName).toString('base64')}`;
  return page.evaluate(async (imageSource) => {
    const image = new Image();
    image.src = imageSource;
    await image.decode();
    const canvas = document.createElement('canvas');
    canvas.width = image.width;
    canvas.height = image.height;
    const context = canvas.getContext('2d', { willReadFrequently: true });
    context.drawImage(image, 0, 0);
    const pixels = context.getImageData(0, 0, image.width, image.height).data;
    const cornerIndexes = [
      0,
      (image.width - 1) * 4,
      (image.height - 1) * image.width * 4,
      (image.height * image.width - 1) * 4,
    ];
    const background = [0, 1, 2, 3].map((channel) => {
      const values = cornerIndexes
        .map((index) => pixels[index + channel])
        .sort((left, right) => left - right);
      return Math.round((values[1] + values[2]) / 2);
    });
    let minimumX = image.width;
    let minimumY = image.height;
    let maximumX = -1;
    let maximumY = -1;
    let inkPixels = 0;
    for (let y = 0; y < image.height; y += 1) {
      for (let x = 0; x < image.width; x += 1) {
        const index = (y * image.width + x) * 4;
        const distance =
          Math.abs(pixels[index] - background[0]) +
          Math.abs(pixels[index + 1] - background[1]) +
          Math.abs(pixels[index + 2] - background[2]) +
          Math.abs(pixels[index + 3] - background[3]);
        if (distance <= 24) continue;
        inkPixels += 1;
        minimumX = Math.min(minimumX, x);
        minimumY = Math.min(minimumY, y);
        maximumX = Math.max(maximumX, x);
        maximumY = Math.max(maximumY, y);
      }
    }
    const contentBounds = maximumX < 0
      ? { x: 0, y: 0, width: 0, height: 0 }
      : {
          x: minimumX,
          y: minimumY,
          width: maximumX - minimumX + 1,
          height: maximumY - minimumY + 1,
        };
    return {
      width: image.width,
      height: image.height,
      background,
      inkPixels,
      inkRatio: inkPixels / (image.width * image.height),
      contentBounds,
    };
  }, source);
}

function ratio(numerator, denominator) {
  return denominator === 0 ? Number.NaN : numerator / denominator;
}

function range(values) {
  const minimum = Math.min(...values);
  const maximum = Math.max(...values);
  return `${minimum.toFixed(3)}-${maximum.toFixed(3)}`;
}

function readNumber(name, fallback) {
  const source = process.env[name];
  if (source == null || source.length === 0) return fallback;
  const value = Number(source);
  if (!Number.isFinite(value)) {
    throw new Error(`${name} must be a finite number`);
  }
  return value;
}

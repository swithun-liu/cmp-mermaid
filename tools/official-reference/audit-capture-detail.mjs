import {
  existsSync,
  mkdirSync,
  readFileSync,
  writeFileSync,
} from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import puppeteer from 'puppeteer';
import {
  compareDetailManifests,
  compareRasterFeatures,
  manifestViewportBackground,
} from './detail-audit-core.mjs';
import { cases as productionCases } from './production-corpus.mjs';
import { puppeteerLaunchOptions } from './puppeteer-options.mjs';
import { cases as stabilityCases } from './stability-corpus.mjs';
import { cases as visualParityCases } from './visual-parity-corpus.mjs';

const repositoryRoot = path.resolve(
  path.dirname(fileURLToPath(import.meta.url)),
  '../..',
);
const inputDirectory = path.resolve(
  repositoryRoot,
  process.env.INPUT_DIR ?? 'captures/local/rc-stability-corpus',
);
const outputFile = path.resolve(
  repositoryRoot,
  process.env.OUTPUT_FILE ?? path.join(inputDirectory, 'detail-audit.json'),
);
const heatmapDirectory = path.resolve(
  repositoryRoot,
  process.env.HEATMAP_DIR ?? path.join(inputDirectory, 'detail-diffs'),
);
const corpusSource = process.env.CORPUS_SOURCE ?? 'stability';
const corpusCases = {
  stability: stabilityCases,
  production: productionCases,
  'visual-parity': visualParityCases,
}[corpusSource];
if (corpusCases === undefined) {
  throw new Error(
    'CORPUS_SOURCE must be stability, production, or visual-parity',
  );
}
const corpusKind = process.env.CORPUS_KIND ?? 'all';
const themeOverride = process.env.CAPTURE_THEME ?? null;
const selectedIds = new Set(
  (process.env.CAPTURE_CASE_IDS ?? '')
    .split(/[\s,]+/)
    .filter(Boolean),
);
const failOnReview = (process.env.FAIL_ON_REVIEW ?? 'true') === 'true';
const gridSize = readInteger('DETAIL_GRID_SIZE', 96);
const cases = corpusCases.filter(
  (entry) =>
    (corpusKind === 'all' || entry.kind === corpusKind) &&
    (selectedIds.size === 0 || selectedIds.has(entry.id)),
);
if (cases.length === 0) {
  throw new Error(`No ${corpusSource} cases found for kind ${corpusKind}`);
}

mkdirSync(path.dirname(outputFile), { recursive: true });
mkdirSync(heatmapDirectory, { recursive: true });
const browser = await puppeteer.launch(puppeteerLaunchOptions);
const results = [];

try {
  const page = await browser.newPage();
  for (const auditCase of cases) {
    const themeSuffix = themeOverride === null ? '' : `_${themeOverride}`;
    const prefix = path.join(inputDirectory, `${auditCase.id}${themeSuffix}`);
    const nativeManifest = readJson(`${prefix}_native.manifest.json`);
    const officialManifest = readJson(`${prefix}_official.manifest.json`);
    const manifest = compareDetailManifests(
      nativeManifest,
      officialManifest,
      auditCase.expectedTexts ?? [],
    );
    const nativeRaster = await analyzePng(
      page,
      `${prefix}_native.png`,
      gridSize,
      manifestViewportBackground(nativeManifest),
    );
    const officialRaster = await analyzePng(
      page,
      `${prefix}_official.png`,
      gridSize,
      manifestViewportBackground(officialManifest),
    );
    const raster = compareRasterFeatures(nativeRaster, officialRaster);
    const findings = [...manifest.findings, ...raster.findings];
    const status = findings.some((finding) => finding.severity === 'error')
      ? 'fail'
      : findings.length > 0
        ? 'review'
        : 'pass';
    let heatmap = null;
    if (status !== 'pass') {
      heatmap = path.join(
        heatmapDirectory,
        `${auditCase.id}${themeSuffix}_heatmap.png`,
      );
      await writeHeatmap(page, nativeRaster, officialRaster, heatmap);
    }
    results.push({
      id: auditCase.id,
      kind: auditCase.kind,
      status,
      findings,
      manifest: withoutFindings(manifest),
      raster: withoutFindings(raster),
      nativeRaster: rasterSummary(nativeRaster),
      officialRaster: rasterSummary(officialRaster),
      heatmap: heatmap == null
        ? null
        : path.relative(repositoryRoot, heatmap),
    });
    console.log(
      `${auditCase.kind}/${auditCase.id}: ${status} ` +
        `(${findings.length} findings)`,
    );
  }
} finally {
  await browser.close();
}

const counts = countBy(results, (result) => result.status);
const report = {
  schemaVersion: 1,
  generatedAt: new Date().toISOString(),
  inputDirectory: path.relative(repositoryRoot, inputDirectory),
  corpusSource,
  corpusKind,
  theme: themeOverride,
  failOnReview,
  gridSize,
  caseCount: results.length,
  counts,
  reviewQueue: results
    .filter((result) => result.status !== 'pass')
    .map((result) => ({
      id: result.id,
      kind: result.kind,
      status: result.status,
      findingCodes: [...new Set(
        result.findings.map((finding) => finding.code),
      )],
      heatmap: result.heatmap,
    })),
  cases: results,
};
writeFileSync(outputFile, `${JSON.stringify(report, null, 2)}\n`);

console.log(
  `Detailed audit: ${counts.pass ?? 0} pass, ${counts.review ?? 0} review, ` +
    `${counts.fail ?? 0} fail`,
);
console.log(`Report: ${path.relative(repositoryRoot, outputFile)}`);
if (
  (counts.fail ?? 0) > 0 ||
  (failOnReview && (counts.review ?? 0) > 0)
) {
  process.exitCode = 1;
}

async function analyzePng(page, fileName, size, declaredBackground) {
  if (!existsSync(fileName)) {
    throw new Error(`Missing capture: ${fileName}`);
  }
  const source =
    `data:image/png;base64,${readFileSync(fileName).toString('base64')}`;
  return page.evaluate(async ({
    imageSource,
    gridSize: targetSize,
    declaredBackground: manifestBackground,
  }) => {
    const image = new Image();
    image.src = imageSource;
    await image.decode();
    const sourceCanvas = document.createElement('canvas');
    sourceCanvas.width = image.width;
    sourceCanvas.height = image.height;
    const sourceContext = sourceCanvas.getContext(
      '2d',
      { willReadFrequently: true },
    );
    sourceContext.drawImage(image, 0, 0);
    const sourcePixels = sourceContext.getImageData(
      0,
      0,
      image.width,
      image.height,
    ).data;
    let background = manifestBackground;
    if (background === null) {
      const sampledColors = new Map();
      for (let y = 0; y < image.height; y += 4) {
        for (let x = 0; x < image.width; x += 4) {
          const index = (y * image.width + x) * 4;
          const key =
            `${sourcePixels[index]},${sourcePixels[index + 1]},` +
            `${sourcePixels[index + 2]},${sourcePixels[index + 3]}`;
          sampledColors.set(key, (sampledColors.get(key) ?? 0) + 1);
        }
      }
      background = [...sampledColors.entries()]
        .sort((left, right) => right[1] - left[1])[0][0]
        .split(',')
        .map(Number);
    }
    const distance = (pixels, index) =>
      Math.abs(pixels[index] - background[0]) +
      Math.abs(pixels[index + 1] - background[1]) +
      Math.abs(pixels[index + 2] - background[2]) +
      Math.abs(pixels[index + 3] - background[3]);
    let minimumX = image.width;
    let minimumY = image.height;
    let maximumX = -1;
    let maximumY = -1;
    for (let y = 0; y < image.height; y += 1) {
      for (let x = 0; x < image.width; x += 1) {
        const index = (y * image.width + x) * 4;
        if (distance(sourcePixels, index) <= 24) continue;
        minimumX = Math.min(minimumX, x);
        minimumY = Math.min(minimumY, y);
        maximumX = Math.max(maximumX, x);
        maximumY = Math.max(maximumY, y);
      }
    }
    const contentBounds = maximumX < 0
      ? { x: 0, y: 0, width: image.width, height: image.height }
      : {
          x: minimumX,
          y: minimumY,
          width: maximumX - minimumX + 1,
          height: maximumY - minimumY + 1,
        };
    const normalizedCanvas = document.createElement('canvas');
    normalizedCanvas.width = targetSize;
    normalizedCanvas.height = targetSize;
    const normalizedContext = normalizedCanvas.getContext(
      '2d',
      { willReadFrequently: true },
    );
    normalizedContext.imageSmoothingEnabled = true;
    normalizedContext.drawImage(
      image,
      contentBounds.x,
      contentBounds.y,
      contentBounds.width,
      contentBounds.height,
      0,
      0,
      targetSize,
      targetSize,
    );
    const pixels = normalizedContext.getImageData(
      0,
      0,
      targetSize,
      targetSize,
    ).data;
    const mask = [];
    const colors = [];
    const luminance = [];
    for (let index = 0; index < pixels.length; index += 4) {
      const pixelDistance =
        Math.abs(pixels[index] - background[0]) +
        Math.abs(pixels[index + 1] - background[1]) +
        Math.abs(pixels[index + 2] - background[2]) +
        Math.abs(pixels[index + 3] - background[3]);
      mask.push(pixelDistance > 24);
      colors.push(pixels[index], pixels[index + 1], pixels[index + 2]);
      luminance.push(
        pixels[index] * 0.2126 +
        pixels[index + 1] * 0.7152 +
        pixels[index + 2] * 0.0722,
      );
    }
    const edges = mask.map((foreground, index) => {
      const x = index % targetSize;
      const y = Math.floor(index / targetSize);
      if (x === 0 || y === 0 || x + 1 === targetSize || y + 1 === targetSize) {
        return false;
      }
      const horizontal = Math.abs(luminance[index - 1] - luminance[index + 1]);
      const vertical = Math.abs(
        luminance[index - targetSize] - luminance[index + targetSize],
      );
      const maskBoundary =
        mask[index - 1] !== foreground ||
        mask[index + 1] !== foreground ||
        mask[index - targetSize] !== foreground ||
        mask[index + targetSize] !== foreground;
      return horizontal + vertical > 48 || maskBoundary;
    });
    return {
      gridSize: targetSize,
      imageWidth: image.width,
      imageHeight: image.height,
      background,
      contentBounds,
      mask,
      edges,
      colors,
    };
  }, {
    imageSource: source,
    gridSize: size,
    declaredBackground,
  });
}

async function writeHeatmap(page, native, official, outputPath) {
  const dataUrl = await page.evaluate(({ left, right }) => {
    const canvas = document.createElement('canvas');
    canvas.width = left.gridSize;
    canvas.height = left.gridSize;
    const context = canvas.getContext('2d');
    const image = context.createImageData(canvas.width, canvas.height);
    for (let index = 0; index < left.mask.length; index += 1) {
      const output = index * 4;
      const color = index * 3;
      const leftOnly = left.mask[index] && !right.mask[index];
      const rightOnly = !left.mask[index] && right.mask[index];
      const difference =
        Math.abs(left.colors[color] - right.colors[color]) +
        Math.abs(left.colors[color + 1] - right.colors[color + 1]) +
        Math.abs(left.colors[color + 2] - right.colors[color + 2]);
      image.data[output] = leftOnly ? 255 : Math.min(255, difference);
      image.data[output + 1] =
        left.edges[index] || right.edges[index] ? 180 : 0;
      image.data[output + 2] = rightOnly ? 255 : 0;
      image.data[output + 3] =
        leftOnly || rightOnly || difference > 24 ? 255 : 24;
    }
    context.putImageData(image, 0, 0);
    return canvas.toDataURL('image/png');
  }, { left: native, right: official });
  writeFileSync(outputPath, Buffer.from(dataUrl.split(',')[1], 'base64'));
}

function readJson(fileName) {
  if (!existsSync(fileName)) {
    throw new Error(`Missing audit manifest: ${fileName}`);
  }
  return JSON.parse(readFileSync(fileName, 'utf8'));
}

function withoutFindings(value) {
  const { findings, ...rest } = value;
  return rest;
}

function rasterSummary(value) {
  return {
    imageWidth: value.imageWidth,
    imageHeight: value.imageHeight,
    background: value.background,
    contentBounds: value.contentBounds,
    foregroundCells: value.mask.filter(Boolean).length,
    edgeCells: value.edges.filter(Boolean).length,
  };
}

function countBy(values, selector) {
  const counts = {};
  for (const value of values) {
    const key = selector(value);
    counts[key] = (counts[key] ?? 0) + 1;
  }
  return counts;
}

function readInteger(name, fallback) {
  const value = Number.parseInt(process.env[name] ?? fallback, 10);
  if (!Number.isInteger(value) || value < 16 || value > 256) {
    throw new Error(`${name} must be an integer between 16 and 256`);
  }
  return value;
}

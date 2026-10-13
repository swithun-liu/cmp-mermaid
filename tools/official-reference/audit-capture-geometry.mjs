import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';
import puppeteer from 'puppeteer';
import {
  compareDetailManifests,
  compareRasterFeatures,
} from './detail-audit-core.mjs';
import { classifyDetailFindings } from './detail-audit-policy.mjs';
import { classifyGeometryRatio } from './geometry-audit-policy.mjs';
import {
  mergedInputDirectoryMetadata,
  replaceCasesForKind,
  replaceMessagesForCaseIds,
} from './partial-audit-report.mjs';
import { cases as productionCases } from './production-corpus.mjs';
import { puppeteerLaunchOptions } from './puppeteer-options.mjs';
import { cases as stabilityCases } from './stability-corpus.mjs';
import { cases as swimlaneDdltCases } from './swimlane-ddlt-corpus.mjs';
import { cases as visualParityCases } from './visual-structure-corpus.mjs';

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
const corpusSource = process.env.CORPUS_SOURCE ?? 'stability';
const corpusCases = {
  stability: stabilityCases,
  production: productionCases,
  'swimlane-ddlt': swimlaneDdltCases,
  'visual-parity': visualParityCases,
}[corpusSource];
if (corpusCases === undefined) {
  throw new Error(
    'CORPUS_SOURCE must be stability, production, swimlane-ddlt, or visual-parity',
  );
}
const corpusKind = process.env.CORPUS_KIND ?? 'all';
const themeOverride = process.env.CAPTURE_THEME ?? null;
const selectedIds = new Set(
  (process.env.CAPTURE_CASE_IDS ?? '')
    .split(/[\s,]+/)
    .filter(Boolean),
);
const supportedThemes = [
  'default',
  'dark',
  'forest',
  'neutral',
  'base',
  'neo',
  'neo-dark',
  'redux',
  'redux-color',
  'redux-dark',
  'redux-dark-color',
];
if (themeOverride !== null && !supportedThemes.includes(themeOverride)) {
  throw new Error(`CAPTURE_THEME must be one of: ${supportedThemes.join(', ')}`);
}
const cases = corpusCases.filter(
  (entry) =>
    (corpusKind === 'all' || entry.kind === corpusKind) &&
    (selectedIds.size === 0 || selectedIds.has(entry.id)),
);
if (cases.length === 0) {
  throw new Error(`No ${corpusSource} cases found for kind ${corpusKind}`);
}
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

const browser = await puppeteer.launch(puppeteerLaunchOptions);
const results = [];
const failures = [];
const reviews = [];

try {
  const page = await browser.newPage();
  for (const auditCase of cases) {
    const themeSuffix = themeOverride === null ? '' : `_${themeOverride}`;
    const prefix = capturePrefix(auditCase, themeSuffix);
    const native = await analyzePng(
      page,
      `${prefix}_native.png`,
    );
    const official = await analyzePng(
      page,
      `${prefix}_official.png`,
    );
    const nativeManifest = readJson(`${prefix}_native.manifest.json`);
    const officialManifest = readJson(`${prefix}_official.manifest.json`);
    const detail = compareDetailManifests(
      nativeManifest,
      officialManifest,
      auditCase.expectedTexts ?? [],
    );
    const raster = compareRasterFeatures(native, official);
    const detailFindings = classifyDetailFindings(
      detail.findings,
      auditCase,
      { manifest: detail, raster },
    );
    const structuralErrors = detailFindings.filter(
      (finding) => finding.severity === 'error',
    );
    structuralErrors.forEach((finding) => {
      failures.push(
        `${auditCase.id}/structure ${finding.code}: ${finding.message}`,
      );
    });
    detailFindings
      .filter((finding) => finding.policy !== undefined)
      .forEach((finding) => {
        reviews.push(
          `${auditCase.id}/structure ${finding.code}: ${finding.message}`,
        );
      });
    const reviewIterativeGeometry =
      auditCase.layout === 'cose-bilkent' &&
      structuralErrors.length === 0;
    const manifestWidthRatio = ratio(
      nativeManifest.viewport?.width,
      officialManifest.viewport?.width,
    );
    const manifestHeightRatio = ratio(
      nativeManifest.viewport?.height,
      officialManifest.viewport?.height,
    );
    const nativeGeometryBounds = manifestGeometryBounds(nativeManifest);
    const officialGeometryBounds = manifestGeometryBounds(officialManifest);
    const manifestGeometryWidthRatio = ratio(
      nativeGeometryBounds.width,
      officialGeometryBounds.width,
    );
    const manifestGeometryHeightRatio = ratio(
      nativeGeometryBounds.height,
      officialGeometryBounds.height,
    );
    const manifestGeometryMatches =
      structuralErrors.length === 0 &&
      (
        (
          within(
            manifestWidthRatio,
            thresholds.minimumWidthRatio,
            thresholds.maximumWidthRatio,
          ) &&
          within(
            manifestHeightRatio,
            thresholds.minimumHeightRatio,
            thresholds.maximumHeightRatio,
          )
        ) ||
        (
          within(
            manifestGeometryWidthRatio,
            thresholds.minimumWidthRatio,
            thresholds.maximumWidthRatio,
          ) &&
          within(
            manifestGeometryHeightRatio,
            thresholds.minimumHeightRatio,
            thresholds.maximumHeightRatio,
          ) &&
          within(detail.elementComparison.ratios.geometry, 0.95, 1.05)
        )
      );
    const dimensionReviewReason = reviewIterativeGeometry
      ? 'CoSE manifest structure matches'
      : manifestGeometryMatches
        ? 'manifest viewport and structure match; raster bounds reflect font wrapping'
        : null;
    const result = {
      id: auditCase.id,
      kind: auditCase.kind,
      layout: auditCase.layout ?? null,
      native: geometrySummary(native),
      official: geometrySummary(official),
      manifestWidthRatio,
      manifestHeightRatio,
      manifestGeometryWidthRatio,
      manifestGeometryHeightRatio,
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
      dimensionReviewReason,
    );
    validateRatio(
      `${auditCase.id}/height`,
      result.heightRatio,
      thresholds.minimumHeightRatio,
      thresholds.maximumHeightRatio,
      dimensionReviewReason,
    );
    validateRatio(
      `${auditCase.id}/ink`,
      result.inkRatio,
      thresholds.minimumInkRatio,
      thresholds.maximumInkRatio,
      reviewIterativeGeometry ? 'CoSE manifest structure matches' : null,
    );
  }
} finally {
  await browser.close();
}

const report = {
  schemaVersion: 1,
  generatedAt: new Date().toISOString(),
  inputDirectory: path.relative(repositoryRoot, inputDirectory),
  corpusSource,
  corpusKind,
  theme: themeOverride,
  caseCount: results.length,
  thresholds,
  failures,
  reviews,
  cases: results,
};

if (outputFile !== null) {
  fs.mkdirSync(path.dirname(outputFile), { recursive: true });
  const persistedReport = mergePartialGeometryReport(report);
  fs.writeFileSync(
    outputFile,
    `${JSON.stringify(persistedReport, null, 2)}\n`,
  );
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
if (reviews.length > 0) {
  console.log('Geometry review queue:');
  for (const review of reviews) {
    console.log(`- ${review}`);
  }
}

function mergePartialGeometryReport(current) {
  if (outputFile === null || !fs.existsSync(outputFile)) {
    return current;
  }
  const previous = readJson(outputFile);
  if (selectedIds.size > 0) {
    if (previous.caseCount !== current.caseCount) {
      throw new Error(
        'Refusing to overwrite a larger geometry report with selected case IDs',
      );
    }
    return current;
  }
  if (corpusKind === 'all') {
    return current;
  }
  const previousKinds = new Set(
    previous.cases.map((entry) => entry.kind),
  );
  if (previousKinds.size === 1 && previousKinds.has(corpusKind)) {
    return current;
  }
  requireCompatiblePartialReport(previous, current);
  const replacedCaseIds = new Set(
    previous.cases
      .filter((entry) => entry.kind === corpusKind)
      .map((entry) => entry.id),
  );
  const mergedCases = replaceCasesForKind(
    previous.cases,
    current.cases,
    corpusKind,
  );
  return {
    ...previous,
    ...current,
    schemaVersion: 2,
    ...mergedInputDirectoryMetadata(
      previous,
      current,
      mergedCases,
      corpusKind,
    ),
    corpusKind: 'all',
    caseCount: mergedCases.length,
    failures: replaceMessagesForCaseIds(
      previous.failures,
      current.failures,
      replacedCaseIds,
    ),
    reviews: replaceMessagesForCaseIds(
      previous.reviews,
      current.reviews,
      replacedCaseIds,
    ),
    cases: mergedCases,
  };
}

function requireCompatiblePartialReport(previous, current) {
  for (const key of ['corpusSource', 'theme']) {
    if (previous[key] !== current[key]) {
      throw new Error(
        `Partial geometry audit ${key} mismatch: ` +
          `${previous[key]} != ${current[key]}`,
      );
    }
  }
  if (
    JSON.stringify(previous.thresholds) !==
    JSON.stringify(current.thresholds)
  ) {
    throw new Error('Partial geometry audit thresholds do not match');
  }
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

function validateRatio(label, value, minimum, maximum, reviewReason = null) {
  const result = classifyGeometryRatio({
    label,
    value,
    minimum,
    maximum,
    iterativeLayoutWithMatchingStructure: false,
    reviewReason,
  });
  if (result.status === 'review') {
    reviews.push(result.message);
  } else if (result.status === 'failure') {
    failures.push(result.message);
  }
}

function readJson(fileName) {
  if (!fs.existsSync(fileName)) {
    throw new Error(`Missing capture manifest: ${fileName}`);
  }
  return JSON.parse(fs.readFileSync(fileName, 'utf8'));
}

async function analyzePng(page, fileName) {
  if (!fs.existsSync(fileName)) {
    throw new Error(`Missing capture: ${fileName}`);
  }
  const manifestFileName = fileName.replace(/\.png$/, '.manifest.json');
  const manifest = fs.existsSync(manifestFileName)
    ? JSON.parse(fs.readFileSync(manifestFileName, 'utf8'))
    : null;
  const viewportBackground = manifest?.viewport?.background;
  const manifestBackground = viewportBackground == null
    ? null
    : [
        viewportBackground.r,
        viewportBackground.g,
        viewportBackground.b,
        viewportBackground.a,
      ];
  const source = `data:image/png;base64,${fs.readFileSync(fileName).toString('base64')}`;
  return page.evaluate(async ({ imageSource, manifestBackground }) => {
    const colorDistance = (left, right) =>
      Math.abs(left[0] - right[0]) +
      Math.abs(left[1] - right[1]) +
      Math.abs(left[2] - right[2]) +
      Math.abs(left[3] - right[3]);
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
    const backgrounds = [background];
    if (
      manifestBackground !== null &&
      colorDistance(background, manifestBackground) > 24
    ) {
      backgrounds.push(manifestBackground);
    }
    let minimumX = image.width;
    let minimumY = image.height;
    let maximumX = -1;
    let maximumY = -1;
    let inkPixels = 0;
    for (let y = 0; y < image.height; y += 1) {
      for (let x = 0; x < image.width; x += 1) {
        const index = (y * image.width + x) * 4;
        let distance = Number.POSITIVE_INFINITY;
        for (const candidate of backgrounds) {
          distance = Math.min(
            distance,
            Math.abs(pixels[index] - candidate[0]) +
              Math.abs(pixels[index + 1] - candidate[1]) +
              Math.abs(pixels[index + 2] - candidate[2]) +
              Math.abs(pixels[index + 3] - candidate[3]),
          );
        }
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
    const gridSize = 96;
    const evidenceBackground = manifestBackground ?? background;
    const normalizedCanvas = document.createElement('canvas');
    normalizedCanvas.width = gridSize;
    normalizedCanvas.height = gridSize;
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
      gridSize,
      gridSize,
    );
    const normalizedPixels = normalizedContext.getImageData(
      0,
      0,
      gridSize,
      gridSize,
    ).data;
    const mask = [];
    const colors = [];
    const luminance = [];
    for (let index = 0; index < normalizedPixels.length; index += 4) {
      const pixelDistance =
        Math.abs(normalizedPixels[index] - evidenceBackground[0]) +
        Math.abs(normalizedPixels[index + 1] - evidenceBackground[1]) +
        Math.abs(normalizedPixels[index + 2] - evidenceBackground[2]) +
        Math.abs(normalizedPixels[index + 3] - evidenceBackground[3]);
      mask.push(pixelDistance > 24);
      colors.push(
        normalizedPixels[index],
        normalizedPixels[index + 1],
        normalizedPixels[index + 2],
      );
      luminance.push(
        normalizedPixels[index] * 0.2126 +
        normalizedPixels[index + 1] * 0.7152 +
        normalizedPixels[index + 2] * 0.0722,
      );
    }
    const edges = mask.map((foreground, index) => {
      const x = index % gridSize;
      const y = Math.floor(index / gridSize);
      if (
        x === 0 ||
        y === 0 ||
        x + 1 === gridSize ||
        y + 1 === gridSize
      ) {
        return false;
      }
      const horizontal =
        Math.abs(luminance[index - 1] - luminance[index + 1]);
      const vertical =
        Math.abs(
          luminance[index - gridSize] - luminance[index + gridSize],
        );
      const maskBoundary =
        mask[index - 1] !== foreground ||
        mask[index + 1] !== foreground ||
        mask[index - gridSize] !== foreground ||
        mask[index + gridSize] !== foreground;
      return horizontal + vertical > 48 || maskBoundary;
    });
    return {
      gridSize,
      width: image.width,
      height: image.height,
      background: evidenceBackground,
      backgroundColors: backgrounds,
      inkPixels,
      inkRatio: inkPixels / (image.width * image.height),
      contentBounds,
      mask,
      edges,
      colors,
    };
  }, { imageSource: source, manifestBackground });
}

function geometrySummary(value) {
  const { mask, edges, colors, ...summary } = value;
  return summary;
}

function ratio(numerator, denominator) {
  return denominator === 0 ? Number.NaN : numerator / denominator;
}

function within(value, minimum, maximum) {
  return Number.isFinite(value) && value >= minimum && value <= maximum;
}

function manifestGeometryBounds(manifest) {
  const bounds = (manifest.elements ?? [])
    .filter((element) =>
      element.type !== 'text' &&
      Number.isFinite(element.bounds?.x) &&
      Number.isFinite(element.bounds?.y) &&
      Number.isFinite(element.bounds?.width) &&
      Number.isFinite(element.bounds?.height) &&
      element.bounds.width >= 0 &&
      element.bounds.height >= 0
    )
    .map((element) => element.bounds);
  if (bounds.length === 0) {
    return { width: Number.NaN, height: Number.NaN };
  }
  const minimumX = Math.min(...bounds.map((entry) => entry.x));
  const minimumY = Math.min(...bounds.map((entry) => entry.y));
  const maximumX = Math.max(
    ...bounds.map((entry) => entry.x + entry.width),
  );
  const maximumY = Math.max(
    ...bounds.map((entry) => entry.y + entry.height),
  );
  return {
    width: maximumX - minimumX,
    height: maximumY - minimumY,
  };
}

function range(values) {
  const minimum = Math.min(...values);
  const maximum = Math.max(...values);
  return `${minimum.toFixed(3)}-${maximum.toFixed(3)}`;
}

function capturePrefix(auditCase, themeSuffix) {
  const filePrefix = `${auditCase.id}${themeSuffix}`;
  const directPrefix = path.join(inputDirectory, filePrefix);
  if (fs.existsSync(`${directPrefix}_native.png`)) {
    return directPrefix;
  }
  return path.join(inputDirectory, auditCase.kind, filePrefix);
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

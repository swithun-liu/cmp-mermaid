#!/usr/bin/env node

import fs from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

const repositoryRoot = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
const sourceMapPath = path.join(
  repositoryRoot,
  "build/upstream/zenuml-core-3.49.2/dist/cli/zenuml.mjs.map",
);
const outputPath = path.join(
  repositoryRoot,
  "mermaid-core/src/commonMain/kotlin/com/swithun/cmpmermaid/core/zenuml/ZenUmlParticipantVectors.kt",
);

const sourceMap = JSON.parse(fs.readFileSync(sourceMapPath, "utf8"));
const assets = new Map([
  ["actor", "actor.svg?raw"],
  ["boundary", "Robustness_Diagram_Boundary.svg?raw"],
  ["control", "Robustness_Diagram_Control.svg?raw"],
  ["database", "database.svg?raw"],
  ["entity", "Robustness_Diagram_Entity.svg?raw"],
  ["ec2", "Res_Amazon-EC2_Instance_48.svg?raw"],
  ["iam", "Res_AWS-Identity-Access-Management_IAM-Access-Analyzer_48.svg?raw"],
  ["lambda", "Res_AWS-Lambda_Lambda-Function_48.svg?raw"],
  ["sns", "Res_Amazon-Simple-Notification-Service_Topic_48.svg?raw"],
  ["sqs", "Res_Amazon-Simple-Queue-Service_Queue_48.svg?raw"],
  ["azurefunction", "10029-icon-service-Function-Apps.svg?raw"],
]);

function findSource(suffix) {
  const index = sourceMap.sources.findIndex((source) => source.endsWith(suffix));
  if (index < 0) {
    throw new Error(`Missing source-map input: ${suffix}`);
  }
  return sourceMap.sourcesContent[index];
}

function decodeRawSvg(source) {
  const match = source.trim().match(/^export default ("[\s\S]*")$/);
  if (!match) {
    throw new Error("Invalid raw SVG module");
  }
  return JSON.parse(match[1]);
}

function attribute(source, name) {
  return source.match(new RegExp(`\\b${name}="([^"]+)"`, "i"))?.[1];
}

function colorExpression(value) {
  if (value === "currentColor") {
    return "ZEN_UML_VECTOR_CURRENT_COLOR";
  }
  if (value?.startsWith("#")) {
    return `SceneColor(0xFF${value.slice(1).toUpperCase()}L)`;
  }
  if (value?.startsWith("url(")) {
    return null;
  }
  return null;
}

function floatLiteral(value) {
  const number = Number(value);
  if (!Number.isFinite(number)) {
    throw new Error(`Invalid float: ${value}`);
  }
  return `${number}f`;
}

function transformExpression(value) {
  if (!value) {
    return null;
  }
  const match = value.match(
    /^translate\(([-+\d.]+)[ ,]+([-+\d.]+)\)\s+rotate\(([-+\d.]+)\)$/,
  );
  if (!match) {
    throw new Error(`Unsupported SVG transform: ${value}`);
  }
  const translateX = Number(match[1]);
  const translateY = Number(match[2]);
  const radians = Number(match[3]) * Math.PI / 180;
  const cosine = Math.cos(radians);
  const sine = Math.sin(radians);
  return [
    "SceneAffineTransform(",
    `        scaleX = ${floatLiteral(cosine)},`,
    `        skewY = ${floatLiteral(sine)},`,
    `        skewX = ${floatLiteral(-sine)},`,
    `        scaleY = ${floatLiteral(cosine)},`,
    `        translateX = ${floatLiteral(translateX)},`,
    `        translateY = ${floatLiteral(translateY)},`,
    "    )",
  ].join("\n");
}

function pathExpression(attributes, inheritedEvenOdd) {
  const pathData = attribute(attributes, "d");
  if (!pathData) {
    throw new Error("SVG path is missing d");
  }
  const fill = attribute(attributes, "fill");
  const sourceStroke = attribute(attributes, "stroke");
  const inheritedCurrentColorStroke = fill === "currentColor" && !sourceStroke;
  const stroke = sourceStroke ?? (inheritedCurrentColorStroke ? "participantBorder" : null);
  const fillColor = colorExpression(fill);
  const strokeColor = inheritedCurrentColorStroke
    ? "ZEN_UML_VECTOR_BORDER_COLOR"
    : colorExpression(stroke);
  const fillGradient = fill?.startsWith("url(") ? "AZURE_FUNCTION_GRADIENT" : null;
  const strokeWidth = attribute(attributes, "stroke-width");
  const strokeCap = attribute(attributes, "stroke-linecap");
  const strokeJoin = attribute(attributes, "stroke-linejoin");
  const fillRule = attribute(attributes, "fill-rule");
  const transform = transformExpression(attribute(attributes, "transform"));
  const lines = [
    "SceneShapePath(",
    `    pathData = ${JSON.stringify(pathData)},`,
    `    fill = SceneShapePaint.${fill && fill !== "none" ? "Fill" : "None"},`,
    `    stroke = SceneShapePaint.${stroke && stroke !== "none" ? "Stroke" : "None"},`,
  ];
  if (fillColor) lines.push(`    fillColor = ${fillColor},`);
  if (fillGradient) lines.push(`    fillGradient = ${fillGradient},`);
  if (strokeColor) lines.push(`    strokeColor = ${strokeColor},`);
  if (stroke && stroke !== "none") {
    lines.push(`    strokeWidth = ${floatLiteral(strokeWidth ?? "1")},`);
  }
  if (strokeCap === "round") lines.push("    strokeCap = SceneStrokeCap.Round,");
  if (strokeJoin === "round") lines.push("    strokeJoin = SceneStrokeJoin.Round,");
  if (fillRule === "evenodd" || inheritedEvenOdd) {
    lines.push("    fillRule = ScenePathFillRule.EvenOdd,");
  }
  if (transform) lines.push(`    transform = ${transform},`);
  lines.push(")");
  return lines.join("\n");
}

const definitions = [];
for (const [type, suffix] of assets) {
  const svg = decodeRawSvg(findSource(suffix));
  const viewBox = attribute(svg, "viewBox")?.split(/\s+/).map(Number);
  if (!viewBox || viewBox.length !== 4 || viewBox.some((value) => !Number.isFinite(value))) {
    throw new Error(`Invalid viewBox for ${type}`);
  }
  const inheritedEvenOdd = /<g\b[^>]*\bfill-rule="evenodd"/i.test(svg);
  const paths = [...svg.matchAll(/<path\b([^>]*)\/?>/gi)].map((match) =>
    pathExpression(match[1], inheritedEvenOdd),
  );
  definitions.push(
    `        ${JSON.stringify(type)} to ZenUmlVectorDefinition(\n` +
      `            viewBox = SceneRect(${viewBox.map(floatLiteral).join(", ")}),\n` +
      `            paths = listOf(\n${paths.map((item) => item.replace(/^/gm, "                ")).join(",\n")},\n` +
      "            ),\n" +
      "            viewportFit = SceneShapeViewportFit.MeetStart,\n" +
      "        )",
  );
}

const output = `package com.swithun.cmpmermaid.core.zenuml

import com.swithun.cmpmermaid.core.SceneAffineTransform
import com.swithun.cmpmermaid.core.SceneColor
import com.swithun.cmpmermaid.core.SceneGradientStop
import com.swithun.cmpmermaid.core.SceneLinearGradient
import com.swithun.cmpmermaid.core.ScenePathFillRule
import com.swithun.cmpmermaid.core.ScenePoint
import com.swithun.cmpmermaid.core.SceneRect
import com.swithun.cmpmermaid.core.SceneShapePaint
import com.swithun.cmpmermaid.core.SceneShapePath
import com.swithun.cmpmermaid.core.SceneShapeViewportFit
import com.swithun.cmpmermaid.core.SceneStrokeCap
import com.swithun.cmpmermaid.core.SceneStrokeJoin

/**
 * Generated from @zenuml/core 3.49.2:
 * src/assets SVG files and src/svg/icons.ts.
 *
 * Regenerate with tools/generate-zenuml-participant-vectors.mjs.
 */
internal object ZenUmlParticipantVectors {
    private val definitions = mapOf(
${definitions.join(",\n")}
    )

    fun find(type: String?): ZenUmlVectorDefinition? =
        type?.lowercase()?.let(definitions::get)
}

internal data class ZenUmlVectorDefinition(
    val viewBox: SceneRect,
    val paths: List<SceneShapePath>,
    val viewportFit: SceneShapeViewportFit,
)

private val ZEN_UML_VECTOR_CURRENT_COLOR = SceneColor(0xFF222222)
private val ZEN_UML_VECTOR_BORDER_COLOR = SceneColor(0xFF666666)
private val AZURE_FUNCTION_GRADIENT = SceneLinearGradient(
    startColor = SceneColor(0xFFFEA11B),
    endColor = SceneColor(0xFFFFD70F),
    start = ScenePoint(0.50694954f, 0.93650186f),
    end = ScenePoint(0.50694954f, 0.049904f),
    colorStops = listOf(
        SceneGradientStop(0f, SceneColor(0xFFFEA11B)),
        SceneGradientStop(0.284f, SceneColor(0xFFFEA51A)),
        SceneGradientStop(0.547f, SceneColor(0xFFFEB018)),
        SceneGradientStop(0.8f, SceneColor(0xFFFFC314)),
        SceneGradientStop(1f, SceneColor(0xFFFFD70F)),
    ),
)
`;

fs.writeFileSync(outputPath, output);
console.log(`Generated ${path.relative(repositoryRoot, outputPath)}`);

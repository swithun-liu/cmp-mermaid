package io.github.cmpmermaid.core.flowchart

import io.github.cmpmermaid.core.GMResult
import io.github.cmpmermaid.core.MermaidError
import io.github.cmpmermaid.core.MermaidRenderOptions
import io.github.cmpmermaid.core.SceneArrowHead
import io.github.cmpmermaid.core.SceneColor
import io.github.cmpmermaid.core.SceneShapeKind
import io.github.cmpmermaid.core.SceneStrokePattern
import io.github.cmpmermaid.core.SceneTextWeight
import io.github.cmpmermaid.core.flowchart.upstream.mermaid.FlowDb
import io.github.cmpmermaid.core.flowchart.upstream.mermaid.FlowLabelType
import io.github.cmpmermaid.core.flowchart.upstream.mermaid.FlowShapeRegistry
import io.github.cmpmermaid.core.flowchart.upstream.mermaid.MermaidRenderedText
import io.github.cmpmermaid.core.flowchart.upstream.mermaid.MermaidTextPort

/**
 * Native data adapter for the output of Mermaid FlowDB.getData().
 *
 * Mermaid syntax and FlowDB semantics stay in the upstream port. This adapter
 * only converts Mermaid renderer data to the platform-neutral SceneGraph model.
 */
internal object FlowchartDataAdapter {
    fun convert(db: FlowDb): GMResult<FlowchartDocument, MermaidError> {
        val data = db.getData()
        val config = db.config()
        val nodes = linkedMapOf<String, FlowNode>()
        data.nodes.filterNot { it.isGroup }.forEach { node ->
            val shape = when (val mapped = shapeKind(node.shape)) {
                is GMResult.Ok -> mapped.value
                is GMResult.Err -> return mapped
            }
            val style = when (
                val parsed = FlowStyleAdapter.parse(
                    styles = node.cssCompiledStyles + node.cssStyles,
                    owner = "node '${node.id}'",
                )
            ) {
                is GMResult.Ok -> parsed.value
                is GMResult.Err -> return parsed
            }
            val label = when (
                val rendered = MermaidTextPort.render(
                    source = node.label.orEmpty(),
                    labelType = node.labelType,
                    config = config,
                )
            ) {
                is GMResult.Ok -> rendered.value
                is GMResult.Err -> return rendered
            }
            val tooltip = when (
                val rendered = node.tooltip?.let { source ->
                    MermaidTextPort.render(source, FlowLabelType.Text, config)
                }
            ) {
                null -> null
                is GMResult.Ok -> rendered.value.text
                is GMResult.Err -> return rendered
            }
            nodes[node.id] = FlowNode(
                id = node.id,
                label = label.text,
                labelSpans = label.spans,
                labelType = node.labelType,
                shape = shape,
                padding = node.padding,
                minWidth = node.minWidth,
                look = node.look,
                inlineStyle = style,
                metadata = node.metadata,
                link = node.link,
                linkTarget = node.linkTarget,
                tooltip = tooltip,
                icon = node.icon,
                position = node.position,
                image = node.image,
                assetWidth = node.assetWidth,
                assetHeight = node.assetHeight,
                constraint = node.constraint,
            )
        }

        val subgraphs = buildList {
            data.nodes.filter { it.isGroup }.forEach { group ->
                val label = when (
                    val rendered = MermaidTextPort.render(
                        source = group.label.orEmpty(),
                        labelType = group.labelType,
                        config = config,
                    )
                ) {
                    is GMResult.Ok -> rendered.value
                    is GMResult.Err -> return rendered
                }
                val style = when (
                    val parsed = FlowStyleAdapter.parse(
                        styles = group.cssCompiledStyles + group.cssStyles,
                        owner = "subgraph '${group.id}'",
                    )
                ) {
                    is GMResult.Ok -> parsed.value
                    is GMResult.Err -> return parsed
                }
                add(
                    FlowSubgraph(
                        id = group.id,
                        label = label.text,
                        labelSpans = label.spans,
                        nodeIds = data.nodes
                            .asSequence()
                            .filter { node -> node.parentId == group.id }
                            .mapTo(linkedSetOf()) { node -> node.id },
                        direction = direction(group.dir),
                        parentId = group.parentId,
                        padding = group.padding,
                        look = group.look,
                        inlineStyle = style,
                    ),
                )
            }
        }

        val edges = buildList {
            data.edges.forEach { edge ->
                val style = when (
                    val parsed = FlowStyleAdapter.parse(
                        styles = edge.cssCompiledStyles + edge.styles,
                        owner = "edge '${edge.id}'",
                    )
                ) {
                    is GMResult.Ok -> parsed.value
                    is GMResult.Err -> return parsed
                }
                val renderedLabel = when (
                    val rendered = MermaidTextPort.render(
                        source = edge.label,
                        labelType = edge.labelType,
                        config = config,
                    )
                ) {
                    is GMResult.Ok -> rendered.value
                    is GMResult.Err -> return rendered
                }
                add(
                    FlowEdge(
                        id = edge.id,
                        from = edge.start,
                        to = edge.end,
                        label = renderedLabel.text.ifEmpty { null },
                        labelSpans = renderedLabel.spans,
                        pattern = when (edge.pattern) {
                            "dotted" -> SceneStrokePattern.Dotted
                            else -> SceneStrokePattern.Solid
                        },
                        arrowStart = arrowHead(edge.arrowTypeStart),
                        arrowEnd = arrowHead(edge.arrowTypeEnd),
                        thickness = if (edge.thickness == "thick") 3.5f else 1f,
                        minimumLength = edge.minimumLength ?: 1,
                        invisible = edge.thickness == "invisible",
                        inlineStyle = style,
                        animated = edge.animate == true ||
                            edge.animation == "fast" ||
                            edge.animation == "slow",
                        curve = edge.curve,
                        look = edge.look,
                    ),
                )
            }
        }
        val title = when (val rendered = renderOptional(db.diagramTitle, config)) {
            is GMResult.Ok -> rendered.value
            is GMResult.Err -> return rendered
        }
        val accessibilityTitle = when (
            val rendered = renderOptional(db.accessibilityTitle, config)
        ) {
            is GMResult.Ok -> rendered.value
            is GMResult.Err -> return rendered
        }
        val accessibilityDescription = when (
            val rendered = renderOptional(db.accessibilityDescription, config)
        ) {
            is GMResult.Ok -> rendered.value
            is GMResult.Err -> return rendered
        }

        return GMResult.Ok(
            FlowchartDocument(
                direction = direction(db.getDirection()) ?: FlowDirection.TopToBottom,
                nodes = nodes,
                edges = edges,
                subgraphs = subgraphs,
                title = title?.text,
                accessibilityTitle = accessibilityTitle?.text,
                accessibilityDescription = accessibilityDescription?.text,
            ),
        )
    }

    private fun renderOptional(
        source: String?,
        config: MermaidRenderOptions,
    ): GMResult<MermaidRenderedText?, MermaidError> =
        if (source == null) {
            GMResult.Ok(null)
        } else {
            MermaidTextPort.render(source, FlowLabelType.Text, config)
        }

    private fun direction(value: String?): FlowDirection? = when (value) {
        "TB", "TD" -> FlowDirection.TopToBottom
        "BT" -> FlowDirection.BottomToTop
        "LR" -> FlowDirection.LeftToRight
        "RL" -> FlowDirection.RightToLeft
        else -> null
    }

    private fun arrowHead(value: String): SceneArrowHead = when (value) {
        "arrow_point" -> SceneArrowHead.Triangle
        "arrow_circle" -> SceneArrowHead.Circle
        "arrow_cross" -> SceneArrowHead.Cross
        else -> SceneArrowHead.None
    }

    private fun shapeKind(shapeId: String): GMResult<SceneShapeKind, MermaidError> {
        val handler = FlowShapeRegistry.handlerFor(shapeId) ?: shapeId
        val kind = when (handler) {
            "squareRect" -> SceneShapeKind.Rectangle
            "roundedRect" -> SceneShapeKind.RoundedRectangle
            "collapsedGroup" -> SceneShapeKind.CollapsedGroup
            "stadium" -> SceneShapeKind.Stadium
            "subroutine" -> SceneShapeKind.Subroutine
            "cylinder" -> SceneShapeKind.Cylinder
            "datastore" -> SceneShapeKind.Datastore
            "folder" -> SceneShapeKind.Folder
            "bucket" -> SceneShapeKind.Bucket
            "consoleWindow" -> SceneShapeKind.Console
            "browser" -> SceneShapeKind.Browser
            "person" -> SceneShapeKind.Person
            "circle" -> SceneShapeKind.Circle
            "bang" -> SceneShapeKind.Bang
            "cloud" -> SceneShapeKind.Cloud
            "doublecircle" -> SceneShapeKind.DoubleCircle
            "question" -> SceneShapeKind.Diamond
            "hexagon" -> SceneShapeKind.Hexagon
            "lean_right" -> SceneShapeKind.Parallelogram
            "lean_left" -> SceneShapeKind.ParallelogramAlt
            "trapezoid" -> SceneShapeKind.Trapezoid
            "inv_trapezoid" -> SceneShapeKind.TrapezoidAlt
            "rect_left_inv_arrow" -> SceneShapeKind.Asymmetric
            "ellipse" -> SceneShapeKind.Ellipse
            "text" -> SceneShapeKind.TextBlock
            "card" -> SceneShapeKind.NotchedRectangle
            "shadedProcess" -> SceneShapeKind.LinedRectangle
            "stateStart" -> SceneShapeKind.SmallCircle
            "stateEnd" -> SceneShapeKind.FramedCircle
            "forkJoin" -> SceneShapeKind.ForkJoin
            "hourglass" -> SceneShapeKind.Hourglass
            "curlyBraceLeft" -> SceneShapeKind.BraceLeft
            "curlyBraceRight" -> SceneShapeKind.BraceRight
            "curlyBraces" -> SceneShapeKind.Braces
            "lightningBolt" -> SceneShapeKind.Bolt
            "waveEdgedRectangle" -> SceneShapeKind.Document
            "halfRoundedRectangle" -> SceneShapeKind.Delay
            "tiltedCylinder" -> SceneShapeKind.DirectAccessStorage
            "linedCylinder" -> SceneShapeKind.LinedCylinder
            "curvedTrapezoid" -> SceneShapeKind.CurvedTrapezoid
            "dividedRectangle" -> SceneShapeKind.DividedRectangle
            "triangle" -> SceneShapeKind.Triangle
            "windowPane" -> SceneShapeKind.WindowPane
            "filledCircle" -> SceneShapeKind.FilledCircle
            "linedWaveEdgedRect" -> SceneShapeKind.LinedDocument
            "trapezoidalPentagon" -> SceneShapeKind.NotchedPentagon
            "flippedTriangle" -> SceneShapeKind.FlippedTriangle
            "slopedRect" -> SceneShapeKind.SlopedRectangle
            "multiWaveEdgedRectangle" -> SceneShapeKind.MultiDocument
            "multiRect" -> SceneShapeKind.MultiProcess
            "waveRectangle" -> SceneShapeKind.PaperTape
            "bowTieRect" -> SceneShapeKind.BowTieRectangle
            "crossedCircle" -> SceneShapeKind.CrossedCircle
            "taggedWaveEdgedRectangle" -> SceneShapeKind.TaggedDocument
            "taggedRect" -> SceneShapeKind.TaggedRectangle
            "icon" -> SceneShapeKind.Icon
            "iconCircle" -> SceneShapeKind.IconCircle
            "iconSquare" -> SceneShapeKind.IconSquare
            "iconRounded" -> SceneShapeKind.IconRounded
            "imageSquare" -> SceneShapeKind.Image
            else -> return GMResult.Err(
                MermaidError.Layout(
                    "Native shape adapter is missing Mermaid handler '$handler' for '$shapeId'",
                ),
            )
        }
        return GMResult.Ok(kind)
    }
}

/**
 * Native CSS adapter for Mermaid's ordered cssCompiledStyles + cssStyles data.
 * The last declaration for a property wins, matching styles2Map().
 */
private object FlowStyleAdapter {
    fun parse(
        styles: List<String>,
        owner: String,
    ): GMResult<FlowNodeStyle, MermaidError> {
        val declarations = linkedMapOf<String, String>()
        styles
            .flatMap(::normalizeStyleList)
            .forEach { style ->
                val pair = style.split(':')
                val key = pair.firstOrNull()?.trim().orEmpty()
                if (key.isNotEmpty()) {
                    declarations[key] = pair.getOrNull(1)?.trim().orEmpty()
                }
            }

        val unsupported = declarations.keys - SUPPORTED_PROPERTIES
        if (unsupported.isNotEmpty()) {
            return GMResult.Err(
                MermaidError.UnsupportedFeature(
                    feature = "CSS ${unsupported.sorted().joinToString()}",
                    message = "Native Mermaid has not translated CSS " +
                        "${unsupported.sorted().joinToString()} on $owner",
                ),
            )
        }
        val dashSource = declarations["stroke-dasharray"]
        val dashIntervals = parseDashArray(dashSource)
        if (dashSource != null && dashIntervals == null) {
            return invalidCss(owner, "stroke-dasharray", dashSource)
        }
        val fontSize = when (val value = declarations["font-size"]) {
            null -> null
            else -> parsePixelNumber(value)
                ?: return invalidCss(owner, "font-size", value)
        }
        val strokeWidth = when (val value = declarations["stroke-width"]) {
            null -> null
            else -> parsePixelNumber(value)
                ?: return invalidCss(owner, "stroke-width", value)
        }
        return GMResult.Ok(FlowNodeStyle(
            fill = declarations["fill"]?.let(::parsePaintColor),
            stroke = declarations["stroke"]?.let(::parsePaintColor),
            text = declarations["color"]?.let(CssColorParser::parse),
            labelBackground = declarations["background"]?.let(::parsePaintColor),
            strokeWidth = strokeWidth,
            strokePattern = dashIntervals
                ?.takeIf(List<Float>::isNotEmpty)
                ?.let { SceneStrokePattern.Dashed },
            dashIntervals = dashIntervals.orEmpty(),
            fontSize = fontSize,
            fontWeight = when (declarations["font-weight"]?.lowercase()) {
                "bold", "600", "700", "800", "900" -> SceneTextWeight.Bold
                "normal", "100", "200", "300", "400", "500" -> SceneTextWeight.Normal
                null -> null
                else -> return invalidCss(
                    owner,
                    "font-weight",
                    declarations.getValue("font-weight"),
                )
            },
        ))
    }

    private fun normalizeStyleList(source: String): List<String> =
        source.split(';')
            .map(String::trim)
            .filter(String::isNotEmpty)

    private fun parsePaintColor(value: String): SceneColor? = when (value.trim().lowercase()) {
        "none" -> SceneColor(0x00000000)
        else -> CssColorParser.parse(value)
    }

    private fun parsePixelNumber(value: String): Float? {
        val normalized = value.trim()
        return when {
            normalized.endsWith("px", ignoreCase = true) ->
                normalized.dropLast(2).trim().toFloatOrNull()
            NUMBER.matches(normalized) -> normalized.toFloatOrNull()
            else -> null
        }
    }

    private fun parseDashArray(value: String?): List<Float>? {
        if (value == null || value.trim().equals("none", ignoreCase = true)) {
            return emptyList()
        }
        val parsed = value
            .trim()
            .split(DASH_SEPARATOR)
            .filter(String::isNotEmpty)
            .map { component ->
                component.removeSuffix("px").toFloatOrNull() ?: return null
            }
        if (parsed.isEmpty() || parsed.any { it < 0f }) {
            return null
        }
        return if (parsed.size % 2 == 0) parsed else parsed + parsed
    }

    private fun invalidCss(
        owner: String,
        property: String,
        value: String,
    ): GMResult<FlowNodeStyle, MermaidError> =
        GMResult.Err(
            MermaidError.UnsupportedFeature(
                feature = "CSS $property",
                message = "Native Mermaid cannot represent CSS '$property: $value' on $owner",
            ),
        )

    private val SUPPORTED_PROPERTIES = setOf(
        "background",
        "color",
        "fill",
        "font-size",
        "font-weight",
        "stroke",
        "stroke-dasharray",
        "stroke-width",
    )
    private val NUMBER = Regex("""^[+-]?(?:\d+(?:\.\d*)?|\.\d+)$""")
    private val DASH_SEPARATOR = Regex("""[\s,]+""")
}

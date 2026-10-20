package com.swithun.cmpmermaid.core

data class SceneColor(
    val argb: Long,
)

enum class SceneShapeKind {
    Rectangle,
    RoundedRectangle,
    CollapsedGroup,
    Stadium,
    Subroutine,
    Cylinder,
    Circle,
    DoubleCircle,
    Diamond,
    Hexagon,
    Parallelogram,
    ParallelogramAlt,
    Trapezoid,
    TrapezoidAlt,
    Asymmetric,
    Ellipse,
    TextBlock,
    NotchedRectangle,
    LinedRectangle,
    SmallCircle,
    FramedCircle,
    ForkJoin,
    Hourglass,
    BraceLeft,
    BraceRight,
    Braces,
    Bolt,
    Document,
    Delay,
    DirectAccessStorage,
    LinedCylinder,
    CurvedTrapezoid,
    DividedRectangle,
    Triangle,
    WindowPane,
    FilledCircle,
    LinedDocument,
    NotchedPentagon,
    FlippedTriangle,
    SlopedRectangle,
    MultiDocument,
    MultiProcess,
    PaperTape,
    BowTieRectangle,
    CrossedCircle,
    TaggedDocument,
    TaggedRectangle,
    Icon,
    IconCircle,
    IconSquare,
    IconRounded,
    Image,
    Datastore,
    Folder,
    Bucket,
    Console,
    Browser,
    Person,
    Bang,
    Cloud,
}

enum class SceneStrokePattern {
    Solid,
    Dashed,
    Dotted,
}

enum class SceneBlendMode {
    SourceOver,
    Multiply,
}

enum class SceneArrowHead {
    None,
    Triangle,
    Circle,
    Cross,
    Open,
    Async,
    SequenceCross,
    HalfTriangleTop,
    HalfTriangleBottom,
    HalfOpenTop,
    HalfOpenBottom,
    ClassAggregation,
    ClassExtension,
    ClassComposition,
    ClassDependency,
    ClassLollipop,
    ErOnlyOne,
    ErZeroOrOne,
    ErOneOrMore,
    ErZeroOrMore,
    RequirementArrow,
    RequirementContains,
}

sealed interface SceneElement {
    val zIndex: Int
}

data class SceneShadow(
    val color: SceneColor,
    val offsetX: Float,
    val offsetY: Float,
    val blurRadius: Float = 0f,
)

data class SceneLinearGradient(
    val startColor: SceneColor,
    val endColor: SceneColor,
    val start: ScenePoint = ScenePoint(0f, 0f),
    val end: ScenePoint = ScenePoint(1f, 0f),
    val colorStops: List<SceneGradientStop> = emptyList(),
)

data class SceneGradientStop(
    val offset: Float,
    val color: SceneColor,
)

data class SceneShape(
    val id: String,
    val bounds: SceneRect,
    val kind: SceneShapeKind,
    val geometry: SceneShapeGeometry? = null,
    val fill: SceneColor,
    val stroke: SceneColor,
    val strokeWidth: Float = 1.5f,
    val strokePattern: SceneStrokePattern = SceneStrokePattern.Solid,
    val dashIntervals: List<Float> = emptyList(),
    val cornerRadius: Float = 8f,
    val shadow: SceneShadow? = null,
    val strokeGradient: SceneLinearGradient? = null,
    override val zIndex: Int = 10,
) : SceneElement

enum class SceneAssetKind {
    Icon,
    Image,
}

data class SceneAsset(
    val id: String,
    val source: String,
    val bounds: SceneRect,
    val kind: SceneAssetKind,
    val tint: SceneColor? = null,
    override val zIndex: Int = 15,
) : SceneElement

enum class SceneShapePaint {
    None,
    Fill,
    Stroke,
}

enum class ScenePathFillRule {
    NonZero,
    EvenOdd,
}

enum class SceneStrokeCap {
    Butt,
    Round,
    Square,
}

enum class SceneStrokeJoin {
    Miter,
    Round,
    Bevel,
}

enum class SceneShapeViewportFit {
    Stretch,
    MeetStart,
    MeetCenter,
}

data class SceneAffineTransform(
    val scaleX: Float = 1f,
    val skewY: Float = 0f,
    val skewX: Float = 0f,
    val scaleY: Float = 1f,
    val translateX: Float = 0f,
    val translateY: Float = 0f,
)

data class SceneShapePath(
    val points: List<ScenePoint> = emptyList(),
    val closed: Boolean = true,
    val fill: SceneShapePaint = SceneShapePaint.Fill,
    val stroke: SceneShapePaint = SceneShapePaint.Stroke,
    val strokeWidth: Float? = null,
    val strokePattern: SceneStrokePattern = SceneStrokePattern.Solid,
    val dashIntervals: List<Float> = emptyList(),
    val opacity: Float = 1f,
    val pathData: String? = null,
    val fillRule: ScenePathFillRule = ScenePathFillRule.NonZero,
    val fillColor: SceneColor? = null,
    val fillGradient: SceneLinearGradient? = null,
    val strokeColor: SceneColor? = null,
    val strokeCap: SceneStrokeCap = SceneStrokeCap.Butt,
    val strokeJoin: SceneStrokeJoin = SceneStrokeJoin.Miter,
    val transform: SceneAffineTransform? = null,
)

/**
 * Shape paths use coordinates relative to the center of [SceneShape.bounds].
 * The outline is the exact Mermaid intersection boundary for the rendered node.
 */
data class SceneShapeGeometry(
    val paths: List<SceneShapePath>,
    val outline: List<ScenePoint>,
    val viewBox: SceneRect? = null,
    val viewportFit: SceneShapeViewportFit = SceneShapeViewportFit.Stretch,
)

class SceneText(
    val text: String,
    val bounds: SceneRect,
    val color: SceneColor,
    val fontSize: Float,
    val lineHeight: Float = 1.2f,
    val fontFamily: String? = null,
    val weight: SceneTextWeight = SceneTextWeight.Medium,
    val italic: Boolean = false,
    val spans: List<SceneTextSpan> = emptyList(),
    val horizontalAlignment: SceneTextAlignment = SceneTextAlignment.Center,
    val rotationDegrees: Float = 0f,
    val rotationPivot: ScenePoint? = null,
    override val zIndex: Int = 20,
    val softWrap: Boolean = true,
    val clipToBounds: Boolean = false,
    val horizontalScale: Float? = null,
    val outlineColor: SceneColor? = null,
    val outlineWidth: Float = 0f,
    val maxLines: Int? = null,
    val overflowEllipsis: Boolean = false,
    val htmlLabel: Boolean = false,
    // CSS layout width when it differs from the measured glyph bounds.
    val wrappingWidth: Float? = null,
) : SceneElement {
    var formula: MermaidFormulaMeasureRequest? = null
        private set

    fun withFormula(formula: MermaidFormulaMeasureRequest?): SceneText =
        copy().also { result -> result.formula = formula }

    operator fun component1(): String = text
    operator fun component2(): SceneRect = bounds
    operator fun component3(): SceneColor = color
    operator fun component4(): Float = fontSize
    operator fun component5(): Float = lineHeight
    operator fun component6(): String? = fontFamily
    operator fun component7(): SceneTextWeight = weight
    operator fun component8(): Boolean = italic
    operator fun component9(): List<SceneTextSpan> = spans
    operator fun component10(): SceneTextAlignment = horizontalAlignment
    operator fun component11(): Float = rotationDegrees
    operator fun component12(): ScenePoint? = rotationPivot
    operator fun component13(): Int = zIndex
    operator fun component14(): Boolean = softWrap
    operator fun component15(): Boolean = clipToBounds
    operator fun component16(): Float? = horizontalScale
    operator fun component17(): SceneColor? = outlineColor
    operator fun component18(): Float = outlineWidth
    operator fun component19(): Int? = maxLines
    operator fun component20(): Boolean = overflowEllipsis
    operator fun component21(): Boolean = htmlLabel
    operator fun component22(): Float? = wrappingWidth

    fun copy(
        text: String = this.text,
        bounds: SceneRect = this.bounds,
        color: SceneColor = this.color,
        fontSize: Float = this.fontSize,
        lineHeight: Float = this.lineHeight,
        fontFamily: String? = this.fontFamily,
        weight: SceneTextWeight = this.weight,
        italic: Boolean = this.italic,
        spans: List<SceneTextSpan> = this.spans,
        horizontalAlignment: SceneTextAlignment = this.horizontalAlignment,
        rotationDegrees: Float = this.rotationDegrees,
        rotationPivot: ScenePoint? = this.rotationPivot,
        zIndex: Int = this.zIndex,
        softWrap: Boolean = this.softWrap,
        clipToBounds: Boolean = this.clipToBounds,
        horizontalScale: Float? = this.horizontalScale,
        outlineColor: SceneColor? = this.outlineColor,
        outlineWidth: Float = this.outlineWidth,
        maxLines: Int? = this.maxLines,
        overflowEllipsis: Boolean = this.overflowEllipsis,
        htmlLabel: Boolean = this.htmlLabel,
        wrappingWidth: Float? = this.wrappingWidth,
    ): SceneText = SceneText(
        text = text,
        bounds = bounds,
        color = color,
        fontSize = fontSize,
        lineHeight = lineHeight,
        fontFamily = fontFamily,
        weight = weight,
        italic = italic,
        spans = spans,
        horizontalAlignment = horizontalAlignment,
        rotationDegrees = rotationDegrees,
        rotationPivot = rotationPivot,
        zIndex = zIndex,
        softWrap = softWrap,
        clipToBounds = clipToBounds,
        horizontalScale = horizontalScale,
        outlineColor = outlineColor,
        outlineWidth = outlineWidth,
        maxLines = maxLines,
        overflowEllipsis = overflowEllipsis,
        htmlLabel = htmlLabel,
        wrappingWidth = wrappingWidth,
    ).also { result -> result.formula = formula }

    override fun equals(other: Any?): Boolean =
        this === other ||
            (
                other is SceneText &&
                    text == other.text &&
                    bounds == other.bounds &&
                    color == other.color &&
                    fontSize.compareTo(other.fontSize) == 0 &&
                    lineHeight.compareTo(other.lineHeight) == 0 &&
                    fontFamily == other.fontFamily &&
                    weight == other.weight &&
                    italic == other.italic &&
                    spans == other.spans &&
                    horizontalAlignment == other.horizontalAlignment &&
                    rotationDegrees.compareTo(other.rotationDegrees) == 0 &&
                    rotationPivot == other.rotationPivot &&
                    zIndex == other.zIndex &&
                    softWrap == other.softWrap &&
                    clipToBounds == other.clipToBounds &&
                    horizontalScale == other.horizontalScale &&
                    outlineColor == other.outlineColor &&
                    outlineWidth.compareTo(other.outlineWidth) == 0 &&
                    maxLines == other.maxLines &&
                    overflowEllipsis == other.overflowEllipsis &&
                    htmlLabel == other.htmlLabel &&
                    wrappingWidth == other.wrappingWidth &&
                    formula == other.formula
                )

    override fun hashCode(): Int = listOf(
        text,
        bounds,
        color,
        fontSize,
        lineHeight,
        fontFamily,
        weight,
        italic,
        spans,
        horizontalAlignment,
        rotationDegrees,
        rotationPivot,
        zIndex,
        softWrap,
        clipToBounds,
        horizontalScale,
        outlineColor,
        outlineWidth,
        maxLines,
        overflowEllipsis,
        htmlLabel,
        wrappingWidth,
        formula,
    ).hashCode()

    override fun toString(): String =
        "SceneText(text=$text, bounds=$bounds, color=$color, fontSize=$fontSize, " +
            "lineHeight=$lineHeight, fontFamily=$fontFamily, weight=$weight, italic=$italic, " +
            "spans=$spans, horizontalAlignment=$horizontalAlignment, " +
            "rotationDegrees=$rotationDegrees, rotationPivot=$rotationPivot, zIndex=$zIndex, " +
            "softWrap=$softWrap, clipToBounds=$clipToBounds, horizontalScale=$horizontalScale, " +
            "outlineColor=$outlineColor, outlineWidth=$outlineWidth, maxLines=$maxLines, " +
            "overflowEllipsis=$overflowEllipsis, htmlLabel=$htmlLabel, " +
            "wrappingWidth=$wrappingWidth, formula=$formula)"
}

enum class MermaidFormulaDisplayMode {
    Block,
}

// Mermaid.js 12.0.0:
// diagrams/common/common.ts -> renderKatexUnsanitized(displayMode = true).
data class MermaidFormulaSource(
    val source: String,
    val displayMode: MermaidFormulaDisplayMode = MermaidFormulaDisplayMode.Block,
    val accessibilityText: String = source,
)

data class MermaidFormulaMeasureRequest(
    val source: String,
    val displayMode: MermaidFormulaDisplayMode,
    val maxWidth: Float,
    val fontSize: Float,
    val rasterScale: Float,
    val accessibilityText: String,
)

data class MermaidFormulaMetrics(
    val width: Float,
    val height: Float,
    val baseline: Float,
)

class SceneTextSpan(
    val start: Int,
    val end: Int,
    val weight: SceneTextWeight? = null,
    val italic: Boolean = false,
    val underline: Boolean = false,
    val lineThrough: Boolean = false,
    val color: SceneColor? = null,
    val background: SceneColor? = null,
    val fontFamily: SceneTextFontFamily? = null,
    val baselineShift: SceneTextBaselineShift? = null,
    val fontSizeScale: Float = 1f,
) {
    var formula: MermaidFormulaSource? = null
        private set

    fun withFormula(formula: MermaidFormulaSource?): SceneTextSpan =
        copy().also { result -> result.formula = formula }

    operator fun component1(): Int = start
    operator fun component2(): Int = end
    operator fun component3(): SceneTextWeight? = weight
    operator fun component4(): Boolean = italic
    operator fun component5(): Boolean = underline
    operator fun component6(): Boolean = lineThrough
    operator fun component7(): SceneColor? = color
    operator fun component8(): SceneColor? = background
    operator fun component9(): SceneTextFontFamily? = fontFamily
    operator fun component10(): SceneTextBaselineShift? = baselineShift
    operator fun component11(): Float = fontSizeScale

    fun copy(
        start: Int = this.start,
        end: Int = this.end,
        weight: SceneTextWeight? = this.weight,
        italic: Boolean = this.italic,
        underline: Boolean = this.underline,
        lineThrough: Boolean = this.lineThrough,
        color: SceneColor? = this.color,
        background: SceneColor? = this.background,
        fontFamily: SceneTextFontFamily? = this.fontFamily,
        baselineShift: SceneTextBaselineShift? = this.baselineShift,
        fontSizeScale: Float = this.fontSizeScale,
    ): SceneTextSpan = SceneTextSpan(
        start = start,
        end = end,
        weight = weight,
        italic = italic,
        underline = underline,
        lineThrough = lineThrough,
        color = color,
        background = background,
        fontFamily = fontFamily,
        baselineShift = baselineShift,
        fontSizeScale = fontSizeScale,
    ).also { result -> result.formula = formula }

    override fun equals(other: Any?): Boolean =
        this === other ||
            (
                other is SceneTextSpan &&
                    start == other.start &&
                    end == other.end &&
                    weight == other.weight &&
                    italic == other.italic &&
                    underline == other.underline &&
                    lineThrough == other.lineThrough &&
                    color == other.color &&
                    background == other.background &&
                    fontFamily == other.fontFamily &&
                    baselineShift == other.baselineShift &&
                    fontSizeScale.compareTo(other.fontSizeScale) == 0 &&
                    formula == other.formula
                )

    override fun hashCode(): Int = listOf(
        start,
        end,
        weight,
        italic,
        underline,
        lineThrough,
        color,
        background,
        fontFamily,
        baselineShift,
        fontSizeScale,
        formula,
    ).hashCode()

    override fun toString(): String =
        "SceneTextSpan(start=$start, end=$end, weight=$weight, italic=$italic, " +
            "underline=$underline, lineThrough=$lineThrough, color=$color, " +
            "background=$background, fontFamily=$fontFamily, baselineShift=$baselineShift, " +
            "fontSizeScale=$fontSizeScale, formula=$formula)"
}

enum class SceneTextWeight {
    Normal,
    Medium,
    Bold,
}

enum class SceneTextFontFamily {
    Monospace,
}

enum class SceneTextBaselineShift {
    Subscript,
    Superscript,
}

enum class SceneTextAlignment {
    Start,
    Center,
    End,
}

sealed interface ScenePathCommand {
    data class MoveTo(
        val point: ScenePoint,
    ) : ScenePathCommand

    data class LineTo(
        val point: ScenePoint,
    ) : ScenePathCommand

    data class QuadraticTo(
        val control: ScenePoint,
        val end: ScenePoint,
    ) : ScenePathCommand

    data class CubicTo(
        val control1: ScenePoint,
        val control2: ScenePoint,
        val end: ScenePoint,
    ) : ScenePathCommand

    data class ArcTo(
        val radius: Float,
        val end: ScenePoint,
        val clockwise: Boolean,
    ) : ScenePathCommand
}

data class ScenePath(
    val id: String,
    val points: List<ScenePoint>,
    val commands: List<ScenePathCommand>,
    val color: SceneColor,
    val strokeWidth: Float,
    val strokePattern: SceneStrokePattern = SceneStrokePattern.Solid,
    val arrowStart: SceneArrowHead = SceneArrowHead.None,
    val arrowEnd: SceneArrowHead = SceneArrowHead.None,
    val arrowColor: SceneColor? = null,
    val curve: String = "rounded",
    val look: String,
    val animated: Boolean,
    val animationDurationMillis: Int? = null,
    override val zIndex: Int = 5,
    val dashIntervals: List<Float> = emptyList(),
    val markerBackground: SceneColor? = null,
    val fillColor: SceneColor? = null,
    val closed: Boolean = false,
    val strokeGradient: SceneLinearGradient? = null,
    val opacity: Float = 1f,
    val blendMode: SceneBlendMode = SceneBlendMode.SourceOver,
) : SceneElement

data class SceneNodeInteraction(
    val nodeId: String,
    val bounds: SceneRect,
    val link: String? = null,
    val linkTarget: String? = null,
    val tooltip: String? = null,
    val callbackName: String? = null,
    val callbackArgs: String? = null,
)

enum class MermaidSceneViewportSizing {
    Fit,
    ResponsiveMaxWidth,
    Intrinsic,
}

class MermaidScene(
    val width: Float,
    val height: Float,
    val background: SceneColor,
    val elements: List<SceneElement>,
    val title: String? = null,
    val accessibilityTitle: String? = null,
    val accessibilityDescription: String? = null,
    val interactions: List<SceneNodeInteraction> = emptyList(),
    val viewportPadding: Float = 0f,
    val viewportSizing: MermaidSceneViewportSizing = MermaidSceneViewportSizing.Fit,
    val diagnostics: List<MermaidDiagnostic> = emptyList(),
) {
    var formulaRequests: List<MermaidFormulaMeasureRequest> = emptyList()
        private set

    fun withFormulaRequests(
        formulaRequests: List<MermaidFormulaMeasureRequest>,
    ): MermaidScene = copy().also { result ->
        result.formulaRequests = formulaRequests
    }

    operator fun component1(): Float = width
    operator fun component2(): Float = height
    operator fun component3(): SceneColor = background
    operator fun component4(): List<SceneElement> = elements
    operator fun component5(): String? = title
    operator fun component6(): String? = accessibilityTitle
    operator fun component7(): String? = accessibilityDescription
    operator fun component8(): List<SceneNodeInteraction> = interactions
    operator fun component9(): Float = viewportPadding
    operator fun component10(): MermaidSceneViewportSizing = viewportSizing
    operator fun component11(): List<MermaidDiagnostic> = diagnostics

    fun copy(
        width: Float = this.width,
        height: Float = this.height,
        background: SceneColor = this.background,
        elements: List<SceneElement> = this.elements,
        title: String? = this.title,
        accessibilityTitle: String? = this.accessibilityTitle,
        accessibilityDescription: String? = this.accessibilityDescription,
        interactions: List<SceneNodeInteraction> = this.interactions,
        viewportPadding: Float = this.viewportPadding,
        viewportSizing: MermaidSceneViewportSizing = this.viewportSizing,
        diagnostics: List<MermaidDiagnostic> = this.diagnostics,
    ): MermaidScene = MermaidScene(
        width = width,
        height = height,
        background = background,
        elements = elements,
        title = title,
        accessibilityTitle = accessibilityTitle,
        accessibilityDescription = accessibilityDescription,
        interactions = interactions,
        viewportPadding = viewportPadding,
        viewportSizing = viewportSizing,
        diagnostics = diagnostics,
    ).also { result -> result.formulaRequests = formulaRequests }

    override fun equals(other: Any?): Boolean =
        this === other ||
            (
                other is MermaidScene &&
                    width.compareTo(other.width) == 0 &&
                    height.compareTo(other.height) == 0 &&
                    background == other.background &&
                    elements == other.elements &&
                    title == other.title &&
                    accessibilityTitle == other.accessibilityTitle &&
                    accessibilityDescription == other.accessibilityDescription &&
                    interactions == other.interactions &&
                    viewportPadding.compareTo(other.viewportPadding) == 0 &&
                    viewportSizing == other.viewportSizing &&
                    diagnostics == other.diagnostics &&
                    formulaRequests == other.formulaRequests
                )

    override fun hashCode(): Int = listOf(
        width,
        height,
        background,
        elements,
        title,
        accessibilityTitle,
        accessibilityDescription,
        interactions,
        viewportPadding,
        viewportSizing,
        diagnostics,
        formulaRequests,
    ).hashCode()

    override fun toString(): String =
        "MermaidScene(width=$width, height=$height, background=$background, " +
            "elements=$elements, title=$title, accessibilityTitle=$accessibilityTitle, " +
            "accessibilityDescription=$accessibilityDescription, interactions=$interactions, " +
            "viewportPadding=$viewportPadding, viewportSizing=$viewportSizing, " +
            "diagnostics=$diagnostics, formulaRequests=$formulaRequests)"
}

data class TextMetricsRequest(
    val text: String,
    val fontSize: Float,
    val maxWidth: Float,
    val lineHeight: Float = 1.2f,
    val fontFamily: String? = null,
    val weight: SceneTextWeight = SceneTextWeight.Medium,
    val spans: List<SceneTextSpan> = emptyList(),
    val horizontalScale: Float? = null,
    val htmlLabel: Boolean = false,
)

class TextMetrics(
    val width: Float,
    val height: Float,
    val lineCount: Int? = null,
) {
    var formula: MermaidFormulaMeasureRequest? = null
        private set

    fun withFormula(formula: MermaidFormulaMeasureRequest?): TextMetrics =
        copy().also { result -> result.formula = formula }

    operator fun component1(): Float = width
    operator fun component2(): Float = height
    operator fun component3(): Int? = lineCount

    fun copy(
        width: Float = this.width,
        height: Float = this.height,
        lineCount: Int? = this.lineCount,
    ): TextMetrics = TextMetrics(
        width = width,
        height = height,
        lineCount = lineCount,
    ).also { result -> result.formula = formula }

    override fun equals(other: Any?): Boolean =
        this === other ||
            (
                other is TextMetrics &&
                    width.compareTo(other.width) == 0 &&
                    height.compareTo(other.height) == 0 &&
                    lineCount == other.lineCount &&
                    formula == other.formula
                )

    override fun hashCode(): Int = listOf(
        width,
        height,
        lineCount,
        formula,
    ).hashCode()

    override fun toString(): String =
        "TextMetrics(width=$width, height=$height, lineCount=$lineCount, formula=$formula)"
}

fun interface TextMetricProvider {
    fun measure(request: TextMetricsRequest): TextMetrics
}

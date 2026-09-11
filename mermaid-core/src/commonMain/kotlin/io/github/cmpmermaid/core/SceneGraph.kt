package io.github.cmpmermaid.core

data class SceneColor(
    val argb: Long,
)

enum class SceneShapeKind {
    Rectangle,
    RoundedRectangle,
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
    Image,
}

enum class SceneStrokePattern {
    Solid,
    Dashed,
    Dotted,
}

enum class SceneArrowHead {
    None,
    Triangle,
    Circle,
    Cross,
}

sealed interface SceneElement {
    val zIndex: Int
}

data class SceneShape(
    val id: String,
    val bounds: SceneRect,
    val kind: SceneShapeKind,
    val fill: SceneColor,
    val stroke: SceneColor,
    val strokeWidth: Float = 1.5f,
    val cornerRadius: Float = 8f,
    override val zIndex: Int = 10,
) : SceneElement

data class SceneText(
    val text: String,
    val bounds: SceneRect,
    val color: SceneColor,
    val fontSize: Float,
    val weight: SceneTextWeight = SceneTextWeight.Medium,
    val horizontalAlignment: SceneTextAlignment = SceneTextAlignment.Center,
    override val zIndex: Int = 20,
) : SceneElement

enum class SceneTextWeight {
    Normal,
    Medium,
    Bold,
}

enum class SceneTextAlignment {
    Start,
    Center,
    End,
}

data class ScenePath(
    val id: String,
    val points: List<ScenePoint>,
    val color: SceneColor,
    val strokeWidth: Float,
    val strokePattern: SceneStrokePattern = SceneStrokePattern.Solid,
    val arrowStart: SceneArrowHead = SceneArrowHead.None,
    val arrowEnd: SceneArrowHead = SceneArrowHead.None,
    override val zIndex: Int = 5,
) : SceneElement

data class MermaidScene(
    val width: Float,
    val height: Float,
    val background: SceneColor,
    val elements: List<SceneElement>,
)

data class TextMetricsRequest(
    val text: String,
    val fontSize: Float,
    val maxWidth: Float,
    val weight: SceneTextWeight = SceneTextWeight.Medium,
)

data class TextMetrics(
    val width: Float,
    val height: Float,
)

fun interface TextMetricProvider {
    fun measure(request: TextMetricsRequest): TextMetrics
}

package io.github.cmpmermaid.core.classdiagram

import io.github.cmpmermaid.core.GMResult
import io.github.cmpmermaid.core.MermaidEngine
import io.github.cmpmermaid.core.MermaidError
import io.github.cmpmermaid.core.MermaidRenderContext
import io.github.cmpmermaid.core.MermaidRenderOptions
import io.github.cmpmermaid.core.MermaidScene
import io.github.cmpmermaid.core.MermaidSecurityLevel
import io.github.cmpmermaid.core.SceneArrowHead
import io.github.cmpmermaid.core.ScenePath
import io.github.cmpmermaid.core.SceneShape
import io.github.cmpmermaid.core.SceneStrokePattern
import io.github.cmpmermaid.core.SceneText
import io.github.cmpmermaid.core.SceneTextAlignment
import io.github.cmpmermaid.core.SceneTextWeight
import io.github.cmpmermaid.core.TextMetricProvider
import io.github.cmpmermaid.core.TextMetrics
import kotlin.math.ceil
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ClassLayoutTest {
    private val engine = MermaidEngine()
    private val context = MermaidRenderContext(
        textMetrics = TextMetricProvider { request ->
            val charactersPerLine = (request.maxWidth / 8f).toInt().coerceAtLeast(1)
            val lines = request.text
                .split('\n')
                .sumOf { line ->
                    ceil(line.length.toDouble() / charactersPerLine)
                        .toInt()
                        .coerceAtLeast(1)
                }
            TextMetrics(
                width = minOf(
                    request.maxWidth,
                    request.text.lineSequence().maxOfOrNull(String::length).orZero() * 8f,
                ),
                height = lines * request.fontSize * request.lineHeight,
            )
        },
        options = MermaidRenderOptions(look = "neo"),
    )

    @Test
    fun rendersClassCompartmentsAndMemberClassifiers() {
        val scene = render(
            """
            classDiagram
                class Service~Result~ {
                    <<interface>>
                    +String name$
                    +load(id) Result*
                }
            """.trimIndent(),
        )
        val shape = scene.elements.filterIsInstance<SceneShape>().single { it.id == "Service" }
        val texts = scene.elements.filterIsInstance<SceneText>()

        assertEquals(3, shape.geometry?.paths?.size)
        assertTrue(texts.any { it.text == "«interface»" })
        assertTrue(texts.any { it.text == "Service<Result>" })
        val member = texts.single { it.text == "+String name" }
        val method = texts.single { it.text == "+load(id) : Result" }
        assertEquals(SceneTextAlignment.Start, member.horizontalAlignment)
        assertTrue(member.spans.single().underline)
        assertTrue(method.spans.single().italic)
    }

    @Test
    fun rendersEveryClassRelationMarkerAndLinePattern() {
        val scene = render(
            """
            classDiagram
                A o-- B
                C *-- D
                E <|-- F
                G <.. H
                I ()-- J
            """.trimIndent(),
        )
        val paths = scene.elements.filterIsInstance<ScenePath>()
            .filterNot { it.id.startsWith("edgeNote") }

        assertTrue(paths.any { it.arrowStart == SceneArrowHead.ClassAggregation })
        assertTrue(paths.any { it.arrowStart == SceneArrowHead.ClassComposition })
        assertTrue(paths.any { it.arrowStart == SceneArrowHead.ClassExtension })
        assertTrue(paths.any { it.arrowStart == SceneArrowHead.ClassDependency })
        assertTrue(paths.any { it.arrowStart == SceneArrowHead.ClassLollipop })
        assertTrue(
            paths.single { it.arrowStart == SceneArrowHead.ClassDependency }.strokePattern ==
                SceneStrokePattern.Dashed,
        )
    }

    @Test
    fun rendersNotesRelationLabelsAndCardinalities() {
        val scene = render(
            """
            classDiagram
                note for Customer "Primary<br/>account"
                Customer "1" --> "0..*" Order : places
            """.trimIndent(),
        )
        val note = scene.elements.filterIsInstance<SceneShape>().single { it.id == "note0" }
        val labels = scene.elements.filterIsInstance<SceneText>()
        val noteEdge = scene.elements.filterIsInstance<ScenePath>()
            .single { it.id == "edgeNote0" }

        assertEquals(2, note.geometry?.paths?.size)
        assertEquals(SceneStrokePattern.Dashed, noteEdge.strokePattern)
        assertTrue(labels.any { it.text == "Primary\naccount" })
        assertTrue(labels.any { it.text == "places" })
        assertTrue(labels.any { it.text == "1" })
        assertTrue(labels.any { it.text == "0..*" })
        val relationLabel = labels.single { it.text == "places" }
        assertTrue(
            labels.filter { it.text == "1" || it.text == "0..*" }
                .none { cardinality -> cardinality.bounds.overlaps(relationLabel.bounds) },
            "Expected cardinalities outside relation label: " +
                labels.filter { it.text == "1" || it.text == "0..*" }
                    .joinToString { "${it.text}=${it.bounds}" } +
                ", places=${relationLabel.bounds}",
        )
        val labelBackground = scene.elements.filterIsInstance<SceneShape>()
            .single { it.id.endsWith("-label-background") }
        assertEquals(context.theme.nodeFill, labelBackground.fill)
    }

    @Test
    fun rendersClassMarkdownThroughTheTranslatedMermaidTextPath() {
        val scene = render(
            """
            classDiagram
                class Service["**Service**"] {
                    +load(_id_)
                }
                note for Service "**Primary**<br/>*account*"
                Service --> Repository : **loads**
            """.trimIndent(),
        )
        val texts = scene.elements.filterIsInstance<SceneText>()
        val title = texts.single { it.text == "Service" }
        val member = texts.single { it.text == "+load(id)" }
        val note = texts.single { it.text == "Primary\naccount" }
        val relation = texts.single { it.text == "loads" }

        assertTrue(title.spans.any { it.weight == SceneTextWeight.Bold })
        assertTrue(member.spans.any { it.italic })
        assertTrue(note.spans.any { it.weight == SceneTextWeight.Bold })
        assertTrue(note.spans.any { it.italic })
        assertTrue(relation.spans.any { it.weight == SceneTextWeight.Bold })
    }

    @Test
    fun rejectsClassKatexInsteadOfRenderingLiteralMarkup() {
        val result = engine.render(
            """
            classDiagram
                class Service
                note for Service "\(x^2\)"
            """.trimIndent(),
            context,
        )

        val error = assertIs<MermaidError.UnsupportedFeature>(
            assertIs<GMResult.Err<MermaidError>>(result).error,
        )
        assertEquals("KaTeX label", error.feature)
    }

    @Test
    fun rendersLollipopInterfaceLabelsOutsideTheClass() {
        val scene = render(
            """
            classDiagram
                class Service {
                    +execute()
                }
                Service --() Port
                Input ()-- Service
            """.trimIndent(),
        )
        val labels = scene.elements.filterIsInstance<SceneText>()
        val service = scene.elements.filterIsInstance<SceneShape>()
            .single { it.id == "Service" }
        val input = labels.single { it.text == "Input" }
        val port = labels.single { it.text == "Port" }

        assertTrue(input.bounds.bottom < service.bounds.top)
        assertTrue(port.bounds.top > service.bounds.bottom)
        assertTrue(
            scene.elements.filterIsInstance<SceneShape>()
                .none { it.id.startsWith("interface") },
        )
    }

    @Test
    fun rendersNestedNamespacesAndCompactConfiguration() {
        val hierarchical = render(
            """
            classDiagram
                namespace Company.Engineering.Backend {
                    class Developer
                }
            """.trimIndent(),
        )
        assertEquals(
            setOf(
                "namespace-Company",
                "namespace-Company.Engineering",
                "namespace-Company.Engineering.Backend",
            ),
            hierarchical.elements.filterIsInstance<SceneShape>()
                .map(SceneShape::id)
                .filter { it.startsWith("namespace-") }
                .toSet(),
        )

        val compact = render(
            """
            ---
            config:
              class:
                hierarchicalNamespaces: false
            ---
            classDiagram
                namespace Company.Engineering.Backend {
                    class Developer
                }
            """.trimIndent(),
        )
        assertEquals(
            setOf("namespace-Company.Engineering.Backend"),
            compact.elements.filterIsInstance<SceneShape>()
                .map(SceneShape::id)
                .filter { it.startsWith("namespace-") }
                .toSet(),
        )
    }

    @Test
    fun honorsHideEmptyMembersBox() {
        val defaultScene = render(
            """
            classDiagram
                class Duck
            """.trimIndent(),
        )
        val hiddenScene = render(
            """
            ---
            config:
              class:
                hideEmptyMembersBox: true
            ---
            classDiagram
                class Duck
            """.trimIndent(),
        )

        val defaultShape = defaultScene.elements.filterIsInstance<SceneShape>()
            .single { it.id == "Duck" }
        val hiddenShape = hiddenScene.elements.filterIsInstance<SceneShape>()
            .single { it.id == "Duck" }
        assertEquals(3, defaultShape.geometry?.paths?.size)
        assertEquals(1, hiddenShape.geometry?.paths?.size)
        assertTrue(hiddenShape.bounds.height < defaultShape.bounds.height)
    }

    @Test
    fun exposesLinksAndLooseCallbacksAsInteractions() {
        val linked = render(
            """
            classDiagram
                class Shape
                link Shape "https://example.com" "Open shape"
            """.trimIndent(),
        )
        val link = linked.interactions.single()
        assertEquals("https://example.com", link.link)
        assertEquals("Open shape", link.tooltip)

        val loose = render(
            """
            classDiagram
                class Shape
                callback Shape "openShape" "Open shape"
            """.trimIndent(),
            context.copy(
                options = context.options.copy(securityLevel = MermaidSecurityLevel.Loose),
            ),
        )
        assertEquals("openShape", loose.interactions.single().callbackName)
    }

    @Test
    fun inheritsTheTopLevelElkLayoutUnlessClassLayoutOverridesIt() {
        val source = """
            classDiagram
                Parent <|-- Child
                Parent --> Service : delegates
        """.trimIndent()
        val defaultScene = render(source)
        val explicitElk = render(
            source,
            context.copy(options = context.options.copy(classLayout = "elk")),
        )
        assertEquals(explicitElk, defaultScene)

        val unsupported = engine.render(
            source,
            context.copy(
                options = context.options.copy(
                    layout = "unsupported-layout",
                    classLayout = null,
                ),
            ),
        )
        assertIs<GMResult.Err<MermaidError>>(unsupported)
        assertIs<MermaidError.UnsupportedFeature>(unsupported.error)

        render(
            source,
            context.copy(
                options = context.options.copy(
                    layout = "unsupported-layout",
                    classLayout = "dagre",
                ),
            ),
        )
    }

    @Test
    fun rendersFrontmatterTitleAndAccessibilityMetadata() {
        val scene = render(
            """
            ---
            title: Service model
            ---
            classDiagram
                accTitle: Service class diagram
                accDescr: A gateway delegates work to a repository
                Gateway --> Repository : delegates
            """.trimIndent(),
        )

        assertEquals("Service model", scene.title)
        assertEquals("Service class diagram", scene.accessibilityTitle)
        assertEquals(
            "A gateway delegates work to a repository",
            scene.accessibilityDescription,
        )
        val title = scene.elements.filterIsInstance<SceneText>()
            .single { text -> text.text == "Service model" }
        assertEquals(18f, title.bounds.height)
    }

    private fun render(
        source: String,
        renderContext: MermaidRenderContext = context,
    ): MermaidScene {
        val result = engine.render(source, renderContext)
        assertIs<GMResult.Ok<MermaidScene>>(result)
        return result.value
    }

    private fun Int?.orZero(): Int = this ?: 0

    private fun io.github.cmpmermaid.core.SceneRect.overlaps(
        other: io.github.cmpmermaid.core.SceneRect,
    ): Boolean =
        left < other.right - 1f &&
            right > other.left + 1f &&
            top < other.bottom - 1f &&
            bottom > other.top + 1f
}

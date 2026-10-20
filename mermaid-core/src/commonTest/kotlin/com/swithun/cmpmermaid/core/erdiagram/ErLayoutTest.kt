package com.swithun.cmpmermaid.core.erdiagram

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidEngine
import com.swithun.cmpmermaid.core.MermaidFormulaRenderContext
import com.swithun.cmpmermaid.core.MermaidRenderContext
import com.swithun.cmpmermaid.core.MermaidRenderOptions
import com.swithun.cmpmermaid.core.MermaidScene
import com.swithun.cmpmermaid.core.MermaidTheme
import com.swithun.cmpmermaid.core.SceneArrowHead
import com.swithun.cmpmermaid.core.ScenePath
import com.swithun.cmpmermaid.core.SceneShape
import com.swithun.cmpmermaid.core.SceneStrokePattern
import com.swithun.cmpmermaid.core.SceneText
import com.swithun.cmpmermaid.core.SceneTextAlignment
import com.swithun.cmpmermaid.core.TextMetricProvider
import com.swithun.cmpmermaid.core.TextMetrics
import com.swithun.cmpmermaid.core.officialErDocumentationCases
import kotlin.math.ceil
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ErLayoutTest {
    private val engine = MermaidEngine()
    private val context = MermaidRenderContext(
        textMetrics = metrics(),
        theme = MermaidTheme.FlowchartDefault,
        options = MermaidRenderOptions(look = "neo"),
    )

    @Test
    fun rendersAttributeTableColumnsKeysCommentsAndAlias() {
        val scene = render(
            """
            erDiagram
                CUSTOMER["Customer Account"] {
                    int id PK
                    string email UK "Primary contact"
                    string? nickname
                }
            """.trimIndent(),
        )
        val entity = scene.elements.filterIsInstance<SceneShape>()
            .single { it.id.matches(Regex("""entity-CUSTOMER-\d+""")) }
        val grid = scene.elements.filterIsInstance<SceneShape>()
            .single { it.id == "${entity.id}-grid" }
        val labels = scene.elements.filterIsInstance<SceneText>()

        assertTrue(grid.geometry?.paths.orEmpty().size >= 5)
        assertTrue(labels.any { it.text == "Customer Account" })
        assertTrue(labels.any { it.text == "PK" })
        assertTrue(labels.any { it.text == "UK" })
        assertTrue(labels.any { it.text == "Primary contact" })
        assertTrue(labels.any {
            it.text == "nickname" && it.horizontalAlignment == SceneTextAlignment.Start
        })
    }

    @Test
    fun scalesNonHtmlEntityPaddingLikeUpstreamErBox() {
        val scene = render(
            """
            erDiagram
                ACCOUNT {
                    int id PK
                }
            """.trimIndent(),
        )
        val entity = scene.elements.filterIsInstance<SceneShape>()
            .single { it.id.matches(Regex("""entity-ACCOUNT-\d+""")) }
        val attributeTexts = scene.elements.filterIsInstance<SceneText>()
            .filter { it.text in setOf("int", "id", "PK") }
            .associateBy(SceneText::text)

        // erBox.ts multiplies PADDING and TEXT_PADDING by 1.25 for SVG labels.
        assertEquals(131f, entity.bounds.width)
        assertEquals(79.5f, entity.bounds.height)
        assertEquals(24f, attributeTexts.getValue("int").bounds.width)
        assertEquals(16f, attributeTexts.getValue("id").bounds.width)
        assertEquals(16f, attributeTexts.getValue("PK").bounds.width)
    }

    @Test
    fun keepsUnscaledEntityPaddingWhenRootHtmlLabelsIsExplicitlyTrue() {
        val scene = render(
            """
            ---
            config:
              htmlLabels: true
            ---
            erDiagram
                ACCOUNT {
                    int id PK
                }
            """.trimIndent(),
        )
        val entity = scene.elements.filterIsInstance<SceneShape>()
            .single { it.id.matches(Regex("""entity-ACCOUNT-\d+""")) }

        // erBox.ts reads config.htmlLabels directly for this branch.
        assertEquals(116f, entity.bounds.width)
        assertEquals(75f, entity.bounds.height)
    }

    @Test
    fun keepsLongEntityAliasUnwrappedLikeUpstreamErBox() {
        val alias = "End To End Production Compatibility Verification Evidence Label 255"
        var measuredAliasWidth: Float? = null
        val wrappingContext = context.copy(
            textMetrics = TextMetricProvider { request ->
                if (request.text == alias) {
                    measuredAliasWidth = request.maxWidth
                }
                TextMetrics(
                    width = minOf(request.maxWidth, request.text.length * 8f),
                    height = request.fontSize * request.lineHeight,
                )
            },
        )
        val result = engine.render(
            """
            erDiagram
                ParityEvidence255["$alias"]
            """.trimIndent(),
            wrappingContext,
        )
        val scene = assertIs<GMResult.Ok<MermaidScene>>(result).value
        val entity = scene.elements.filterIsInstance<SceneShape>()
            .single { shape -> shape.id.matches(Regex("""entity-ParityEvidence255-\d+""")) }
        val title = scene.elements.filterIsInstance<SceneText>()
            .single { text -> text.text == alias }

        assertEquals(100_000f, measuredAliasWidth)
        assertEquals(
            alias.length * 8f +
                wrappingContext.options.erDiagramPadding * 2f,
            entity.bounds.width,
        )
        assertTrue(!title.softWrap)
        assertEquals(title.fontSize * title.lineHeight, title.bounds.height)
    }

    @Test
    fun keepsUnbrokenEntityNameOnSingleLine() {
        val entityName = "BILLING_PROFILE_ARCHIVE"
        var measuredEntityWidth: Float? = null
        val wrappingContext = context.copy(
            textMetrics = TextMetricProvider { request ->
                if (request.text == entityName) {
                    measuredEntityWidth = request.maxWidth
                }
                TextMetrics(
                    width = minOf(request.maxWidth, request.text.length * 8f),
                    height = request.fontSize * request.lineHeight,
                )
            },
        )
        val result = engine.render(
            """
            erDiagram
                $entityName
            """.trimIndent(),
            wrappingContext,
        )
        val scene = assertIs<GMResult.Ok<MermaidScene>>(result).value
        val entity = scene.elements.filterIsInstance<SceneShape>()
            .single { shape -> shape.id.matches(Regex("""entity-$entityName-\d+""")) }
        val title = scene.elements.filterIsInstance<SceneText>()
            .single { text -> text.text == entityName }

        assertEquals(100_000f, measuredEntityWidth)
        assertTrue(entity.bounds.width > wrappingContext.options.wrappingWidth)
        assertTrue(!title.softWrap)
        assertEquals(title.fontSize * title.lineHeight, title.bounds.height)
    }

    @Test
    fun rendersEveryCardinalityAndIdentificationPattern() {
        val scene = render(
            """
            erDiagram
                A ||--o| B : "zero-or-one"
                C ||--|{ D : "one-or-more"
                E ||--o{ F : "zero-or-more"
                G ||--|| H : "only-one"
                I u--o{ J : parent
                K ||..o{ L : optional
            """.trimIndent(),
        )
        val paths = scene.elements.filterIsInstance<ScenePath>()
        val markerTypes = paths.flatMap { path -> listOf(path.arrowStart, path.arrowEnd) }.toSet()

        assertTrue(SceneArrowHead.ErOnlyOne in markerTypes)
        assertTrue(SceneArrowHead.ErZeroOrOne in markerTypes)
        assertTrue(SceneArrowHead.ErOneOrMore in markerTypes)
        assertTrue(SceneArrowHead.ErZeroOrMore in markerTypes)
        assertEquals(
            SceneArrowHead.None,
            paths.single { it.id.contains("entity-I-") }.arrowStart,
        )
        assertEquals(
            SceneStrokePattern.Dashed,
            paths.single { it.id.contains("entity-K-") }.strokePattern,
        )
        assertTrue(paths.filterNot { it.id.contains("entity-K-") }.all {
            it.strokePattern == SceneStrokePattern.Solid
        })
    }

    @Test
    fun usesMermaid12ErSpecificRelationshipLabelBackground() {
        val scene = render(
            """
            erDiagram
                ACCOUNT ||--o{ RECORD : owns
            """.trimIndent(),
        )
        val background = scene.elements.filterIsInstance<SceneShape>()
            .single { it.id.endsWith("-label-background") }

        assertEquals(
            MermaidTheme.FlowchartDefault.erEdgeLabelFill,
            background.fill,
        )
        assertEquals(0xFFFFFFFF, background.fill.argb)
    }

    @Test
    fun exposesProviderRequestForErRelationshipFormula() {
        val result = engine.render(
            """
            erDiagram
                ACCOUNT ||--o{ RECORD : "${'$'}${'$'}x^2${'$'}${'$'}"
            """.trimIndent(),
            context,
            MermaidFormulaRenderContext(),
        )
        val scene = assertIs<GMResult.Ok<MermaidScene>>(result).value
        val request = scene.formulaRequests.single()
        val label = scene.elements.filterIsInstance<SceneText>()
            .single { text -> text.text == "x^2" }

        assertEquals("x^2", request.source)
        assertEquals(request, label.formula)
        assertTrue(scene.diagnostics.isEmpty())
    }

    @Test
    fun usesMermaid12DefaultSubgraphMarginsAndErThemeFallbacks() {
        val scene = render(
            """
            erDiagram
                direction LR
                subgraph MASTER_DATA
                    direction TB
                    ACCOUNT {
                        uuid id PK
                    }
                    RECORD {
                        uuid id PK
                    }
                end
                subgraph ACTIVITY
                    direction TB
                    EVENT {
                        uuid id PK
                    }
                    TAG {
                        uuid id PK
                    }
                end
                ACCOUNT ||--o{ RECORD : owns
                RECORD ||--o{ EVENT : emits
                EVENT ||--o{ TAG : tagged
            """.trimIndent(),
        )
        val shapes = scene.elements.filterIsInstance<SceneShape>()
        val masterData = shapes.single { it.id == "er-subgraph-MASTER_DATA" }
        val account = shapes.single { it.id.matches(Regex("""entity-ACCOUNT-\d+""")) }
        val title = scene.elements.filterIsInstance<SceneText>()
            .single { it.text == "MASTER_DATA" }

        // Mermaid.js 12.0.0: subGraphTitleMargins.ts defaults both margins to
        // zero; clusters.js places the label at the cluster top.
        assertEquals(239.5f, masterData.bounds.height)
        assertEquals(80f, account.bounds.top - masterData.bounds.top)
        assertEquals(masterData.bounds.top, title.bounds.top)

        // Mermaid.js 12.0.0: diagrams/er/styles.ts -> getStyles.
        assertEquals(context.theme.erClusterFill, masterData.fill)
        assertEquals(context.theme.erClusterStroke, masterData.stroke)
        assertEquals(context.theme.erClusterText, title.color)
        assertEquals(0xFF28253D, masterData.stroke.argb)
        assertEquals(0xFF28253D, title.color.argb)
    }

    @Test
    fun rendersStylesNestedSubgraphsAndAccessibilityMetadata() {
        val scene = render(
            """
            ---
            title: Commerce model
            config:
              er:
                diagramPadding: 12
                entityPadding: 10
                nodeSpacing: 90
                rankSpacing: 60
            ---
            erDiagram
                accTitle: Commerce entities
                accDescr: Customer and order storage
                classDef warm fill:#ffedd5,stroke:#ea580c,color:#7c2d12
                subgraph domain [Customer Domain]
                    direction LR
                    customer["Customer"]:::warm {
                        int id PK
                    }
                    subgraph orders [Orders]
                        ORDER
                    end
                end
                customer ||--o{ ORDER : places
            """.trimIndent(),
        )
        val customer = scene.elements.filterIsInstance<SceneShape>()
            .single { it.id.matches(Regex("""entity-customer-\d+""")) }

        assertEquals(0xFFFFEDD5, customer.fill.argb)
        assertEquals(0xFFEA580C, customer.stroke.argb)
        assertEquals(2, scene.elements.filterIsInstance<SceneShape>().count {
            it.id.startsWith("er-subgraph-")
        })
        val subgraphs = scene.elements.filterIsInstance<SceneShape>()
            .filter { it.id.startsWith("er-subgraph-") }
            .associateBy(SceneShape::id)
        assertTrue(
            subgraphs.getValue("er-subgraph-orders").zIndex >
                subgraphs.getValue("er-subgraph-domain").zIndex,
        )
        assertEquals("Commerce model", scene.title)
        assertEquals("Commerce entities", scene.accessibilityTitle)
        assertEquals("Customer and order storage", scene.accessibilityDescription)
    }

    @Test
    fun rendersEveryOfficialErDocumentationExample() {
        assertEquals(24, officialErDocumentationCases.size)
        val failures = officialErDocumentationCases.mapNotNull { case ->
            when (val result = engine.render(case.source, context)) {
                is GMResult.Ok -> null
                is GMResult.Err -> "${case.id}: ${result.error.message}"
            }
        }
        assertTrue(
            failures.isEmpty(),
            failures.joinToString(
                prefix = "Failed Mermaid 12.0.0 ER render cases:\n",
                separator = "\n",
            ),
        )
    }

    private fun render(source: String): MermaidScene {
        val result = engine.render(source, context)
        return assertIs<GMResult.Ok<MermaidScene>>(
            result,
            "Expected ER render success:\n$source\n$result",
        ).value
    }

    private fun metrics(): TextMetricProvider = TextMetricProvider { request ->
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
    }

    private fun Int?.orZero(): Int = this ?: 0
}

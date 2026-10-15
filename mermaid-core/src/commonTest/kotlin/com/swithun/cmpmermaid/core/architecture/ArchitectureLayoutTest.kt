package com.swithun.cmpmermaid.core.architecture

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidEngine
import com.swithun.cmpmermaid.core.MermaidError
import com.swithun.cmpmermaid.core.MermaidRenderContext
import com.swithun.cmpmermaid.core.MermaidScene
import com.swithun.cmpmermaid.core.MermaidSceneViewportSizing
import com.swithun.cmpmermaid.core.SceneArrowHead
import com.swithun.cmpmermaid.core.SceneAsset
import com.swithun.cmpmermaid.core.ScenePath
import com.swithun.cmpmermaid.core.ScenePoint
import com.swithun.cmpmermaid.core.SceneShape
import com.swithun.cmpmermaid.core.SceneStrokePattern
import com.swithun.cmpmermaid.core.SceneText
import com.swithun.cmpmermaid.core.SceneTextWeight
import com.swithun.cmpmermaid.core.TextMetricProvider
import com.swithun.cmpmermaid.core.TextMetrics
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ArchitectureLayoutTest {
    private val engine = MermaidEngine()
    private val context = MermaidRenderContext(
        textMetrics = TextMetricProvider { request ->
            TextMetrics(
                width = minOf(request.maxWidth, request.text.length * request.fontSize * 0.55f),
                height = request.fontSize * request.lineHeight,
            )
        },
    )

    @Test
    fun rendersDirectionalEdgesIconsGroupsAndLabels() {
        val scene = render(
            """
                architecture-beta
                  group api(cloud)[API]
                  group data(database)[Data]
                  service app(server)[Application] in api
                  service db(database)[Database] in data
                  app{group}:R --> L:db{group}
            """.trimIndent(),
        )
        val shapes = scene.elements.filterIsInstance<SceneShape>()
        val paths = scene.elements.filterIsInstance<ScenePath>()
        val app = shapes.single { it.id == "architecture-service-icon-app-background" }
        val db = shapes.single { it.id == "architecture-service-icon-db-background" }
        val groups = shapes.filter {
            it.id in setOf("architecture-group-api", "architecture-group-data")
        }
        val edge = paths.single { it.id.startsWith("architecture-edge-") }

        assertTrue(app.bounds.center.x < db.bounds.center.x)
        assertEquals(2, groups.size)
        assertTrue(groups.all { it.strokePattern == SceneStrokePattern.Dashed })
        assertEquals(SceneArrowHead.None, edge.arrowEnd)
        assertTrue(
            shapes.single { it.id == "architecture-edge-0-arrow-end" }
                .geometry != null,
        )
        assertTrue(
            scene.elements.filterIsInstance<SceneText>()
                .map(SceneText::text)
                .containsAll(listOf("API", "Data", "Application", "Database")),
        )
        assertTrue(
            scene.elements.filterIsInstance<SceneText>()
                .filter { text ->
                    text.text in setOf("API", "Data", "Application", "Database")
                }
                .all { text -> text.weight == SceneTextWeight.Normal },
        )
    }

    @Test
    fun usesCytoscapePositionAsTheServiceSvgOrigin() {
        val scene = render(
            """
                architecture-beta
                  group platform(cloud)[Platform]
                  service app(server)[A] in platform
            """.trimIndent(),
        )
        val group = scene.shape("architecture-group-platform").bounds
        val service = scene.serviceBounds("app")
        val groupLabel = scene.elements.filterIsInstance<SceneText>()
            .single { it.text == "Platform" }

        assertEquals(165f, group.width, 0.01f)
        assertEquals(199.5f, group.height, 0.01f)
        assertEquals(42.5f, service.left - group.left, 0.01f)
        assertEquals(42.5f, service.top - group.top, 0.01f)
        assertEquals(30f, groupLabel.bounds.left - group.left, 0.01f)
        assertEquals(7f, groupLabel.bounds.top - group.top, 0.01f)
        assertEquals(
            SceneTextWeight.Normal,
            groupLabel.weight,
        )
        assertEquals(
            SceneTextWeight.Normal,
            scene.elements.filterIsInstance<SceneText>().single { it.text == "A" }.weight,
        )
    }

    @Test
    fun paintsEdgesThenServicesThenGroupsLikeUpstreamLayers() {
        val scene = render(
            """
                architecture-beta
                  group runtime(server)[Runtime]
                  service source(server)[Source] in runtime
                  service target(internet)[Target] in runtime
                  source:B -[observes]-> T:target
            """.trimIndent(),
        )
        val edgeLabelIndex = scene.elements.indexOfFirst { element ->
            element is SceneText && element.text == "observes"
        }
        val edgeLabel = assertIs<SceneText>(scene.elements[edgeLabelIndex])
        val edgeLabelPivot = assertNotNull(edgeLabel.rotationPivot)
        val edgePathIndex = scene.elements.indexOfFirst { element ->
            element is ScenePath && element.id.startsWith("architecture-edge-")
        }
        val edgePath = assertIs<ScenePath>(scene.elements[edgePathIndex])
        val edgeArrowIndex = scene.elements.indexOfFirst { element ->
            element is SceneShape && element.id == "architecture-edge-0-arrow-end"
        }
        val sourceTitleIndex = scene.elements.indexOfFirst { element ->
            element is SceneText && element.text == "Source"
        }
        val serviceBackgroundIndex = scene.elements.indexOfFirst { element ->
            element is SceneShape &&
                element.id.startsWith("architecture-service-icon-") &&
                element.id.endsWith("-background")
        }
        val sourceIconLastIndex = scene.elements.indexOfLast { element ->
            when (element) {
                is SceneAsset -> element.id.startsWith("architecture-service-icon-source")
                is ScenePath -> element.id.startsWith("architecture-service-icon-source")
                is SceneShape -> element.id.startsWith("architecture-service-icon-source")
                is SceneText -> false
            }
        }
        val targetTitleIndex = scene.elements.indexOfFirst { element ->
            element is SceneText && element.text == "Target"
        }
        val targetBackgroundIndex = scene.elements.indexOfFirst { element ->
            element is SceneShape &&
                element.id == "architecture-service-icon-target-background"
        }
        val groupIndex = scene.elements.indexOfFirst { element ->
            element is SceneShape && element.id == "architecture-group-runtime"
        }
        val sourceBounds = scene.serviceBounds("source")
        val targetBounds = scene.serviceBounds("target")

        assertTrue(edgePathIndex < edgeArrowIndex)
        assertTrue(edgeArrowIndex < edgeLabelIndex)
        assertEquals(sourceBounds.center.x, edgePath.points.first().x, 0.01f)
        assertEquals(sourceBounds.bottom, edgePath.points.first().y, 0.01f)
        assertEquals(targetBounds.center.x, edgePath.points.last().x, 0.01f)
        assertEquals(targetBounds.top, edgePath.points.last().y, 0.01f)
        assertEquals(edgeLabelPivot.y + 16f / 3f, edgeLabel.bounds.top, 0.01f)
        assertTrue(edgeLabelIndex < sourceTitleIndex)
        assertTrue(sourceTitleIndex < serviceBackgroundIndex)
        assertTrue(sourceIconLastIndex < targetTitleIndex)
        assertTrue(targetTitleIndex < targetBackgroundIndex)
        assertTrue(serviceBackgroundIndex > edgeLabelIndex)
        assertTrue(groupIndex > serviceBackgroundIndex)
        assertTrue(
            scene.elements.filterIsInstance<SceneText>()
                .filter { text -> text.text in setOf("Runtime", "Source", "Target", "observes") }
                .all { text -> text.weight == SceneTextWeight.Normal },
        )
    }

    @Test
    fun honorsExplicitRowAndColumnAlignment() {
        val row = render(
            """
                architecture-beta
                  service a(server)[A]
                  service b(server)[B]
                  service c(server)[C]
                  align row a b c
            """.trimIndent(),
        )
        val rowCenters = listOf("a", "b", "c").map { id ->
            row.serviceBounds(id).center
        }
        assertEquals(1, rowCenters.map { it.y }.distinct().size)
        assertTrue(rowCenters.zipWithNext().all { (first, second) -> first.x < second.x })

        val column = render(
            """
                architecture-beta
                  service a(server)[A]
                  service b(server)[B]
                  service c(server)[C]
                  align column a b c
            """.trimIndent(),
        )
        val columnCenters = listOf("a", "b", "c").map { id ->
            column.serviceBounds(id).center
        }
        assertEquals(1, columnCenters.map { it.x }.distinct().size)
        assertTrue(columnCenters.zipWithNext().all { (first, second) -> first.y < second.y })
    }

    @Test
    fun keepsCombinedAlignmentGroupsIndependentDuringOverlapResolution() {
        val scene = render(
            """
                architecture-beta
                  service input_a(server)[Input A]
                  service input_b(server)[Input B]
                  service process_a(server)[Process A]
                  service process_b(server)[Process B]
                  service output_a(disk)[Output A]
                  service output_b(disk)[Output B]
                  input_a:B --> T:process_a
                  input_b:B --> T:process_b
                  process_a:B --> T:output_a
                  process_b:B --> T:output_b
                  align row input_a input_b
                  align row process_a process_b
                  align row output_a output_b
                  align column input_a process_a output_a
                  align column input_b process_b output_b
            """.trimIndent(),
        )
        val leftColumn = listOf("input_a", "process_a", "output_a")
            .map { id -> scene.serviceBounds(id).center }
        val rightColumn = listOf("input_b", "process_b", "output_b")
            .map { id -> scene.serviceBounds(id).center }

        assertEquals(1, leftColumn.map(ScenePoint::x).distinct().size)
        assertEquals(1, rightColumn.map(ScenePoint::x).distinct().size)
        assertTrue(rightColumn.first().x - leftColumn.first().x in 120f..220f)
        assertTrue(
            leftColumn.zipWithNext().all { (first, second) ->
                second.y - first.y in 120f..220f
            },
        )
    }

    @Test
    fun nestedGroupBoundsContainTheirDescendants() {
        val scene = render(
            """
                architecture-beta
                  group outer(cloud)[Outer]
                  group inner(cloud)[Inner] in outer
                  service app(server)[Application] in inner
            """.trimIndent(),
        )
        val outer = scene.shape("architecture-group-outer").bounds
        val inner = scene.shape("architecture-group-inner").bounds
        val app = scene.serviceBounds("app")

        assertTrue(outer.contains(inner))
        assertTrue(inner.contains(app))
    }

    @Test
    fun separatesDisconnectedSiblingGroups() {
        val scene = render(
            """
                architecture-beta
                  group primary(cloud)[Primary]
                  service first(server)[First] in primary
                  group isolated(cloud)[Isolated]
                  service second(server)[Second] in isolated
            """.trimIndent(),
        )
        val primary = scene.shape("architecture-group-primary").bounds
        val isolated = scene.shape("architecture-group-isolated").bounds

        assertTrue(
            isolated.right <= primary.left ||
                primary.right <= isolated.left ||
                isolated.bottom <= primary.top ||
                primary.bottom <= isolated.top,
        )
    }

    @Test
    fun appliesArchitectureConfigurationAndExternalIcons() {
        val scene = render(
            """
                ---
                config:
                  architecture:
                    useMaxWidth: false
                    padding: 24
                    iconSize: 48
                    fontSize: 12
                    randomize: false
                    nodeSeparation: 60
                    idealEdgeLengthMultiplier: 2
                    edgeElasticity: 0.6
                    numIter: 100
                    seed: 42
                ---
                architecture-beta
                  service app(logos:kotlin)[Application]
            """.trimIndent(),
        )
        val asset = scene.elements.filterIsInstance<SceneAsset>().single()

        assertEquals("logos:kotlin", asset.source)
        assertEquals(48f, asset.bounds.width, 0.01f)
        assertEquals(MermaidSceneViewportSizing.Intrinsic, scene.viewportSizing)
    }

    @Test
    fun rejectsRandomizedArchitectureUntilSpectralPathIsTranslated() {
        val result = engine.render(
            source = """
                ---
                config:
                  architecture:
                    randomize: true
                ---
                architecture-beta
                  service app(server)[Application]
            """.trimIndent(),
            context = context,
        )

        val error = assertIs<GMResult.Err<MermaidError>>(result).error
        assertEquals(
            MermaidError.UnsupportedFeature(
                feature = "Architecture fCoSE spectral initialization",
            ),
            error,
        )
    }

    @Test
    fun clampsIconTextToTheUpstreamComputedLineCountWithEllipsis() {
        val label = "Deterministic Rendering Verification Evidence Label"
        val scene = render(
            """
                architecture-beta
                  service app "$label"[Application]
            """.trimIndent(),
        )
        val iconText = scene.elements.filterIsInstance<SceneText>()
            .single { text -> text.text == label }

        assertEquals(4, iconText.maxLines)
        assertTrue(iconText.overflowEllipsis)
        assertTrue(iconText.clipToBounds)
        assertEquals(76f, iconText.bounds.width, 0.01f)
        assertEquals(76f, iconText.bounds.height, 0.01f)
    }

    @Test
    fun preservesAccessibilityMetadataAndSeededDeterminism() {
        val source = """
            architecture-beta
              accTitle: Accessible system
              accDescr: A deterministic architecture.
              service a(server)[A]
              service b(database)[B]
              a:R -- L:b
        """.trimIndent()
        val first = render(source)
        val second = render(source)

        assertEquals(first, second)
        assertEquals("Accessible system", first.accessibilityTitle)
        assertEquals("A deterministic architecture.", first.accessibilityDescription)
    }

    @Test
    fun spreadsSamePortStarAcrossBothAxesLikeIncrementalFcose() {
        val scene = render(
            """
                ---
                config:
                  architecture:
                    seed: 31
                ---
                architecture-beta
                  group cluster(cloud)[Cluster]
                  service center(database)[Center] in cluster
                  service first(database)[First] in cluster
                  service second(database)[Second] in cluster
                  service third(database)[Third] in cluster
                  service fourth(database)[Fourth] in cluster
                  center:R --> L:first
                  center:R --> L:second
                  center:R --> L:third
                  center:R --> L:fourth
            """.trimIndent(),
        )
        val centers = listOf("center", "first", "second", "third", "fourth")
            .map { id -> scene.serviceBounds(id).center }
        val xSpan = centers.maxOf(ScenePoint::x) - centers.minOf(ScenePoint::x)
        val ySpan = centers.maxOf(ScenePoint::y) - centers.minOf(ScenePoint::y)

        assertTrue(xSpan > 72f)
        assertTrue(ySpan > 72f)
    }

    @Test
    fun spreadsTwoLeafSamePortStarAcrossBothAxesLikeIncrementalFcose() {
        val scene = render(
            """
                architecture-beta
                  group cluster(cloud)[Cluster]
                  service center(database)[Center] in cluster
                  service first(database)[First] in cluster
                  service second(database)[Second] in cluster
                  center:R --> L:first
                  center:R --> L:second
            """.trimIndent(),
        )
        val centers = listOf("center", "first", "second")
            .map { id -> scene.serviceBounds(id).center }
        val xSpan = centers.maxOf(ScenePoint::x) - centers.minOf(ScenePoint::x)
        val ySpan = centers.maxOf(ScenePoint::y) - centers.minOf(ScenePoint::y)

        assertTrue(xSpan > 72f)
        assertTrue(ySpan > 72f)
    }

    @Test
    fun placesTwoLeafStarDiagonallyFromTallRootComponent() {
        val scene = render(
            """
                architecture-beta
                  service gateway(internet)[Gateway]
                  service app(server)[Application]
                  service database(database)[Database]
                  gateway:R --> L:app
                  app:B --> T:database
                  group cluster(cloud)[Cluster]
                  service center(database)[Center] in cluster
                  service first(database)[First] in cluster
                  service second(database)[Second] in cluster
                  center:R --> L:first
                  center:R --> L:second
            """.trimIndent(),
        )
        val rootCenters = listOf("gateway", "app", "database")
            .map { id -> scene.serviceBounds(id).center }
        val cluster = scene.shape("architecture-group-cluster").bounds
        val rootCenterX = (
            rootCenters.minOf(ScenePoint::x) +
                rootCenters.maxOf(ScenePoint::x)
            ) / 2f
        val rootCenterY = (
            rootCenters.minOf(ScenePoint::y) +
                rootCenters.maxOf(ScenePoint::y)
            ) / 2f

        assertTrue(cluster.center.x < rootCenterX)
        assertTrue(cluster.center.y > rootCenterY)
    }

    @Test
    fun offsetsDisconnectedSingleCompoundAcrossBothAxes() {
        val scene = render(
            """
                architecture-beta
                  service client(internet)[Client]
                  service api(server)[API]
                  service cache(disk)[Cache]
                  service database(database)[Database]
                  client:R --> L:api
                  api:B --> T:cache
                  cache:R --> L:database
                  group cluster(cloud)[Cluster]
                  service isolated(server)[Isolated] in cluster
            """.trimIndent(),
        )
        val rootCenters = listOf("client", "api", "cache", "database")
            .map { id -> scene.serviceBounds(id).center }
        val cluster = scene.shape("architecture-group-cluster").bounds
        val rootCenterY = (
            rootCenters.minOf(ScenePoint::y) +
                rootCenters.maxOf(ScenePoint::y)
            ) / 2f

        assertTrue(cluster.center.x > rootCenters.maxOf(ScenePoint::x))
        assertTrue(cluster.center.y < rootCenterY - 72f)
    }

    @Test
    fun placesDisconnectedHorizontalChainBesideUnconstrainedRootComponent() {
        val scene = render(
            """
                architecture-beta
                  service client(internet)[Client]
                  service api(server)[API]
                  service cache(disk)[Cache]
                  service database(database)[Database]
                  client:R --> L:api
                  api:B --> T:cache
                  cache:R --> L:database
                  group cluster(cloud)[Cluster]
                  service first(server)[First] in cluster
                  service second(server)[Second] in cluster
                  service third(server)[Third] in cluster
                  first:R --> L:second
                  second:R --> L:third
            """.trimIndent(),
        )
        val baseBounds = listOf("client", "api", "cache", "database")
            .map { id -> scene.serviceBounds(id) }
            .reduce { first, second ->
                com.swithun.cmpmermaid.core.SceneRect(
                    left = minOf(first.left, second.left),
                    top = minOf(first.top, second.top),
                    right = maxOf(first.right, second.right),
                    bottom = maxOf(first.bottom, second.bottom),
                )
            }
        val cluster = scene.shape("architecture-group-cluster").bounds

        assertTrue(cluster.left < baseBounds.right)
        assertTrue(cluster.right > baseBounds.right + 300f)
        assertTrue(cluster.top < baseBounds.bottom)
        assertTrue(cluster.bottom > baseBounds.top)
    }

    @Test
    fun separatesDisconnectedSiblingCompoundsAcrossBothAxes() {
        val scene = render(
            """
                architecture-beta
                  group runtime(cloud)[Runtime]
                  service gateway(internet)[Gateway] in runtime
                  junction split in runtime
                  service api(server)[API] in runtime
                  service jobs(server)[Jobs] in runtime
                  gateway:B --> T:split
                  split:R --> L:api
                  split:B --> T:jobs
                  group cluster(cloud)[Cluster]
                  service first(server)[First] in cluster
                  service second(server)[Second] in cluster
                  first:R --> L:second
            """.trimIndent(),
        )
        val runtime = scene.shape("architecture-group-runtime").bounds
        val cluster = scene.shape("architecture-group-cluster").bounds

        assertTrue(cluster.right < runtime.left)
        assertTrue(cluster.bottom < runtime.top)
    }

    @Test
    fun placesDisconnectedBranchingCompoundBelowRigidRootGrid() {
        val scene = render(
            """
                architecture-beta
                  service input_a(server)[Input A]
                  service input_b(server)[Input B]
                  service process_a(server)[Process A]
                  service process_b(server)[Process B]
                  service output_a(disk)[Output A]
                  service output_b(disk)[Output B]
                  input_a:B --> T:process_a
                  input_b:B --> T:process_b
                  process_a:B --> T:output_a
                  process_b:B --> T:output_b
                  align row input_a input_b
                  align row process_a process_b
                  align row output_a output_b
                  align column input_a process_a output_a
                  align column input_b process_b output_b
                  group cluster(cloud)[Cluster]
                  service center(database)[Center] in cluster
                  service first(database)[First] in cluster
                  service second(database)[Second] in cluster
                  center:R --> L:first
                  center:R --> L:second
            """.trimIndent(),
        )
        val rootBottom = listOf("output_a", "output_b")
            .maxOf { id -> scene.serviceBounds(id).bottom }
        val cluster = scene.shape("architecture-group-cluster").bounds

        assertTrue(cluster.top > rootBottom)
    }

    @Test
    fun keepsDisconnectedBranchingCompoundBesideTwoRowRootGrid() {
        val scene = render(
            """
                architecture-beta
                  service source_a(server)[Source A]
                  service source_b(server)[Source B]
                  service database_a(database)[Database A]
                  service database_b(database)[Database B]
                  source_a:B --> T:database_a
                  source_b:B --> T:database_b
                  align row source_a source_b
                  align row database_a database_b
                  align column source_a database_a
                  align column source_b database_b
                  group cluster(cloud)[Cluster]
                  service center(database)[Center] in cluster
                  service first(database)[First] in cluster
                  service second(database)[Second] in cluster
                  service third(database)[Third] in cluster
                  center:R --> L:first
                  center:R --> L:second
                  center:R --> L:third
            """.trimIndent(),
        )
        val rootTop = listOf("source_a", "source_b")
            .minOf { id -> scene.serviceBounds(id).top }
        val rootBottom = listOf("database_a", "database_b")
            .maxOf { id -> scene.serviceBounds(id).bottom }
        val cluster = scene.shape("architecture-group-cluster").bounds

        assertTrue(cluster.top < rootBottom)
        assertTrue(cluster.bottom > rootTop)
    }

    @Test
    fun placesFiveNodeStarDiagonallyFromHintedHorizontalCompound() {
        val scene = render(
            """
                architecture-beta
                  group primary(cloud)[Primary]
                  service first(server)[First] in primary
                  service second(server)[Second] in primary
                  service third(server)[Third] in primary
                  first:R --> L:second
                  second:R --> L:third
                  align row first second third
                  group detached(cloud)[Detached]
                  service center(database)[Center] in detached
                  service leaf_a(database)[Leaf A] in detached
                  service leaf_b(database)[Leaf B] in detached
                  service leaf_c(database)[Leaf C] in detached
                  service leaf_d(database)[Leaf D] in detached
                  center:R --> L:leaf_a
                  center:R --> L:leaf_b
                  center:R --> L:leaf_c
                  center:R --> L:leaf_d
            """.trimIndent(),
        )
        val primary = scene.shape("architecture-group-primary").bounds
        val detached = scene.shape("architecture-group-detached").bounds
        val primaryCenters = listOf("first", "second", "third")
            .map { id -> scene.serviceBounds(id).center }

        assertEquals(1, primaryCenters.map(ScenePoint::y).distinct().size)
        assertTrue(primaryCenters.zipWithNext().all { (first, second) ->
            first.x < second.x
        })
        assertTrue(kotlin.math.abs(detached.center.x - primary.center.x) > 80f)
        assertTrue(detached.center.y < primary.center.y)
    }

    @Test
    fun overlapsSmallDetachedStarWithHintedHorizontalCompoundOnXAxis() {
        val scene = render(
            """
                architecture-beta
                  group primary(cloud)[Primary]
                  service first(server)[First] in primary
                  service second(server)[Second] in primary
                  service third(server)[Third] in primary
                  first:R --> L:second
                  second:R --> L:third
                  align row first second third
                  group detached(cloud)[Detached]
                  service center(database)[Center] in detached
                  service leaf_a(database)[Leaf A] in detached
                  service leaf_b(database)[Leaf B] in detached
                  center:R --> L:leaf_a
                  center:R --> L:leaf_b
            """.trimIndent(),
        )
        val primary = scene.shape("architecture-group-primary").bounds
        val detached = scene.shape("architecture-group-detached").bounds

        assertTrue(
            detached.left < primary.center.x,
            "detached=$detached primary=$primary",
        )
        assertTrue(
            detached.right > primary.left,
            "detached=$detached primary=$primary",
        )
        assertTrue(
            detached.bottom < primary.top,
            "detached=$detached primary=$primary",
        )
    }

    @Test
    fun keepsCrossGroupEdgesWithinFcoseScale() {
        val scene = render(
            """
                architecture-beta
                  group runtime(server)[Delivery Platform]
                  service declare(server)[Declare] in runtime
                  service inspect(server)[Inspect] in runtime
                  service assess(server)[Assess] in runtime
                  service release(internet)[Release]
                  declare:R --> L:inspect
                  inspect:R --> L:assess
                  assess:R --> L:release
                  declare:B -[observes]-> T:release
            """.trimIndent(),
        )
        val centers = listOf("declare", "inspect", "assess", "release")
            .map { id -> scene.serviceBounds(id).center }
        val xSpan = centers.maxOf(ScenePoint::x) - centers.minOf(ScenePoint::x)
        val ySpan = centers.maxOf(ScenePoint::y) - centers.minOf(ScenePoint::y)

        assertTrue(xSpan in 200f..800f, "xSpan=$xSpan")
        assertTrue(ySpan in 100f..800f, "ySpan=$ySpan")
    }

    @Test
    fun detectorAcceptsArchitecturePrefixButGrammarRequiresBeta() {
        val result = engine.render(
            source = "architecture\nservice app",
            context = context,
        )

        assertIs<GMResult.Err<MermaidError>>(result)
        assertIs<MermaidError.Parse>(result.error)
    }

    private fun render(source: String): MermaidScene {
        val result = engine.render(source, context)
        return assertIs<GMResult.Ok<MermaidScene>>(result, result.toString()).value
    }

    private fun MermaidScene.shape(id: String): SceneShape =
        elements.filterIsInstance<SceneShape>().single { it.id == id }

    private fun MermaidScene.serviceBounds(id: String) =
        shape("architecture-service-icon-$id-background").bounds

    private fun com.swithun.cmpmermaid.core.SceneRect.contains(
        other: com.swithun.cmpmermaid.core.SceneRect,
    ): Boolean =
        left <= other.left &&
            top <= other.top &&
            right >= other.right &&
            bottom >= other.bottom
}

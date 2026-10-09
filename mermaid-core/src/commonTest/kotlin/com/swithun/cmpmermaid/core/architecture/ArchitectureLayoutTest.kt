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
import com.swithun.cmpmermaid.core.TextMetricProvider
import com.swithun.cmpmermaid.core.TextMetrics
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
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
    fun normalizesDisconnectedVerticalChainToOfficialCenterSpacing() {
        val scene = render(
            """
                architecture-beta
                  service gateway(internet)[Gateway]
                  service app(server)[Application]
                  service database(database)[Database]
                  gateway:R --> L:app
                  app:B --> T:database
                  group cluster(cloud)[Cluster]
                  service first(internet)[First] in cluster
                  service second(internet)[Second] in cluster
                  service third(internet)[Third] in cluster
                  first:B --> T:second
                  second:B --> T:third
            """.trimIndent(),
        )
        val centers = listOf("first", "second", "third")
            .map { id -> scene.serviceBounds(id).center }
        val baseBottom = listOf("gateway", "app", "database")
            .maxOf { id -> scene.serviceBounds(id).bottom }
        val cluster = scene.shape("architecture-group-cluster").bounds

        assertEquals(1, centers.map(ScenePoint::x).distinct().size)
        assertEquals(200f, centers[1].y - centers[0].y, 0.1f)
        assertEquals(200f, centers[2].y - centers[1].y, 0.1f)
        assertTrue(cluster.top < baseBottom)
        assertTrue(cluster.bottom > baseBottom + 300f)
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
    fun projectsRigidRootGridAroundDisconnectedHorizontalChain() {
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
                  service first(server)[First] in cluster
                  service second(server)[Second] in cluster
                  service third(server)[Third] in cluster
                  service fourth(server)[Fourth] in cluster
                  service fifth(server)[Fifth] in cluster
                  first:R --> L:second
                  second:R --> L:third
                  third:R --> L:fourth
                  fourth:R --> L:fifth
            """.trimIndent(),
        )
        val input = scene.serviceBounds("input_a").center
        val process = scene.serviceBounds("process_a").center
        val output = scene.serviceBounds("output_a").center
        val rightColumn = scene.serviceBounds("input_b").center
        val cluster = scene.shape("architecture-group-cluster").bounds

        assertEquals(200f, process.y - input.y, 0.1f)
        assertEquals(200f, output.y - process.y, 0.1f)
        assertEquals(cluster.center.x, (input.x + rightColumn.x) / 2f, 0.1f)
        assertTrue(cluster.top < output.y)
        assertTrue(cluster.bottom > process.y)
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
    fun projectsTwoRowRootGridAroundFourAndFiveNodeHorizontalChains() {
        listOf(4, 5).forEach { chainSize ->
            val chainNodes = (1..chainSize).joinToString("\n") { index ->
                "  service chain_$index(server)[Chain $index] in detached"
            }
            val chainEdges = (1 until chainSize).joinToString("\n") { index ->
                "  chain_$index:R --> L:chain_${index + 1}"
            }
            val scene = render(
                """
                    architecture-beta
                      service source_left(server)[Source left]
                      service source_right(server)[Source right]
                      service sink_left(database)[Sink left]
                      service sink_right(database)[Sink right]
                      source_left:B --> T:sink_left
                      source_right:B --> T:sink_right
                      align row source_left source_right
                      align row sink_left sink_right
                      align column source_left sink_left
                      align column source_right sink_right
                      group detached(cloud)[Detached]
                    $chainNodes
                    $chainEdges
                """.trimIndent(),
            )
            val sourceLeft = scene.serviceBounds("source_left").center
            val sourceRight = scene.serviceBounds("source_right").center
            val sinkLeft = scene.serviceBounds("sink_left").center
            val sinkRight = scene.serviceBounds("sink_right").center
            val detached = scene.shape("architecture-group-detached").bounds
            val columnSpan = sourceRight.x - sourceLeft.x

            assertEquals(sourceLeft.y, sourceRight.y, 0.1f)
            assertEquals(sinkLeft.y, sinkRight.y, 0.1f)
            assertEquals(sourceLeft.x, sinkLeft.x, 0.1f)
            assertEquals(sourceRight.x, sinkRight.x, 0.1f)
            assertEquals(171f, sinkLeft.y - sourceLeft.y, 0.1f)
            assertEquals(detached.center.x, (sourceLeft.x + sourceRight.x) / 2f, 0.1f)
            assertTrue(columnSpan >= detached.width + 149f)
        }
    }

    @Test
    fun projectsRigidRootGridsOutsideDetachedVerticalChains() {
        val twoRowScene = render(
            """
                architecture-beta
                  service source_left(server)[Source left]
                  service source_right(server)[Source right]
                  service sink_left(database)[Sink left]
                  service sink_right(database)[Sink right]
                  source_left:B --> T:sink_left
                  source_right:B --> T:sink_right
                  align row source_left source_right
                  align row sink_left sink_right
                  align column source_left sink_left
                  align column source_right sink_right
                  group detached(cloud)[Detached]
                  service chain_a(internet)[Chain A] in detached
                  service chain_b(internet)[Chain B] in detached
                  service chain_c(internet)[Chain C] in detached
                  service chain_d(internet)[Chain D] in detached
                  chain_a:B --> T:chain_b
                  chain_b:B --> T:chain_c
                  chain_c:B --> T:chain_d
            """.trimIndent(),
        )
        assertRigidColumnsOutsideDetachedComponent(
            scene = twoRowScene,
            leftColumnIds = listOf("source_left", "sink_left"),
            rightColumnIds = listOf("source_right", "sink_right"),
            detachedGroupId = "detached",
            expectedRowGap = 200f,
        )
        assertEquals(
            twoRowScene.shape("architecture-group-detached").bounds.center.y,
            (
                twoRowScene.serviceBounds("source_left").center.y +
                    twoRowScene.serviceBounds("sink_left").center.y
                ) / 2f,
            0.1f,
        )

        val threeRowScene = render(
            """
                architecture-beta
                  service input_left(server)[Input left]
                  service input_right(server)[Input right]
                  service process_left(server)[Process left]
                  service process_right(server)[Process right]
                  service output_left(disk)[Output left]
                  service output_right(disk)[Output right]
                  input_left:B --> T:process_left
                  input_right:B --> T:process_right
                  process_left:B --> T:output_left
                  process_right:B --> T:output_right
                  align row input_left input_right
                  align row process_left process_right
                  align row output_left output_right
                  align column input_left process_left output_left
                  align column input_right process_right output_right
                  group detached(cloud)[Detached]
                  service chain_a(internet)[Chain A] in detached
                  service chain_b(internet)[Chain B] in detached
                  service chain_c(internet)[Chain C] in detached
                  chain_a:B --> T:chain_b
                  chain_b:B --> T:chain_c
            """.trimIndent(),
        )
        assertRigidColumnsOutsideDetachedComponent(
            scene = threeRowScene,
            leftColumnIds = listOf("input_left", "process_left", "output_left"),
            rightColumnIds = listOf("input_right", "process_right", "output_right"),
            detachedGroupId = "detached",
            expectedRowGap = 200f,
        )
        assertEquals(
            171f,
            threeRowScene.serviceBounds("chain_a").center.y -
                threeRowScene.serviceBounds("input_left").center.y,
            0.1f,
        )
    }

    @Test
    fun centersDetachedHorizontalStarsBetweenTwoRigidRootColumns() {
        listOf(2, 3).forEach { leafCount ->
            val leaves = (1..leafCount).joinToString("\n") { index ->
                "  service leaf_$index(database)[Leaf $index] in detached"
            }
            val edges = (1..leafCount).joinToString("\n") { index ->
                "  center:R --> L:leaf_$index"
            }
            val scene = render(
                """
                    architecture-beta
                      service source_left(server)[Source left]
                      service source_right(server)[Source right]
                      service sink_left(database)[Sink left]
                      service sink_right(database)[Sink right]
                      source_left:B --> T:sink_left
                      source_right:B --> T:sink_right
                      align row source_left source_right
                      align row sink_left sink_right
                      align column source_left sink_left
                      align column source_right sink_right
                      group detached(cloud)[Detached]
                      service center(database)[Center] in detached
                    $leaves
                    $edges
                """.trimIndent(),
            )

            assertRigidColumnsOutsideDetachedComponent(
                scene = scene,
                leftColumnIds = listOf("source_left", "sink_left"),
                rightColumnIds = listOf("source_right", "sink_right"),
                detachedGroupId = "detached",
                expectedRowGap = 200f,
            )
            assertEquals(
                scene.shape("architecture-group-detached").bounds.center.y,
                (
                    scene.serviceBounds("source_left").center.y +
                        scene.serviceBounds("sink_left").center.y
                    ) / 2f,
                0.1f,
            )
        }
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
    fun pullsRestoredVerticalChainTowardNestedMainCompound() {
        val scene = render(
            """
                architecture-beta
                  group platform(cloud)[Platform]
                  group compute(server)[Compute] in platform
                  group storage(disk)[Storage] in platform
                  service api(server)[API] in compute
                  service worker(server)[Worker] in compute
                  service primary(database)[Primary] in storage
                  api:R --> L:worker
                  worker{group}:R --> L:primary{group}
                  group detached(cloud)[Detached]
                  service first(internet)[First] in detached
                  service second(internet)[Second] in detached
                  first:B --> T:second
            """.trimIndent(),
        )
        val platform = scene.shape("architecture-group-platform").bounds
        val detached = scene.shape("architecture-group-detached").bounds

        assertTrue(detached.center.x < platform.center.x)
        assertTrue(platform.top - detached.bottom in 0f..20.1f)
    }

    @Test
    fun packsFiveNodeHorizontalChainBesideTallMainCompound() {
        val scene = render(
            """
                architecture-beta
                  group primary(cloud)[Primary]
                  group compute(server)[Compute] in primary
                  group storage(database)[Storage] in primary
                  service gateway(internet)[Gateway] in primary
                  service api(server)[API] in compute
                  service worker(server)[Worker] in compute
                  service database(database)[Database] in storage
                  gateway{group}:R --> L:api{group}
                  api:B --> T:worker
                  worker{group}:R --> L:database{group}
                  group detached(cloud)[Detached]
                  service chain_a(server)[Chain A] in detached
                  service chain_b(server)[Chain B] in detached
                  service chain_c(server)[Chain C] in detached
                  service chain_d(server)[Chain D] in detached
                  service chain_e(server)[Chain E] in detached
                  chain_a:R --> L:chain_b
                  chain_b:R --> L:chain_c
                  chain_c:R --> L:chain_d
                  chain_d:R --> L:chain_e
            """.trimIndent(),
        )
        val primary = scene.shape("architecture-group-primary").bounds
        val detached = scene.shape("architecture-group-detached").bounds
        val chainCenters = listOf(
            "chain_a",
            "chain_b",
            "chain_c",
            "chain_d",
            "chain_e",
        ).map { id -> scene.serviceBounds(id).center }
        val horizontalGap = primary.left - detached.right

        assertEquals(1, chainCenters.map(ScenePoint::y).distinct().size)
        assertTrue(chainCenters.zipWithNext().all { (first, second) ->
            second.x - first.x == 200f
        })
        assertTrue(horizontalGap in 0f..40f)
        assertTrue(detached.top < primary.bottom)
        assertTrue(detached.center.y < primary.center.y)
    }

    @Test
    fun expandsCyclicRootComponentAroundDetachedHorizontalChains() {
        listOf(2, 5).forEach { chainSize ->
            val chainNodes = (1..chainSize).joinToString("\n") { index ->
                "  service chain_$index(server)[Chain $index] in detached"
            }
            val chainEdges = (1 until chainSize).joinToString("\n") { index ->
                "  chain_$index:R --> L:chain_${index + 1}"
            }
            val scene = render(
                """
                    architecture-beta
                      service client(internet)[Client]
                      service gateway(server)[Gateway]
                      service cache(disk)[Cache]
                      service database(database)[Database]
                      client:R --> L:gateway
                      gateway:B --> T:cache
                      cache:R <--> L:database
                      database:T <-- B:gateway
                      group detached(cloud)[Detached]
                    $chainNodes
                    $chainEdges
                """.trimIndent(),
            )
            val rootCenters = listOf("client", "gateway", "cache", "database")
                .map { id -> scene.serviceBounds(id).center }
            val minimumY = rootCenters.minOf(ScenePoint::y)
            val maximumY = rootCenters.maxOf(ScenePoint::y)
            val minimumX = rootCenters.minOf(ScenePoint::x)
            val maximumX = rootCenters.maxOf(ScenePoint::x)
            val detached = scene.shape("architecture-group-detached").bounds

            assertTrue(maximumY - minimumY in 275f..290f)
            assertEquals((minimumX + maximumX) / 2f, detached.center.x, 0.1f)
            assertTrue(
                kotlin.math.abs(
                    (minimumY + maximumY) / 2f - detached.center.y,
                ) <= 10f,
            )
        }
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

    private fun assertRigidColumnsOutsideDetachedComponent(
        scene: MermaidScene,
        leftColumnIds: List<String>,
        rightColumnIds: List<String>,
        detachedGroupId: String,
        expectedRowGap: Float,
    ) {
        val leftColumn = leftColumnIds.map { id -> scene.serviceBounds(id) }
        val rightColumn = rightColumnIds.map { id -> scene.serviceBounds(id) }
        val detached = scene.shape("architecture-group-$detachedGroupId").bounds

        assertEquals(1, leftColumn.map { bounds -> bounds.center.x }.distinct().size)
        assertEquals(1, rightColumn.map { bounds -> bounds.center.x }.distinct().size)
        assertTrue(leftColumn.zipWithNext().all { (first, second) ->
            kotlin.math.abs(second.center.y - first.center.y - expectedRowGap) <= 0.1f
        })
        assertTrue(rightColumn.zipWithNext().all { (first, second) ->
            kotlin.math.abs(second.center.y - first.center.y - expectedRowGap) <= 0.1f
        })
        assertTrue(leftColumn.maxOf { bounds -> bounds.right } < detached.left)
        assertTrue(detached.right < rightColumn.minOf { bounds -> bounds.left })
        assertEquals(
            detached.center.x,
            (
                leftColumn.first().center.x +
                    rightColumn.first().center.x
                ) / 2f,
            0.1f,
        )
    }

    private fun com.swithun.cmpmermaid.core.SceneRect.contains(
        other: com.swithun.cmpmermaid.core.SceneRect,
    ): Boolean =
        left <= other.left &&
            top <= other.top &&
            right >= other.right &&
            bottom >= other.bottom
}

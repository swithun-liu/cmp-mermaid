package io.github.cmpmermaid.core.xychart

import io.github.cmpmermaid.core.GMResult
import io.github.cmpmermaid.core.MermaidEngine
import io.github.cmpmermaid.core.MermaidError
import io.github.cmpmermaid.core.MermaidRenderContext
import io.github.cmpmermaid.core.MermaidScene
import io.github.cmpmermaid.core.MermaidTheme
import io.github.cmpmermaid.core.SceneColor
import io.github.cmpmermaid.core.ScenePath
import io.github.cmpmermaid.core.SceneShape
import io.github.cmpmermaid.core.SceneText
import io.github.cmpmermaid.core.TextMetricProvider
import io.github.cmpmermaid.core.TextMetrics
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class XyChartLayoutTest {
    private val engine = MermaidEngine()
    private val context = MermaidRenderContext(
        textMetrics = TextMetricProvider { request ->
            TextMetrics(
                width = request.text.length * request.fontSize * 0.55f,
                height = request.fontSize,
            )
        },
        theme = MermaidTheme.MermaidDefault,
    )

    @Test
    fun rendersVerticalBarsLineAxesTitleAndLegend() {
        val scene = render(
            """
            xychart
                title "Sales Revenue"
                x-axis "Month" [Jan, Feb, Mar, Apr]
                y-axis "Revenue" 0 --> 100
                bar "Actual" [20, 45, 60, 85]
                line "Target" [25, 40, 65, 80]
            """.trimIndent(),
        )

        assertEquals(700f, scene.width)
        assertEquals(500f, scene.height)
        assertEquals(4, scene.elements.filterIsInstance<SceneShape>().count {
            it.id.startsWith("xy-bar-")
        })
        assertTrue(scene.elements.filterIsInstance<ScenePath>().any { it.id == "xy-line-1" })
        assertTrue(scene.elements.filterIsInstance<SceneText>().any {
            it.text == "Sales Revenue"
        })
        assertTrue(scene.elements.filterIsInstance<SceneText>().any {
            it.text == "Actual"
        })
        assertTrue(scene.elements.filterIsInstance<SceneText>().any {
            it.text == "Revenue" && it.rotationDegrees == 270f
        })
    }

    @Test
    fun rendersHorizontalChartAndDataLabels() {
        val scene = render(
            """
            ---
            config:
              xyChart:
                showDataLabel: true
                showDataLabelOutsideBar: true
            ---
            xychart horizontal
                x-axis [A, B, C]
                y-axis 0 --> 30
                bar [10, 20, 30]
            """.trimIndent(),
        )

        val bars = scene.elements.filterIsInstance<SceneShape>().filter {
            it.id.startsWith("xy-bar-")
        }
        val labels = scene.elements.filterIsInstance<SceneText>().filter {
            it.text in setOf("10", "20", "30") && it.zIndex == 18
        }

        assertEquals(3, bars.size)
        assertEquals(3, labels.size)
        assertTrue(bars.all { it.bounds.width > 0f && it.bounds.height > 0f })
        assertTrue(labels.zip(bars).all { (label, bar) -> label.bounds.left >= bar.bounds.right })
    }

    @Test
    fun appliesNestedConfigAndThemeVariables() {
        val scene = render(
            """
            ---
            config:
              xyChart:
                width: 540
                height: 360
                titleFontSize: 24
                xAxis:
                  labelRotation: -45
              themeVariables:
                xyChart:
                  backgroundColor: "#f0fdf4"
                  titleColor: "#166534"
                  plotColorPalette: "#dc2626, #2563eb"
            ---
            xychart
                title "Configured"
                x-axis [One, Two]
                bar [2, 4]
                line [1, 3]
            """.trimIndent(),
        )

        assertEquals(540f, scene.width)
        assertEquals(360f, scene.height)
        assertEquals(SceneColor(0xFFF0FDF4), scene.background)
        assertEquals(
            SceneColor(0xFF166534),
            scene.elements.filterIsInstance<SceneText>()
                .single { it.text == "Configured" }
                .color,
        )
        assertEquals(
            SceneColor(0xFFDC2626),
            scene.elements.filterIsInstance<SceneShape>()
                .first { it.id.startsWith("xy-bar-") }
                .fill,
        )
        assertTrue(scene.elements.filterIsInstance<SceneText>().any {
            it.text == "One" && it.rotationDegrees == -45f
        })
    }

    @Test
    fun keepsEqualLinearDomainAtRangeMidpoint() {
        val scene = render(
            """
            xychart
                x-axis 5 --> 5
                y-axis 10 --> 10
                line [10]
            """.trimIndent(),
        )
        val path = scene.elements.filterIsInstance<ScenePath>()
            .single { it.id == "xy-line-0" }

        assertEquals(1, path.points.size)
        assertTrue(path.points.single().x in 300f..400f)
        assertTrue(path.points.single().y in 200f..300f)
    }

    @Test
    fun reportsInvalidConfigurationWithoutThrowing() {
        val result = engine.render(
            """
            ---
            config:
              xyChart:
                width: 0
            ---
            xychart
                line [1, 2, 3]
            """.trimIndent(),
            context,
        )

        val error = assertIs<GMResult.Err<MermaidError>>(result).error
        assertIs<MermaidError.Configuration>(error)
        assertTrue(error.message.contains("xyChart.width"))
    }

    private fun render(source: String): MermaidScene {
        val result = engine.render(source, context)
        return assertIs<GMResult.Ok<MermaidScene>>(
            result,
            "Expected XY Chart render success:\n$source\n$result",
        ).value
    }
}

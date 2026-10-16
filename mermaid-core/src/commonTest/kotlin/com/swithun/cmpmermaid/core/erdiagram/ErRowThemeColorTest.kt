package com.swithun.cmpmermaid.core.erdiagram

import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidEngine
import com.swithun.cmpmermaid.core.MermaidRenderContext
import com.swithun.cmpmermaid.core.MermaidScene
import com.swithun.cmpmermaid.core.MermaidTheme
import com.swithun.cmpmermaid.core.MermaidThemePreset
import com.swithun.cmpmermaid.core.SceneColor
import com.swithun.cmpmermaid.core.SceneShape
import com.swithun.cmpmermaid.core.TextMetricProvider
import com.swithun.cmpmermaid.core.TextMetrics
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ErRowThemeColorTest {
    private val source = """
        erDiagram
            SESSION {
                string id
                string workdir
            }
    """.trimIndent()
    private val textMetrics = TextMetricProvider { request ->
        TextMetrics(
            width = request.text.length * 8f,
            height = request.fontSize * request.lineHeight,
        )
    }

    @Test
    fun paintsDarkThemeRowsWithExactMermaid12Colors() {
        assertEquals(
            listOf(SceneColor(0xFF2C2D2D), SceneColor(0xFF060606)),
            rowFills(MermaidTheme.preset(MermaidThemePreset.Dark)),
        )
    }

    @Test
    fun leavesUndefinedReduxRowsUnfilledLikeUpstreamErBox() {
        assertEquals(
            listOf(SceneColor(0x00000000), SceneColor(0x00000000)),
            rowFills(MermaidTheme.preset(MermaidThemePreset.ReduxColor)),
        )
    }

    @Test
    fun paintsExplicitThemeVariableOverrides() {
        val theme = assertIs<GMResult.Ok<MermaidTheme>>(
            MermaidTheme.withVariables(
                theme = MermaidTheme.preset(MermaidThemePreset.ReduxColor),
                values = mapOf(
                    "rowOdd" to "#112233",
                    "rowEven" to "#445566",
                ),
                themeName = MermaidThemePreset.ReduxColor.configName,
            ),
        ).value

        assertEquals(
            listOf(SceneColor(0xFF112233), SceneColor(0xFF445566)),
            rowFills(theme),
        )
    }

    private fun rowFills(theme: MermaidTheme): List<SceneColor> {
        val rendered = assertIs<GMResult.Ok<MermaidScene>>(
            MermaidEngine().render(
                source = source,
                context = MermaidRenderContext(
                    textMetrics = textMetrics,
                    theme = theme,
                ),
            ),
        ).value

        return rendered.elements
            .filterIsInstance<SceneShape>()
            .filter { shape -> "-row-" in shape.id }
            .map(SceneShape::fill)
    }
}

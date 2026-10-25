@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package com.swithun.cmpmermaid.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidRenderOptions
import com.swithun.cmpmermaid.core.MermaidScene
import com.swithun.cmpmermaid.core.SceneColor
import com.swithun.cmpmermaid.core.SceneRect
import com.swithun.cmpmermaid.core.SceneShadow
import com.swithun.cmpmermaid.core.SceneShape
import com.swithun.cmpmermaid.core.SceneShapeKind
import com.swithun.cmpmermaid.core.SceneText
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class LabelCompatibilityRenderingTest {
    @Test
    fun actualHtmlMeasurementKeepsMoreThanEightLines() = runBlocking {
        val short = prepared("flowchart TD\nA[\"One<br/>Two<br/>Three<br/>Four\"]")
            .elements.filterIsInstance<SceneText>().single()
        val long = prepared("flowchart TD\nA[\"${(1..12).joinToString("<br/>") { "Line $it" }}\"]")
            .elements.filterIsInstance<SceneText>().single()
        assertEquals(12, long.text.lines().size)
        assertEquals(short.bounds.height * 3f, long.bounds.height, 0.1f)
        assertTrue(long.bounds.height > 200f)
    }

    @Test
    fun emptyHtmlSpanHasZeroHeightButExplicitBreaksHaveLineBoxes() = runBlocking {
        val empty = prepared("flowchart TD\nA[\" \"]").elements.filterIsInstance<SceneText>().single()
        val breaks = prepared("flowchart TD\nA[\"<br/><br/>\"]").elements.filterIsInstance<SceneText>().single()
        assertEquals(0f, empty.bounds.width)
        assertEquals(0f, empty.bounds.height)
        assertTrue(breaks.bounds.height > 0f)
    }

    @Test
    fun transparentPaintCannotCastAnOpaqueShadowOnCanvas() {
        val transparent = SceneShape(
            id = "transparent",
            bounds = SceneRect(10f, 10f, 30f, 30f),
            kind = SceneShapeKind.Rectangle,
            fill = SceneColor(0),
            stroke = SceneColor(0),
            shadow = SceneShadow(SceneColor(0xFF000000), 8f, 8f, 2f),
        )
        assertTrue(pixels(transparent).contentEquals(pixels(transparent.copy(shadow = null))))
        val opaque = transparent.copy(fill = SceneColor(0xFFFF0000))
        assertTrue(!pixels(opaque).contentEquals(pixels(opaque.copy(shadow = null))))
    }

    @Test
    fun sourceAlphaMultipliesShadowAndBlurSampleAlpha() {
        assertEquals(0L, mermaidShadowPaint(SceneColor(0), SceneColor(0xFF000000), 1f).argb)
        assertEquals(0x20123456L, mermaidShadowPaint(
            SceneColor(0x80FFFFFF), SceneColor(0x80123456), 0.5f,
        ).argb)
    }

    private suspend fun prepared(source: String): MermaidScene {
        var prepared: MermaidPreparedScene? = null
        val canvas = ImageComposeScene(800, 900)
        try {
            canvas.setContent {
                prepared = rememberMermaidPreparedScene(
                    source = source,
                    options = MermaidRenderOptions(fontFamily = "Arial"),
                    formulaRenderer = null,
                ).value
            }
            val deadline = System.nanoTime() + 10_000_000_000L
            while (prepared == null && System.nanoTime() < deadline) {
                canvas.render().close()
                delay(10)
            }
            return assertIs<GMResult.Ok<MermaidScene>>(prepared?.result).value
        } finally {
            canvas.close()
        }
    }

    private fun pixels(shape: SceneShape): IntArray {
        val canvas = ImageComposeScene(64, 64)
        try {
            val scene = MermaidScene(64f, 64f, SceneColor(0xFFFFFFFF), listOf(shape))
            canvas.setContent {
                MermaidSceneCanvas(scene, Modifier.fillMaxSize(), respectSourceViewportSizing = false)
            }
            val image = canvas.render()
            val data = try { image.encodeToData()!! } finally { image.close() }
            val png = try { data.bytes } finally { data.close() }
            val bitmap = ImageIO.read(ByteArrayInputStream(png))
            return bitmap.getRGB(0, 0, 64, 64, null, 0, 64)
        } finally {
            canvas.close()
        }
    }
}

package io.github.cmpmermaid.core.flowchart.upstream.mermaid

import io.github.cmpmermaid.core.GMResult
import io.github.cmpmermaid.core.MermaidError
import io.github.cmpmermaid.core.MermaidRenderOptions
import io.github.cmpmermaid.core.SceneTextSpan
import io.github.cmpmermaid.core.SceneTextWeight
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MermaidTextPortTest {
    @Test
    fun translatesMarkedStrongAndEmphasisTokens() {
        val result = MermaidTextPort.render(
            source = "This **is** _Markdown_",
            labelType = FlowLabelType.Markdown,
            config = MermaidRenderOptions(htmlLabels = false),
        )

        val text = assertIs<GMResult.Ok<MermaidRenderedText>>(result).value
        assertEquals("This is Markdown", text.text)
        assertEquals(
            listOf(
                SceneTextSpan(5, 7, weight = SceneTextWeight.Bold),
                SceneTextSpan(8, 16, italic = true),
            ),
            text.spans,
        )
    }

    @Test
    fun translatesMermaidLineBreakAndEntityBoundaries() {
        val result = MermaidTextPort.render(
            source = "Line one<br/>Line two &amp; &#35; &#x2764;",
            labelType = FlowLabelType.Text,
            config = MermaidRenderOptions(),
        )

        val text = assertIs<GMResult.Ok<MermaidRenderedText>>(result).value
        assertEquals("Line one\nLine two & # ❤", text.text)
    }

    @Test
    fun rejectsUntranslatedMarkedTokens() {
        val result = MermaidTextPort.render(
            source = "[label](https://example.com)",
            labelType = FlowLabelType.Markdown,
            config = MermaidRenderOptions(),
        )

        val error = assertIs<GMResult.Err<MermaidError>>(result).error
        assertIs<MermaidError.UnsupportedFeature>(error)
        assertContains(error.message, "link or image")
    }
}

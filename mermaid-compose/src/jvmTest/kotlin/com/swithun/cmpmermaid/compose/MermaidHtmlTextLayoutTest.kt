package com.swithun.cmpmermaid.compose

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import kotlin.test.Test
import kotlin.test.assertEquals

class MermaidHtmlTextLayoutTest {
    @Test
    fun wrapsOnlyAtBreakSpaces() {
        val wrapped = AnnotatedString("Alpha Beta Gamma").withCssBreakSpacesLines(
            maximumWidth = 10f,
            measureWidth = ::monospaceWidth,
        )

        assertEquals("Alpha \nBeta Gamma", wrapped.text)
    }

    @Test
    fun preservesOversizedWordsInsteadOfFragmentingThem() {
        val wrapped = AnnotatedString("Electrocardiograph").withCssBreakSpacesLines(
            maximumWidth = 8f,
            measureWidth = ::monospaceWidth,
        )

        assertEquals("Electrocardiograph", wrapped.text)
    }

    @Test
    fun wrapsAtUnicodeLineBreakOpportunities() {
        val hyphenated = AnnotatedString("well-defined").withCssBreakSpacesLines(
            maximumWidth = 6f,
            measureWidth = ::monospaceWidth,
        )
        val cjk = AnnotatedString("漢字漢字").withCssBreakSpacesLines(
            maximumWidth = 2f,
            measureWidth = ::monospaceWidth,
        )
        val zeroWidthSpace = AnnotatedString("A\u200BB").withCssBreakSpacesLines(
            maximumWidth = 1f,
            measureWidth = { value ->
                value.text.count { character -> character != '\u200B' }.toFloat()
            },
        )

        assertEquals("well-\ndefined", hyphenated.text)
        assertEquals("漢字\n漢字", cjk.text)
        assertEquals("A\u200B\nB", zeroWidthSpace.text)
    }

    @Test
    fun preservesEverySpaceWhileWrapping() {
        val wrapped = AnnotatedString("A  B").withCssBreakSpacesLines(
            maximumWidth = 2f,
            measureWidth = ::monospaceWidth,
        )

        assertEquals("A \n B", wrapped.text)
    }

    @Test
    fun preservesExplicitLinesAndUnbreakablePunctuation() {
        val wrapped = AnnotatedString("well-defined next_value\nfinal line")
            .withCssBreakSpacesLines(
                maximumWidth = 12f,
                measureWidth = ::monospaceWidth,
            )

        assertEquals("well-\ndefined \nnext_value\nfinal line", wrapped.text)
    }

    @Test
    fun remapsAnnotationsWithoutChangingVisibleText() {
        val bold = SpanStyle(fontWeight = FontWeight.Bold)
        val wrapped = buildAnnotatedString {
            append("One Two")
            addStyle(bold, start = 4, end = 7)
            addStringAnnotation(
                tag = "kind",
                annotation = "label",
                start = 4,
                end = 7,
            )
        }.withCssBreakSpacesLines(
            maximumWidth = 4f,
            measureWidth = ::monospaceWidth,
        )

        val range = wrapped.spanStyles.single()
        val annotation = wrapped.getStringAnnotations("kind", 0, wrapped.length).single()
        assertEquals(bold, range.item)
        assertEquals("Two", wrapped.text.substring(range.start, range.end))
        assertEquals("Two", wrapped.text.substring(annotation.start, annotation.end))
    }

    private fun monospaceWidth(text: AnnotatedString): Float = text.length.toFloat()
}

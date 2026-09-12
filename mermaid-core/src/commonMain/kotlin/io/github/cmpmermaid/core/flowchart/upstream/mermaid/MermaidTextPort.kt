package io.github.cmpmermaid.core.flowchart.upstream.mermaid

import io.github.cmpmermaid.core.GMResult
import io.github.cmpmermaid.core.MermaidError
import io.github.cmpmermaid.core.MermaidPreprocessor
import io.github.cmpmermaid.core.MermaidRenderOptions
import io.github.cmpmermaid.core.SceneTextSpan
import io.github.cmpmermaid.core.SceneTextWeight

/**
 * Native text model for the subset emitted by Mermaid 12.0.0 createText.ts and
 * handle-markdown-text.ts. Unsupported marked/HTML tokens fail explicitly.
 */
internal object MermaidTextPort {
    fun render(
        source: String,
        labelType: FlowLabelType,
        config: MermaidRenderOptions,
    ): GMResult<MermaidRenderedText, MermaidError> {
        val decoded = when (val result = decodeHtmlEntities(MermaidPreprocessor.decodeEntities(source))) {
            is GMResult.Ok -> result.value
            is GMResult.Err -> return result
        }
        if (KATEX.containsMatchIn(decoded)) {
            return unsupported("KaTeX label")
        }
        return when (labelType) {
            FlowLabelType.Markdown -> renderMarkdown(decoded, config.markdownAutoWrap)
            FlowLabelType.String,
            FlowLabelType.Text,
            -> renderNonMarkdown(decoded)
        }
    }

    private fun renderNonMarkdown(source: String): GMResult<MermaidRenderedText, MermaidError> {
        val normalized = source
            .replace(LINE_BREAK, "\n")
            .replace("\\n", "\n")
        val unsupportedTag = HTML_TAG.find(normalized)
        if (unsupportedTag != null) {
            return unsupported("HTML label tag '${unsupportedTag.value}'")
        }
        return GMResult.Ok(MermaidRenderedText(normalized))
    }

    private fun renderMarkdown(
        source: String,
        autoWrap: Boolean,
    ): GMResult<MermaidRenderedText, MermaidError> {
        val preprocessed = preprocessMarkdown(source, autoWrap)
        val builder = StyledTextBuilder()
        return when (val parsed = parseMarkdownRange(preprocessed, 0, null, TextStyle(), builder)) {
            is GMResult.Ok -> GMResult.Ok(builder.build())
            is GMResult.Err -> parsed
        }
    }

    private fun preprocessMarkdown(
        source: String,
        autoWrap: Boolean,
    ): String {
        val withoutBreaks = source.replace(LINE_BREAK, "\n")
        val withoutMultipleNewlines = withoutBreaks.replace(MULTIPLE_NEWLINES, "\n")
        val dedented = dedent(withoutMultipleNewlines)
        return if (autoWrap) dedented else dedented
    }

    private fun dedent(source: String): String {
        val lines = source.lines()
        val indent = lines
            .filter(String::isNotBlank)
            .map { line -> line.indexOfFirst { !it.isWhitespace() }.coerceAtLeast(0) }
            .minOrNull()
            ?: 0
        return lines.joinToString("\n") { line ->
            if (line.isBlank()) "" else line.drop(indent)
        }
    }

    private fun parseMarkdownRange(
        source: String,
        start: Int,
        closing: String?,
        style: TextStyle,
        output: StyledTextBuilder,
    ): GMResult<Int, MermaidError> {
        var index = start
        while (index < source.length) {
            if (closing != null && source.startsWith(closing, index)) {
                return GMResult.Ok(index + closing.length)
            }
            when {
                source[index] == '\\' && index + 1 < source.length -> {
                    output.append(source[index + 1].toString(), style)
                    index += 2
                }
                source.startsWith("**", index) || source.startsWith("__", index) -> {
                    val marker = source.substring(index, index + 2)
                    if (source.indexOf(marker, index + marker.length) < 0) {
                        output.append(marker, style)
                        index += marker.length
                    } else {
                        when (
                            val nested = parseMarkdownRange(
                                source = source,
                                start = index + marker.length,
                                closing = marker,
                                style = style.copy(strong = true),
                                output = output,
                            )
                        ) {
                            is GMResult.Ok -> index = nested.value
                            is GMResult.Err -> return nested
                        }
                    }
                }
                source[index] == '*' || source[index] == '_' -> {
                    val marker = source[index].toString()
                    if (source.indexOf(marker, index + 1) < 0) {
                        output.append(marker, style)
                        index += 1
                    } else {
                        when (
                            val nested = parseMarkdownRange(
                                source = source,
                                start = index + 1,
                                closing = marker,
                                style = style.copy(emphasis = true),
                                output = output,
                            )
                        ) {
                            is GMResult.Ok -> index = nested.value
                            is GMResult.Err -> return nested
                        }
                    }
                }
                source.startsWith("<strong>", index, ignoreCase = true) -> {
                    when (
                        val nested = parseHtmlStyle(
                            source = source,
                            start = index,
                            opening = "<strong>",
                            closing = "</strong>",
                            style = style.copy(strong = true),
                            output = output,
                        )
                    ) {
                        is GMResult.Ok -> index = nested.value
                        is GMResult.Err -> return nested
                    }
                }
                source.startsWith("<b>", index, ignoreCase = true) -> {
                    when (
                        val nested = parseHtmlStyle(
                            source = source,
                            start = index,
                            opening = "<b>",
                            closing = "</b>",
                            style = style.copy(strong = true),
                            output = output,
                        )
                    ) {
                        is GMResult.Ok -> index = nested.value
                        is GMResult.Err -> return nested
                    }
                }
                source.startsWith("<em>", index, ignoreCase = true) -> {
                    when (
                        val nested = parseHtmlStyle(
                            source = source,
                            start = index,
                            opening = "<em>",
                            closing = "</em>",
                            style = style.copy(emphasis = true),
                            output = output,
                        )
                    ) {
                        is GMResult.Ok -> index = nested.value
                        is GMResult.Err -> return nested
                    }
                }
                source.startsWith("<i>", index, ignoreCase = true) -> {
                    when (
                        val nested = parseHtmlStyle(
                            source = source,
                            start = index,
                            opening = "<i>",
                            closing = "</i>",
                            style = style.copy(emphasis = true),
                            output = output,
                        )
                    ) {
                        is GMResult.Ok -> index = nested.value
                        is GMResult.Err -> return nested
                    }
                }
                source[index] == '`' -> return unsupported("inline code label")
                source[index] == '[' ||
                    (source[index] == '!' && source.getOrNull(index + 1) == '[') ->
                    return unsupported("Markdown link or image label")
                source[index] == '<' -> {
                    val tag = HTML_TAG.find(source, index)
                    if (tag?.range?.first == index) {
                        return unsupported("HTML label tag '${tag.value}'")
                    }
                    output.append("<", style)
                    index += 1
                }
                else -> {
                    val next = nextSpecialIndex(source, index + 1, closing)
                    output.append(source.substring(index, next), style)
                    index = next
                }
            }
        }
        return if (closing == null) {
            GMResult.Ok(index)
        } else {
            GMResult.Err(
                MermaidError.Parse(
                    line = 1,
                    column = start + 1,
                    message = "Unclosed Markdown delimiter '$closing'",
                ),
            )
        }
    }

    private fun parseHtmlStyle(
        source: String,
        start: Int,
        opening: String,
        closing: String,
        style: TextStyle,
        output: StyledTextBuilder,
    ): GMResult<Int, MermaidError> {
        val end = source.indexOf(closing, start + opening.length, ignoreCase = true)
        if (end < 0) {
            return GMResult.Err(
                MermaidError.Parse(
                    line = 1,
                    column = start + 1,
                    message = "Unclosed HTML label tag '$opening'",
                ),
            )
        }
        val content = source.substring(start + opening.length, end)
        return when (val parsed = parseMarkdownRange(content, 0, null, style, output)) {
            is GMResult.Ok -> GMResult.Ok(end + closing.length)
            is GMResult.Err -> parsed
        }
    }

    private fun nextSpecialIndex(
        source: String,
        start: Int,
        closing: String?,
    ): Int {
        var index = start
        while (index < source.length) {
            if (
                (closing != null && source.startsWith(closing, index)) ||
                source[index] in SPECIAL_CHARACTERS
            ) {
                return index
            }
            index += 1
        }
        return source.length
    }

    private fun decodeHtmlEntities(source: String): GMResult<String, MermaidError> {
        val result = StringBuilder()
        var cursor = 0
        HTML_ENTITY.findAll(source).forEach { match ->
            result.append(source, cursor, match.range.first)
            val body = match.groupValues[1]
            val decoded = when {
                body.startsWith("#x", ignoreCase = true) ->
                    body.drop(2).toIntOrNull(16)?.let(::codePointToString)
                body.startsWith('#') ->
                    body.drop(1).toIntOrNull()?.let(::codePointToString)
                else -> NAMED_ENTITIES[body]
            } ?: return unsupported("HTML entity '&$body;'")
            result.append(decoded)
            cursor = match.range.last + 1
        }
        result.append(source, cursor, source.length)
        return GMResult.Ok(result.toString())
    }

    private fun codePointToString(codePoint: Int): String? = when (codePoint) {
        in 0..0xD7FF,
        in 0xE000..0xFFFF,
        -> codePoint.toChar().toString()
        in 0x10000..0x10FFFF -> {
            val normalized = codePoint - 0x10000
            val high = (0xD800 + (normalized shr 10)).toChar()
            val low = (0xDC00 + (normalized and 0x3FF)).toChar()
            "$high$low"
        }
        else -> null
    }

    private fun <T> unsupported(feature: String): GMResult<T, MermaidError> =
        GMResult.Err(
            MermaidError.UnsupportedFeature(
                feature = feature,
                message = "Native Mermaid has not translated Mermaid $feature rendering",
            ),
        )

    private class StyledTextBuilder {
        private val text = StringBuilder()
        private val spans = mutableListOf<SceneTextSpan>()

        fun append(
            value: String,
            style: TextStyle,
        ) {
            if (value.isEmpty()) {
                return
            }
            val start = text.length
            text.append(value)
            if (style.strong || style.emphasis) {
                spans += SceneTextSpan(
                    start = start,
                    end = text.length,
                    weight = if (style.strong) SceneTextWeight.Bold else null,
                    italic = style.emphasis,
                )
            }
        }

        fun build(): MermaidRenderedText =
            MermaidRenderedText(text = text.toString(), spans = spans)
    }

    private data class TextStyle(
        val strong: Boolean = false,
        val emphasis: Boolean = false,
    )

    private val LINE_BREAK = Regex("""</?br\s*/?>""", RegexOption.IGNORE_CASE)
    private val MULTIPLE_NEWLINES = Regex("""\n{2,}""")
    private val HTML_TAG = Regex("""</?[A-Za-z][^>]*>""")
    private val HTML_ENTITY = Regex("""&(#x[0-9A-Fa-f]+|#\d+|[A-Za-z][A-Za-z0-9]+);""")
    private val KATEX = Regex("""(?:\$\$?[^$]+\$\$?|\\\(|\\\[)""")
    private val SPECIAL_CHARACTERS = setOf('\\', '*', '_', '`', '[', '!', '<')
    private val NAMED_ENTITIES = mapOf(
        "amp" to "&",
        "apos" to "'",
        "gt" to ">",
        "lt" to "<",
        "nbsp" to "\u00A0",
        "quot" to "\"",
    )
}

internal data class MermaidRenderedText(
    val text: String,
    val spans: List<SceneTextSpan> = emptyList(),
)

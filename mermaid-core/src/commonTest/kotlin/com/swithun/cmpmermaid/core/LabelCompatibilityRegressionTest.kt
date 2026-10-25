package com.swithun.cmpmermaid.core

import com.swithun.cmpmermaid.core.classdiagram.upstream.mermaid.ClassDb
import com.swithun.cmpmermaid.core.classdiagram.upstream.mermaid.ClassJisonParser
import com.swithun.cmpmermaid.core.flowchart.FlowchartDataAdapter
import com.swithun.cmpmermaid.core.flowchart.FlowchartDocument
import com.swithun.cmpmermaid.core.flowchart.upstream.mermaid.FlowDb
import com.swithun.cmpmermaid.core.flowchart.upstream.mermaid.FlowJisonLexer
import com.swithun.cmpmermaid.core.flowchart.upstream.mermaid.FlowJisonParser
import com.swithun.cmpmermaid.core.flowchart.upstream.mermaid.FlowLabelType
import com.swithun.cmpmermaid.core.flowchart.upstream.mermaid.JisonToken
import com.swithun.cmpmermaid.core.flowchart.upstream.mermaid.MermaidRenderedText
import com.swithun.cmpmermaid.core.flowchart.upstream.mermaid.MermaidTextPort
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/** Mermaid.js 12.0.0 regression boundaries; all inputs are synthetic. */
class LabelCompatibilityRegressionTest {
    @Test
    fun directionTokensKeepGeneratedJisonMeanings() {
        // flow.jison: "^" -> UP; "v" -> DOWN.
        assertEquals("UP", assertIs<GMResult.Ok<JisonToken>>(
            FlowJisonLexer("^", firstGraph = { false }).next(),
        ).value.name)
        assertEquals("DOWN", assertIs<GMResult.Ok<JisonToken>>(
            FlowJisonLexer("v", firstGraph = { false }).next(),
        ).value.name)
        val db = assertIs<GMResult.Ok<FlowDb>>(
            FlowJisonParser().parse("flowchart TD\nv[Value] --> A[Result]\n"),
        ).value
        assertEquals(listOf("v", "A"), db.vertices().keys.toList())
        assertEquals("Value", db.vertices().getValue("v").text)
        assertEquals("v" to "A", db.edges().single().let { it.start to it.end })
    }

    @Test
    fun htmlTagOpenRequiresAnAsciiLetter() {
        // createText.addHtmlSpan -> innerHTML tag-open/end-tag-open states.
        listOf(
            "x < -2 or x > 2",
            "x < 2 and x > -2",
            "< 2 >",
            "<3>",
            "<é>",
            "A </ 2> B",
            "A <",
        ).forEach { source ->
            assertEquals(source, renderText(source), source)
        }
        assertEquals("bold\nnext", renderText("<b>bold</b><BR/>next"))
        assertEquals("Map", renderText("Map<Key,Value>"))
        assertEquals("left right", renderText("left <!--comment-->right"))
        assertEquals("safe", renderText("<script>hidden</script>safe"))
    }

    @Test
    fun commentsMatchUpstreamNormalIndentedAndEndOfInputCases() {
        // diagram-api/comments.spec.ts normal, indentation, trimStart and EOF.
        val source = "\n\n%% leading\nflowchart TD\n A --> B\n %% indented\n C --> D\n%% trailing"
        assertEquals("flowchart TD\n A --> B\n C --> D\n", preprocess(source).code.cleaned)
        assertEquals("flowchart TD\n%%\n A --> B\n", preprocess("flowchart TD\n%%\n A --> B\n").code.cleaned)
        val directive = preprocess("%% removed\n%%{init: {'theme': 'forest'}}%%\nflowchart TD\n A --> B")
        assertEquals("forest", directive.config.themeName)
        assertEquals("flowchart TD\n A --> B", directive.code.cleaned)
    }

    @Test
    fun emptyDagreGroupUsesAnOrdinaryRectNode() {
        // dagre/index.js -> measureDagreGraph/paintDagreLayoutCore children > 0.
        val db = assertIs<GMResult.Ok<FlowDb>>(FlowJisonParser().parse(
            "flowchart TD\nsubgraph Empty[Empty group]\nend\nsubgraph Full[Full group]\nA[Alpha]\nend\n",
        )).value
        val document = assertIs<GMResult.Ok<FlowchartDocument>>(
            FlowchartDataAdapter.convert(db),
        ).value
        assertEquals(setOf("Empty", "A"), document.nodes.keys)
        val empty = document.nodes.getValue("Empty")
        assertEquals(SceneShapeKind.Rectangle, empty.shape)
        assertEquals("Empty group", empty.label)
        assertEquals(db.getData().nodes.single { it.id == "Empty" }.padding, empty.padding)
        assertEquals(listOf("Full"), document.subgraphs.map { it.id })
        assertEquals(setOf("A"), document.subgraphs.single().nodeIds)
    }

    @Test
    fun classMembersAreSanitizedBeforeGenericFormattingAndMethodParsing() {
        // classDb.addMember -> ClassMember.constructor -> parseMember.
        val db = assertIs<GMResult.Ok<ClassDb>>(ClassJisonParser(MermaidSecurityLevel.Strict).parse(
            "classDiagram\nclass Store {\n+Map<Key,Value> raw\n+Map~Key,Value~ typed\n+load(Map<Key,Value> input) Result\n}\n",
        )).value
        val node = db.getClasses().getValue("Store")
        assertEquals(listOf("+Map raw", "+Map<Key,Value> typed"), node.members.map { it.displayText })
        assertEquals("+load(Map input) : Result", node.methods.single().displayText)
        assertIs<GMResult.Ok<Unit>>(db.addMember("Store", "<<interface>>"))
        assertEquals(listOf("interface"), node.annotations)
        assertIs<GMResult.Ok<Unit>>(db.addMember("Store", ")boundary"))
        assertEquals(")boundary", node.members.last().displayText)
        assertIs<GMResult.Ok<Unit>>(db.addMember("Store", "  "))
        assertEquals(3, node.members.size)
    }

    @Test
    fun classBulkMembersKeepUpstreamReverseOrderAndSafeResult() {
        // classDb.addMembers reverses its Jison stack before adding members.
        val db = ClassDb(MermaidSecurityLevel.Strict)
        assertIs<GMResult.Ok<Unit>>(db.addMembers("Box", listOf("+third", "+second", "+first")))
        assertEquals(listOf("+first", "+second", "+third"), db.getClasses().getValue("Box").members.map { it.displayText })
        val result = db.addMember("Box", "+<b =bad>member</b>")
        assertIs<GMResult.Err<MermaidError>>(result)
        assertEquals(3, db.getClasses().getValue("Box").members.size)
    }

    @Test
    fun themeRadiusUsesUpstreamConstructorsAndOverrideOrder() {
        // themes/theme-*.js -> Theme.constructor/calculate.
        val expected = mapOf(
            "default" to 5f, "base" to 5f, "dark" to 5f, "forest" to 5f, "neutral" to 5f,
            "neo" to 3f, "neo-dark" to 3f,
            "redux" to 12f, "redux-color" to 12f, "redux-dark" to 12f,
        )
        expected.forEach { (name, radius) ->
            assertEquals(radius, MermaidTheme.named(name)?.radius, name)
        }
        val overridden = assertIs<GMResult.Ok<MermaidTheme>>(
            MermaidTheme.withVariables(MermaidTheme.FlowchartDefault, mapOf("radius" to "7")),
        ).value
        assertEquals(7f, overridden.radius)
    }

    private fun renderText(source: String): String = assertIs<GMResult.Ok<MermaidRenderedText>>(
        MermaidTextPort.render(source, FlowLabelType.Text, MermaidRenderOptions()),
    ).value.text

    private fun preprocess(source: String): MermaidPreprocessResult =
        assertIs<GMResult.Ok<MermaidPreprocessResult>>(MermaidPreprocessor.preprocess(source)).value
}

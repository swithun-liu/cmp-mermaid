package io.github.cmpmermaid.core

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MermaidPreprocessorTest {
    @Test
    fun usesMermaid12DefaultElkLayout() {
        val result = MermaidPreprocessor.preprocess(
            """
                flowchart LR
                  A --> B
            """.trimIndent(),
        )

        val processed = assertIs<GMResult.Ok<MermaidPreprocessResult>>(result).value
        val options = assertIs<GMResult.Ok<MermaidRenderOptions>>(
            processed.config.applyTo(MermaidRenderOptions()),
        ).value

        assertEquals("elk", options.layout)
    }

    @Test
    fun directiveLayoutOverridesFrontmatterAndCallerOptions() {
        val result = MermaidPreprocessor.preprocess(
            """
                ---
                config:
                  layout: elk
                ---
                %%{init: {'layout': 'dagre'}}%%
                flowchart LR
                  A --> B
            """.trimIndent(),
        )

        val processed = assertIs<GMResult.Ok<MermaidPreprocessResult>>(result).value
        val options = assertIs<GMResult.Ok<MermaidRenderOptions>>(
            processed.config.applyTo(MermaidRenderOptions(layout = "elk.stress")),
        ).value

        assertEquals("dagre", options.layout)
    }

    @Test
    fun portsFrontmatterExtractionAndFlowchartConfig() {
        val result = MermaidPreprocessor.preprocess(
            """
                ---
                title: Configured graph
                config:
                  flowchart:
                    padding: 24
                    minNodeWidth: 180
                    nodeSpacing: 72
                    rankSpacing: 84
                    curve: linear
                    inheritDir: true
                    subGraphTitleMargin:
                      top: 10
                      bottom: 5
                ---
                flowchart TB
                  A --> B
            """.trimIndent(),
        )

        val processed = assertIs<GMResult.Ok<MermaidPreprocessResult>>(result).value
        val options = assertIs<GMResult.Ok<MermaidRenderOptions>>(
            processed.config.applyTo(MermaidRenderOptions()),
        ).value
        assertEquals("Configured graph", processed.title)
        assertEquals("flowchart TB\n  A --> B", processed.code.cleaned)
        assertEquals(14, processed.code.frontmatterLineOffset)
        assertEquals(24f, options.flowchartPadding)
        assertEquals(180f, options.minNodeWidth)
        assertEquals(72f, options.nodeSpacing)
        assertEquals(84f, options.rankSpacing)
        assertEquals("linear", options.curve)
        assertEquals(true, options.inheritDirection)
        assertEquals(10f, options.subGraphTitleTopMargin)
        assertEquals(5f, options.subGraphTitleBottomMargin)
    }

    @Test
    fun portsMermaidElkConfiguration() {
        val result = MermaidPreprocessor.preprocess(
            """
                ---
                config:
                  elk:
                    mergeEdges: true
                    nodePlacementStrategy: NETWORK_SIMPLEX
                    nodePlacementAlignment: RIGHTDOWN
                    preset: modelOrder
                    straightenEdges: false
                    lineHops: gap
                    layeringStrategy: COFFMAN_GRAHAM
                    layeringLayerBound: 7
                    cycleBreakingStrategy: GREEDY_MODEL_ORDER
                    forceNodeModelOrder: true
                    considerModelOrder: PREFER_NODES
                    keepEntryNodeOnTop: true
                ---
                flowchart LR
                  A --> B
            """.trimIndent(),
        )

        val processed = assertIs<GMResult.Ok<MermaidPreprocessResult>>(result).value
        val options = assertIs<GMResult.Ok<MermaidRenderOptions>>(
            processed.config.applyTo(MermaidRenderOptions()),
        ).value

        assertEquals(true, options.elk.mergeEdges)
        assertEquals("NETWORK_SIMPLEX", options.elk.nodePlacementStrategy)
        assertEquals("RIGHTDOWN", options.elk.nodePlacementAlignment)
        assertEquals("modelOrder", options.elk.preset)
        assertEquals(false, options.elk.straightenEdges)
        assertEquals(MermaidElkLineHops.Gap, options.elk.lineHops)
        assertEquals("COFFMAN_GRAHAM", options.elk.layeringStrategy)
        assertEquals(7, options.elk.layeringLayerBound)
        assertEquals("GREEDY_MODEL_ORDER", options.elk.cycleBreakingStrategy)
        assertEquals(true, options.elk.forceNodeModelOrder)
        assertEquals("PREFER_NODES", options.elk.considerModelOrder)
        assertEquals(true, options.elk.keepEntryNodeOnTop)
    }

    @Test
    fun portsMermaidPieConfiguration() {
        val result = MermaidPreprocessor.preprocess(
            """
                ---
                config:
                  pie:
                    textPosition: 0.4
                    donutHole: 0.3
                    legendPosition: bottom
                    highlightSlice: Potassium
                ---
                pie
                  "Calcium" : 40
                  "Potassium" : 60
            """.trimIndent(),
        )

        val processed = assertIs<GMResult.Ok<MermaidPreprocessResult>>(result).value
        val options = assertIs<GMResult.Ok<MermaidRenderOptions>>(
            processed.config.applyTo(MermaidRenderOptions()),
        ).value

        assertEquals(0.4f, options.pieTextPosition)
        assertEquals(0.3f, options.pieDonutHole)
        assertEquals("bottom", options.pieLegendPosition)
        assertEquals("Potassium", options.pieHighlightSlice)
    }

    @Test
    fun rejectsOutOfRangePieConfiguration() {
        listOf(
            "textPosition: 1.1",
            "donutHole: -0.1",
            "donutHole: 0.91",
            "legendPosition: diagonal",
        ).forEach { config ->
            val result = MermaidPreprocessor.preprocess(
                """
                    ---
                    config:
                      pie:
                        $config
                    ---
                    pie
                      "A" : 1
                """.trimIndent(),
            )

            assertIs<GMResult.Err<MermaidError>>(
                result,
                "Expected invalid Pie config to fail: $config",
            )
        }
    }

    @Test
    fun directiveOverridesFrontmatterLikeCleanAndMerge() {
        val result = MermaidPreprocessor.preprocess(
            """
                ---
                config:
                  flowchart:
                    padding: 20
                    curve: basis
                ---
                %%{init: {'flowchart': {'padding': 32, 'curve': 'step'}}}%%
                flowchart LR
                  A --> B
            """.trimIndent(),
        )

        val processed = assertIs<GMResult.Ok<MermaidPreprocessResult>>(result).value
        val options = assertIs<GMResult.Ok<MermaidRenderOptions>>(
            processed.config.applyTo(MermaidRenderOptions()),
        ).value
        assertEquals(32f, options.flowchartPadding)
        assertEquals("step", options.curve)
        assertEquals("flowchart LR\n  A --> B", processed.code.cleaned)
    }

    @Test
    fun stripsSecureHostOptionsFromDiagramConfig() {
        val result = MermaidPreprocessor.preprocess(
            """
                ---
                config:
                  maxEdges: 999
                  maxTextSize: 99999
                  securityLevel: loose
                  secure: []
                ---
                %%{init: {
                  'maxEdges': 1000,
                  'maxTextSize': 100000,
                  'securityLevel': 'loose'
                }}%%
                flowchart LR
                  A --> B
            """.trimIndent(),
        )

        val processed = assertIs<GMResult.Ok<MermaidPreprocessResult>>(result).value
        val options = assertIs<GMResult.Ok<MermaidRenderOptions>>(
            processed.config.applyTo(
                MermaidRenderOptions(
                    securityLevel = MermaidSecurityLevel.Strict,
                    maxTextSize = 17,
                    maxEdges = 7,
                ),
            ),
        ).value
        assertEquals(MermaidSecurityLevel.Strict, options.securityLevel)
        assertEquals(17, options.maxTextSize)
        assertEquals(7, options.maxEdges)
    }

    @Test
    fun returnsStructuredErrorForInvalidConfigType() {
        val result = MermaidPreprocessor.preprocess(
            """
                ---
                config:
                  flowchart:
                    padding: wide
                ---
                flowchart LR
                  A --> B
            """.trimIndent(),
        )

        val error = assertIs<GMResult.Err<MermaidError>>(result).error
        assertIs<MermaidError.Configuration>(error)
        assertContains(error.message, "flowchart.padding")
    }

    @Test
    fun rejectsInvalidSubgraphTitleMargin() {
        val invalidMap = MermaidPreprocessor.preprocess(
            """
                ---
                config:
                  flowchart:
                    subGraphTitleMargin: 12
                ---
                flowchart LR
                  A --> B
            """.trimIndent(),
        )
        val mapError = assertIs<GMResult.Err<MermaidError>>(invalidMap).error
        assertIs<MermaidError.Configuration>(mapError)
        assertContains(mapError.message, "flowchart.subGraphTitleMargin")

        val negative = MermaidPreprocessor.preprocess(
            """
                ---
                config:
                  flowchart:
                    subGraphTitleMargin:
                      top: -1
                ---
                flowchart LR
                  A --> B
            """.trimIndent(),
        )
        val negativeError = assertIs<GMResult.Err<MermaidError>>(negative).error
        assertIs<MermaidError.Configuration>(negativeError)
        assertContains(negativeError.message, "flowchart.subGraphTitleMargin.top")
    }

    @Test
    fun acceptsMermaidBuiltInThemeConfiguration() {
        val result = MermaidPreprocessor.preprocess(
            """
                ---
                config:
                  theme: forest
                ---
                flowchart LR
                  A --> B
            """.trimIndent(),
        )

        val processed = assertIs<GMResult.Ok<MermaidPreprocessResult>>(result).value
        val options = assertIs<GMResult.Ok<MermaidRenderOptions>>(
            processed.config.applyTo(MermaidRenderOptions()),
        ).value
        assertEquals("forest", options.themeName)
    }

    @Test
    fun preservesQuotedNullThemeButTreatsYamlNullAsAbsent() {
        val quoted = MermaidPreprocessor.preprocess(
            """
                ---
                config:
                  flowchart:
                    theme: "null"
                ---
                flowchart LR
                  A --> B
            """.trimIndent(),
        )
        val yamlNull = MermaidPreprocessor.preprocess(
            """
                ---
                config:
                  flowchart:
                    theme: null
                ---
                flowchart LR
                  A --> B
            """.trimIndent(),
        )

        val quotedOptions = assertIs<GMResult.Ok<MermaidPreprocessResult>>(quoted)
            .value
            .config
            .applyTo(MermaidRenderOptions(themeName = "forest"))
        val yamlNullOptions = assertIs<GMResult.Ok<MermaidPreprocessResult>>(yamlNull)
            .value
            .config
            .applyTo(MermaidRenderOptions(themeName = "forest"))

        assertEquals(
            "null",
            assertIs<GMResult.Ok<MermaidRenderOptions>>(quotedOptions).value.themeName,
        )
        assertEquals(
            "forest",
            assertIs<GMResult.Ok<MermaidRenderOptions>>(yamlNullOptions).value.themeName,
        )
    }

    @Test
    fun ignoresUnknownScopedAppearanceBeforeApplyingSameLayerGlobalValue() {
        val result = MermaidPreprocessor.preprocess(
            """
                ---
                config:
                  theme: dark
                  look: classic
                  flowchart:
                    theme: totally-bogus
                    look: not-a-look
                ---
                flowchart LR
                  A --> B
            """.trimIndent(),
        )

        val processed = assertIs<GMResult.Ok<MermaidPreprocessResult>>(result).value
        val options = assertIs<GMResult.Ok<MermaidRenderOptions>>(
            processed.config.applyTo(MermaidRenderOptions()),
        ).value

        assertEquals("dark", options.themeName)
        assertEquals("classic", options.look)
    }

    @Test
    fun mergesThemeVariablesAndAcceptsDomOnlyFlowchartOptions() {
        val result = MermaidPreprocessor.preprocess(
            """
                ---
                config:
                  themeVariables:
                    nodeBkg: "#112233"
                    lineColor: "#445566"
                  flowchart:
                    arrowMarkerAbsolute: true
                    useMaxWidth: false
                ---
                %%{init: {'themeVariables': {'lineColor': '#778899'}}}%%
                flowchart LR
                  A --> B
            """.trimIndent(),
        )

        val processed = assertIs<GMResult.Ok<MermaidPreprocessResult>>(result).value
        val options = assertIs<GMResult.Ok<MermaidRenderOptions>>(
            processed.config.applyTo(MermaidRenderOptions()),
        ).value

        assertEquals("#112233", options.themeVariables["nodeBkg"])
        assertEquals("#778899", options.themeVariables["lineColor"])
    }

    @Test
    fun portsFontFamilyAndPreservesThemeVariablePrecedence() {
        val result = MermaidPreprocessor.preprocess(
            """
                ---
                config:
                  fontFamily: "Arial, sans-serif"
                  themeVariables:
                    fontFamily: "Verdana, sans-serif"
                ---
                flowchart LR
                  A --> B
            """.trimIndent(),
        )

        val processed = assertIs<GMResult.Ok<MermaidPreprocessResult>>(result).value
        val options = assertIs<GMResult.Ok<MermaidRenderOptions>>(
            processed.config.applyTo(MermaidRenderOptions()),
        ).value

        assertEquals("Arial, sans-serif", options.fontFamily)
        assertEquals("Verdana, sans-serif", options.themeVariables["fontFamily"])
    }

    @Test
    fun preservesThemeColorArraysAsStructuredValues() {
        val result = MermaidPreprocessor.preprocess(
            """
                ---
                config:
                  themeVariables:
                    bkgColorArray: ["#112233", "rgb(68, 85, 102)"]
                    borderColorArray:
                      - "#778899"
                      - "#aabbcc"
                ---
                flowchart LR
                  A --> B
            """.trimIndent(),
        )

        val processed = assertIs<GMResult.Ok<MermaidPreprocessResult>>(result).value
        val options = assertIs<GMResult.Ok<MermaidRenderOptions>>(
            processed.config.applyTo(MermaidRenderOptions()),
        ).value

        assertEquals(
            listOf("#112233", "rgb(68, 85, 102)"),
            options.themeColorArrays["bkgColorArray"],
        )
        assertEquals(
            listOf("#778899", "#aabbcc"),
            options.themeColorArrays["borderColorArray"],
        )
    }

    @Test
    fun rejectsKnownConfigurationWithoutANativePort() {
        val result = MermaidPreprocessor.preprocess(
            """
                ---
                config:
                  themeCSS: ".node { filter: blur(2px); }"
                ---
                flowchart LR
                  A --> B
            """.trimIndent(),
        )

        val error = assertIs<GMResult.Err<MermaidError>>(result).error
        assertIs<MermaidError.UnsupportedFeature>(error)
        assertContains(error.message, "themeCSS")
    }
}

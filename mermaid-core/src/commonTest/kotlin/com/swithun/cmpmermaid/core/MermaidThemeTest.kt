package com.swithun.cmpmermaid.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class MermaidThemeTest {
    @Test
    fun exposesEveryMermaid12ThemePresetByTypedAndConfigNameApis() {
        MermaidThemePreset.entries.forEach { preset ->
            val typed = MermaidTheme.preset(preset)
            val named = assertIs<GMResult.Ok<MermaidTheme>>(
                MermaidTheme.fromName(preset.configName),
            ).value

            assertEquals(typed, named)
        }
    }

    @Test
    fun returnsConfigurationErrorForUnknownThemeName() {
        val result = MermaidTheme.fromName("not-a-mermaid-theme")

        assertIs<GMResult.Err<MermaidError.Configuration>>(result)
    }

    @Test
    fun matchesMermaid12NoteVariablesForEveryPreset() {
        val expected = mapOf(
            MermaidThemePreset.Default to Triple(0xFFFFF5AD, 0xFFAAAA33, 0xFF000000),
            MermaidThemePreset.Dark to Triple(0xFF474949, 0xFF2F2F2F, 0xFFB8B6B6),
            MermaidThemePreset.Forest to Triple(0xFFFFF5AD, 0xFF6EAA49, 0xFF000000),
            MermaidThemePreset.Neutral to Triple(0xFF666666, 0xFF999999, 0xFFFFFFFF),
            MermaidThemePreset.Base to Triple(0xFFFFF5AD, 0xFFE4DB95, 0xFF333333),
            MermaidThemePreset.Neo to Triple(0xFFFFF5AD, 0xFFE4DB95, 0xFF333333),
            MermaidThemePreset.NeoDark to Triple(0xFFFFF5AD, 0xFFE4DB95, 0xFF333333),
            MermaidThemePreset.Redux to Triple(0xFFFFF5AD, 0xFFFACC15, 0xFF28253D),
            MermaidThemePreset.ReduxColor to Triple(0xFFFFF5AD, 0xFFFACC15, 0xFF28253D),
            MermaidThemePreset.ReduxDark to Triple(0xFFFEF9C3, 0xFFFACC15, 0xFF28253D),
            MermaidThemePreset.ReduxDarkColor to
                Triple(0xFFFEF9C3, 0xFFFACC15, 0xFF28253D),
        )

        expected.forEach { (preset, colors) ->
            val theme = MermaidTheme.preset(preset)
            assertEquals(SceneColor(colors.first), theme.noteFill, preset.name)
            assertEquals(SceneColor(colors.second), theme.noteStroke, preset.name)
            assertEquals(SceneColor(colors.third), theme.noteText, preset.name)
        }
    }

    @Test
    fun matchesMermaid12CoreVariablesForLegacyPresets() {
        assertEquals(
            SceneColor(0xFF131300),
            MermaidTheme.preset(MermaidThemePreset.Default).nodeText,
        )
        assertEquals(
            SceneColor(0xFFE0DFDF),
            MermaidTheme.preset(MermaidThemePreset.Dark).nodeText,
        )
        assertEquals(
            SceneColor(0xFF321B67),
            MermaidTheme.preset(MermaidThemePreset.Forest).nodeText,
        )
        with(MermaidTheme.preset(MermaidThemePreset.Base)) {
            assertEquals(SceneColor(0xFFEEDEBB), nodeStroke)
            assertEquals(SceneColor(0xFFF4DDFF), edgeLabelFill)
            assertEquals(SceneColor(0xFFF7F9FF), groupFill)
            assertEquals(SceneColor(0xFFCFDBF3), groupStroke)
            assertEquals(SceneColor(0xFF090600), groupText)
        }
        with(MermaidTheme.preset(MermaidThemePreset.ReduxColor).gantt) {
            assertEquals(SceneColor(0xFFF4A8FF), sectionBackground)
            assertEquals(SceneColor(0xFF46ECD5), secondSectionBackground)
            assertEquals(SceneColor(0xFFECECFE), taskFill)
        }
        with(MermaidTheme.preset(MermaidThemePreset.ReduxDarkColor).gantt) {
            assertEquals(SceneColor(0xFF333333), alternateSectionBackground)
            assertEquals(SceneColor(0xFF1F2020), taskFill)
            assertEquals(SceneColor(0xFF38383E), doneTaskFill)
        }
    }

    @Test
    fun appliesBrandVariablesWithoutMutatingTheBasePreset() {
        val base = MermaidTheme.preset(MermaidThemePreset.ReduxColor)
        val result = MermaidTheme.withVariables(
            theme = base,
            values = mapOf(
                "background" to "#101820",
                "mainBkg" to "#f2aa4c",
                "nodeTextColor" to "#101820",
                "defaultLinkColor" to "#f2aa4c",
                "noteBkgColor" to "#ffffff",
                "noteBorderColor" to "#f2aa4c",
                "noteTextColor" to "#101820",
                "taskBkgColor" to "#334455",
                "sectionBkgColor" to "#667788",
                "fontSize" to "18px",
            ),
            colorArrays = mapOf(
                "bkgColorArray" to listOf("#f2aa4c", "#ffffff"),
                "borderColorArray" to listOf("#101820", "#f2aa4c"),
            ),
        )

        val customized = assertIs<GMResult.Ok<MermaidTheme>>(result).value
        assertEquals(SceneColor(0xFF101820), customized.background)
        assertEquals(SceneColor(0xFFF2AA4C), customized.nodeFill)
        assertEquals(SceneColor(0xFF101820), customized.nodeText)
        assertEquals(SceneColor(0xFFF2AA4C), customized.edge)
        assertEquals(SceneColor(0xFFFFFFFF), customized.noteFill)
        assertEquals(SceneColor(0xFFF2AA4C), customized.noteStroke)
        assertEquals(SceneColor(0xFF101820), customized.noteText)
        assertEquals(SceneColor(0xFF334455), customized.gantt.taskFill)
        assertEquals(SceneColor(0xFF667788), customized.gantt.sectionBackground)
        assertEquals(18f, customized.fontSize)
        assertEquals(
            listOf(SceneColor(0xFFF2AA4C), SceneColor(0xFFFFFFFF)),
            customized.bkgColorArray,
        )
        assertEquals(MermaidTheme.FlowchartDefault, base)
    }

    @Test
    fun appliesOnlyJourneyVariablesAcceptedByMermaid12DirectiveSanitizer() {
        val base = MermaidTheme.preset(MermaidThemePreset.Default)
        val customized = assertIs<GMResult.Ok<MermaidTheme>>(
            MermaidTheme.withVariables(
                theme = base,
                values = mapOf(
                    "fillType0" to "#112233",
                    "textColor" to "#445566",
                    "actor0" to "#778899",
                    "faceColor" to "#aabbcc",
                ),
            ),
        ).value

        assertEquals(SceneColor(0xFF112233), customized.journey.sectionFills.first())
        assertEquals(SceneColor(0xFF445566), customized.journey.textColor)
        assertEquals(base.journey.actorColors, customized.journey.actorColors)
        assertEquals(base.journey.faceColor, customized.journey.faceColor)
    }
}

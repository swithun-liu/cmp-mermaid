package com.swithun.cmpmermaid.core

import com.swithun.cmpmermaid.core.agentflow.upstream.mermaid.AgentflowJisonTables
import com.swithun.cmpmermaid.core.block.upstream.mermaid.BlockJisonCell
import com.swithun.cmpmermaid.core.block.upstream.mermaid.BlockJisonTables
import com.swithun.cmpmermaid.core.classdiagram.upstream.mermaid.ClassJisonCell
import com.swithun.cmpmermaid.core.classdiagram.upstream.mermaid.ClassJisonTables
import com.swithun.cmpmermaid.core.erdiagram.upstream.mermaid.ErJisonCell
import com.swithun.cmpmermaid.core.erdiagram.upstream.mermaid.ErJisonTables
import com.swithun.cmpmermaid.core.flowchart.upstream.mermaid.FlowJisonTables
import com.swithun.cmpmermaid.core.flowchart.upstream.mermaid.JisonCell as FlowCell
import com.swithun.cmpmermaid.core.gantt.upstream.mermaid.GanttJisonCell
import com.swithun.cmpmermaid.core.gantt.upstream.mermaid.GanttJisonTables
import com.swithun.cmpmermaid.core.journey.upstream.mermaid.JourneyJisonCell
import com.swithun.cmpmermaid.core.journey.upstream.mermaid.JourneyJisonTables
import com.swithun.cmpmermaid.core.mindmap.upstream.mermaid.MindmapJisonCell
import com.swithun.cmpmermaid.core.mindmap.upstream.mermaid.MindmapJisonTables
import com.swithun.cmpmermaid.core.requirement.upstream.mermaid.RequirementJisonCell
import com.swithun.cmpmermaid.core.requirement.upstream.mermaid.RequirementJisonTables
import com.swithun.cmpmermaid.core.sequence.upstream.mermaid.SequenceJisonCell
import com.swithun.cmpmermaid.core.sequence.upstream.mermaid.SequenceJisonTables
import com.swithun.cmpmermaid.core.statediagram.upstream.mermaid.StateJisonCell
import com.swithun.cmpmermaid.core.statediagram.upstream.mermaid.StateJisonTables
import com.swithun.cmpmermaid.core.xychart.upstream.mermaid.XyJisonCell
import com.swithun.cmpmermaid.core.xychart.upstream.mermaid.XyJisonTables
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Independent expected rows come from official Mermaid 12.0.0 JavaScript
 * objects, including numeric key order, not from the compact Kotlin emitter.
 */
class JisonTableOfficialParityTest {
    @Test
    fun agentflow() = verify("agentflow", AgentflowJisonTables.states) {
        when (it) {
            is FlowCell.Goto -> "g:${it.state}"
            is FlowCell.Shift -> "s:${it.state}"
            is FlowCell.Reduce -> "r:${it.production}"
            FlowCell.Accept -> "a:0"
        }
    }

    @Test
    fun block() = verify("block", BlockJisonTables.states) {
        when (it) {
            is BlockJisonCell.Goto -> "g:${it.state}"
            is BlockJisonCell.Shift -> "s:${it.state}"
            is BlockJisonCell.Reduce -> "r:${it.production}"
            BlockJisonCell.Accept -> "a:0"
        }
    }

    @Test
    fun classDiagram() = verify("class", ClassJisonTables.states) {
        when (it) {
            is ClassJisonCell.Goto -> "g:${it.state}"
            is ClassJisonCell.Shift -> "s:${it.state}"
            is ClassJisonCell.Reduce -> "r:${it.production}"
            ClassJisonCell.Accept -> "a:0"
        }
    }

    @Test
    fun er() = verify("er", ErJisonTables.states) {
        when (it) {
            is ErJisonCell.Goto -> "g:${it.state}"
            is ErJisonCell.Shift -> "s:${it.state}"
            is ErJisonCell.Reduce -> "r:${it.production}"
            ErJisonCell.Accept -> "a:0"
        }
    }

    @Test
    fun flow() = verify("flow", FlowJisonTables.states) {
        when (it) {
            is FlowCell.Goto -> "g:${it.state}"
            is FlowCell.Shift -> "s:${it.state}"
            is FlowCell.Reduce -> "r:${it.production}"
            FlowCell.Accept -> "a:0"
        }
    }

    @Test
    fun gantt() = verify("gantt", GanttJisonTables.states) {
        when (it) {
            is GanttJisonCell.Goto -> "g:${it.state}"
            is GanttJisonCell.Shift -> "s:${it.state}"
            is GanttJisonCell.Reduce -> "r:${it.production}"
            GanttJisonCell.Accept -> "a:0"
        }
    }

    @Test
    fun journey() = verify("journey", JourneyJisonTables.states) {
        when (it) {
            is JourneyJisonCell.Goto -> "g:${it.state}"
            is JourneyJisonCell.Shift -> "s:${it.state}"
            is JourneyJisonCell.Reduce -> "r:${it.production}"
            JourneyJisonCell.Accept -> "a:0"
        }
    }

    @Test
    fun mindmap() = verify("mindmap", MindmapJisonTables.states) {
        when (it) {
            is MindmapJisonCell.Goto -> "g:${it.state}"
            is MindmapJisonCell.Shift -> "s:${it.state}"
            is MindmapJisonCell.Reduce -> "r:${it.production}"
            MindmapJisonCell.Accept -> "a:0"
        }
    }

    @Test
    fun requirement() = verify("requirement", RequirementJisonTables.states) {
        when (it) {
            is RequirementJisonCell.Goto -> "g:${it.state}"
            is RequirementJisonCell.Shift -> "s:${it.state}"
            is RequirementJisonCell.Reduce -> "r:${it.production}"
            RequirementJisonCell.Accept -> "a:0"
        }
    }

    @Test
    fun sequence() = verify("sequence", SequenceJisonTables.states) {
        when (it) {
            is SequenceJisonCell.Goto -> "g:${it.state}"
            is SequenceJisonCell.Shift -> "s:${it.state}"
            is SequenceJisonCell.Reduce -> "r:${it.production}"
            SequenceJisonCell.Accept -> "a:0"
        }
    }

    @Test
    fun state() = verify("state", StateJisonTables.states) {
        when (it) {
            is StateJisonCell.Goto -> "g:${it.state}"
            is StateJisonCell.Shift -> "s:${it.state}"
            is StateJisonCell.Reduce -> "r:${it.production}"
            StateJisonCell.Accept -> "a:0"
        }
    }

    @Test
    fun xyChart() = verify("xychart", XyJisonTables.states) {
        when (it) {
            is XyJisonCell.Goto -> "g:${it.state}"
            is XyJisonCell.Shift -> "s:${it.state}"
            is XyJisonCell.Reduce -> "r:${it.production}"
            XyJisonCell.Accept -> "a:0"
        }
    }

    private fun <T> verify(family: String, states: Array<Map<Int, T>>, encode: (T) -> String) {
        val stream = javaClass.getResourceAsStream("/jison-tables/$family.rows")
        assertNotNull(stream, "Missing independent official oracle for $family")
        val expected = stream.bufferedReader().use { it.readLines() }
        assertEquals(expected.size, states.size, "$family state count")
        states.forEachIndexed { index, row ->
            val actual = row.entries.joinToString(";") { (symbol, cell) -> "$symbol:${encode(cell)}" }
            assertEquals(expected[index], actual, "$family state $index: keys, order and actions")
        }
    }
}

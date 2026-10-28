package com.swithun.cmpmermaid.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class JisonTableConstructionTest {
    @Test
    fun emptyAndSingleRowsRetainTheirEntries() {
        assertEquals(emptyMap(), buildJisonState(intArrayOf(), "reduce"))
        assertEquals(mapOf(3 to "accept"), buildJisonState(intArrayOf(3), "accept"))
    }

    @Test
    fun repeatedRowsShareImmutableCellsAndDoNotMutateInputs() {
        val symbols = intArrayOf(2, 7, 11)
        val cell = Pair(2, 15)
        val row = buildJisonState(symbols, cell)
        assertEquals(listOf(2, 7, 11), row.keys.toList())
        for (value in row.values) assertSame(cell, value)
        assertEquals(listOf(2, 7, 11), symbols.toList())
    }

    @Test
    fun overridesRetainNumericPropertyOrderAndMissingEntries() {
        val overrides = mapOf(19 to "goto", 3 to "shift")
        val row = buildJisonState(intArrayOf(2, 3, 11, 19), "reduce", overrides)
        assertEquals(listOf(2, 3, 11, 19), row.keys.toList())
        assertEquals(
            mapOf(2 to "reduce", 3 to "shift", 11 to "reduce", 19 to "goto"),
            row,
        )
        assertEquals(mapOf(19 to "goto", 3 to "shift"), overrides)
        assertEquals(null, row[7])
    }
}

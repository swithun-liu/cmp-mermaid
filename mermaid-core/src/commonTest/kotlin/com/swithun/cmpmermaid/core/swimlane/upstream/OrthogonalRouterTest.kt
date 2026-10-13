package com.swithun.cmpmermaid.core.swimlane.upstream

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class OrthogonalRouterTest {
    @Test
    fun pointKeyMatchesJavaScriptToFixedOneRounding() {
        assertEquals("0.0,0.0", orthogonalRouterPointKey(SwimlanePoint(0.0, -0.0)))
        assertEquals("0.0,0.1", orthogonalRouterPointKey(SwimlanePoint(0.04, 0.05)))
        assertEquals("0.1,0.2", orthogonalRouterPointKey(SwimlanePoint(0.11, 0.19)))
        assertEquals("1.1,1.3", orthogonalRouterPointKey(SwimlanePoint(1.15, 1.25)))
        assertEquals("-1.1,-1.3", orthogonalRouterPointKey(SwimlanePoint(-1.15, -1.25)))
    }

    @Test
    fun pointKeyDoesNotCollapseCoordinatesThatRoundToDifferentTenths() {
        assertNotEquals(
            orthogonalRouterPointKey(SwimlanePoint(0.11, 0.11)),
            orthogonalRouterPointKey(SwimlanePoint(0.19, 0.19)),
        )
    }
}

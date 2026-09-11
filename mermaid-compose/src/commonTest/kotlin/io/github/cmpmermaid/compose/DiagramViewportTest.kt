package io.github.cmpmermaid.compose

import kotlin.test.Test
import kotlin.test.assertEquals

class DiagramViewportTest {
    @Test
    fun zoomAroundCenterKeepsContentCentered() {
        val viewport = DiagramViewport().applyGesture(
            viewportWidth = 300f,
            viewportHeight = 200f,
            fittedContentWidth = 300f,
            fittedContentHeight = 120f,
            centroidX = 150f,
            centroidY = 100f,
            gesturePanX = 0f,
            gesturePanY = 0f,
            zoomChange = 2f,
        )

        assertEquals(2f, viewport.zoom)
        assertEquals(0f, viewport.panX)
        assertEquals(0f, viewport.panY)
    }

    @Test
    fun zoomAroundRightSideKeepsTouchedContentUnderCentroid() {
        val viewport = DiagramViewport().applyGesture(
            viewportWidth = 300f,
            viewportHeight = 200f,
            fittedContentWidth = 300f,
            fittedContentHeight = 120f,
            centroidX = 250f,
            centroidY = 100f,
            gesturePanX = 0f,
            gesturePanY = 0f,
            zoomChange = 2f,
        )

        assertEquals(-100f, viewport.panX)
    }

    @Test
    fun panCannotMoveContentBeyondViewport() {
        val viewport = DiagramViewport(zoom = 2f).applyGesture(
            viewportWidth = 300f,
            viewportHeight = 200f,
            fittedContentWidth = 300f,
            fittedContentHeight = 120f,
            centroidX = 150f,
            centroidY = 100f,
            gesturePanX = 1_000f,
            gesturePanY = -1_000f,
            zoomChange = 1f,
        )

        assertEquals(150f, viewport.panX)
        assertEquals(-20f, viewport.panY)
    }

    @Test
    fun zoomingBackToFitResetsPan() {
        val viewport = DiagramViewport(
            zoom = 2f,
            panX = 80f,
            panY = -20f,
        ).applyGesture(
            viewportWidth = 300f,
            viewportHeight = 200f,
            fittedContentWidth = 300f,
            fittedContentHeight = 120f,
            centroidX = 150f,
            centroidY = 100f,
            gesturePanX = 0f,
            gesturePanY = 0f,
            zoomChange = 0.5f,
        )

        assertEquals(DiagramViewport(), viewport)
    }
}

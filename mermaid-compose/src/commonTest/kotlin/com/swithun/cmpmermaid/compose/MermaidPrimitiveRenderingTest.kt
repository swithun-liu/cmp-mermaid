package com.swithun.cmpmermaid.compose

import androidx.compose.ui.text.style.LineHeightStyle
import com.swithun.cmpmermaid.core.SceneShapeKind
import kotlin.test.Test
import kotlin.test.assertEquals

class MermaidPrimitiveRenderingTest {
    @Test
    fun dispatchesCircularShapesToOvalWithoutChangingExistingPrimitiveKinds() {
        assertEquals(
            PrimitiveCanvasShape.Oval,
            SceneShapeKind.Circle.primitiveCanvasShape(),
        )
        assertEquals(
            PrimitiveCanvasShape.Oval,
            SceneShapeKind.Ellipse.primitiveCanvasShape(),
        )
        assertEquals(
            PrimitiveCanvasShape.RoundedRectangle,
            SceneShapeKind.RoundedRectangle.primitiveCanvasShape(),
        )
        assertEquals(
            PrimitiveCanvasShape.Rectangle,
            SceneShapeKind.Rectangle.primitiveCanvasShape(),
        )
    }

    @Test
    fun preservesExplicitLinesWhenSoftWrappingIsDisabled() {
        assertEquals(1, mermaidTextMaxLines("main", softWrap = false))
        assertEquals(3, mermaidTextMaxLines("release\ncandidate\nready", softWrap = false))
        assertEquals(8, mermaidTextMaxLines("release\ncandidate", softWrap = true))
        assertEquals(
            4,
            mermaidTextMaxLines(
                text = "release candidate",
                softWrap = true,
                explicitMaxLines = 4,
            ),
        )
    }

    @Test
    fun preservesDefaultTextScaleWhileAllowingSvgTextOverride() {
        assertEquals(1.06f, mermaidTextHorizontalScale(null))
        assertEquals(1f, mermaidTextHorizontalScale(1f))
    }

    @Test
    fun preservesCssLeadingOnlyForHtmlTextLineHeight() {
        assertEquals(
            LineHeightStyle(
                alignment = LineHeightStyle.Alignment.Center,
                trim = LineHeightStyle.Trim.None,
            ),
            mermaidLineHeightStyle(1.5f),
        )
        assertEquals(null, mermaidLineHeightStyle(1.2f))
        assertEquals(null, mermaidLineHeightStyle(1.1f))
    }

    @Test
    fun convertsExplicitHorizontalScaleIntoLayoutWidth() {
        assertEquals(
            209,
            205f.mermaidTextConstraint(
                spans = emptyList(),
                horizontalScale = 0.98f,
            ),
        )
        assertEquals(
            205,
            205f.mermaidTextConstraint(spans = emptyList()),
        )
    }

    @Test
    fun matchesBrowserHtmlLabelBoundaryWrapping() {
        assertEquals(true, htmlLabelFitsWithoutWrapping(119.04785f, 120f))
        assertEquals(false, htmlLabelFitsWithoutWrapping(120f, 120f))
        assertEquals(false, htmlLabelFitsWithoutWrapping(121.41992f, 120f))
    }

    @Test
    fun preservesSubpixelMaximumWidthBeforeComposeConstraintRounding() {
        assertEquals(
            186.56717f,
            175f.mermaidTextMaximumWidth(
                spans = emptyList(),
                horizontalScale = 0.938f,
            ),
            0.0001f,
        )
    }

    @Test
    fun keepsHtmlLabelLayoutAtItsCssVisualWidth() {
        assertEquals(
            175f,
            175f.mermaidTextLayoutWidth(
                spans = emptyList(),
                horizontalScale = 0.98f,
                htmlLabel = true,
            ),
        )
        assertEquals(
            178.57143f,
            175f.mermaidTextLayoutWidth(
                spans = emptyList(),
                horizontalScale = 0.98f,
                htmlLabel = false,
            ),
            0.0001f,
        )
    }

    @Test
    fun appliesExplicitHorizontalScaleToHtmlIntrinsicWidth() {
        assertEquals(
            98f,
            100f.mermaidTextIntrinsicWidth(0.98f),
            0.0001f,
        )
        assertEquals(
            178.76553f,
            178.76553f.mermaidTextIntrinsicWidth(null),
        )
    }
}

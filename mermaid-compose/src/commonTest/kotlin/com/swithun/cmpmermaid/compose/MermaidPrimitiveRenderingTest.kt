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
}

package com.swithun.cmpmermaid.core.architecture.upstream.layoutbase

import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sqrt

/**
 * layout-base 2.0.1:
 * src/util/PointD.js -> PointD
 * src/util/DimensionD.js -> DimensionD
 */
internal data class LPointD(
    var x: Double = 0.0,
    var y: Double = 0.0,
) {
    fun getDifference(point: LPointD): LDimensionD =
        LDimensionD(x - point.x, y - point.y)

    fun getCopy(): LPointD = copy()

    fun translate(dimension: LDimensionD): LPointD {
        x += dimension.width
        y += dimension.height
        return this
    }
}
internal data class LDimensionD(
    var width: Double = 0.0,
    var height: Double = 0.0,
)

/**
 * layout-base 2.0.1: src/util/RectangleD.js -> RectangleD.
 */
internal data class LRectangleD(
    var x: Double = 0.0,
    var y: Double = 0.0,
    var width: Double = 0.0,
    var height: Double = 0.0,
) {
    val right: Double get() = x + width
    val bottom: Double get() = y + height
    val centerX: Double get() = x + width / 2.0
    val centerY: Double get() = y + height / 2.0
    val widthHalf: Double get() = width / 2.0
    val heightHalf: Double get() = height / 2.0

    fun intersects(other: LRectangleD): Boolean =
        right >= other.x &&
            bottom >= other.y &&
            other.right >= x &&
            other.bottom >= y

    fun setCenter(
        centerX: Double,
        centerY: Double,
    ) {
        x = centerX - width / 2.0
        y = centerY - height / 2.0
    }
}

/**
 * layout-base 2.0.1: src/util/IMath.js -> sign.
 */
internal fun layoutBaseSign(value: Double): Double = when {
    value > 0.0 -> 1.0
    value < 0.0 -> -1.0
    else -> 0.0
}

internal data class LRectangleIntersection(
    val overlaps: Boolean,
    val firstX: Double,
    val firstY: Double,
    val secondX: Double,
    val secondY: Double,
)

/**
 * layout-base 2.0.1: src/util/IGeometry.js.
 */
internal object LGeometry {
    /**
     * IGeometry.calcSeparationAmount and decideDirectionsForOverlappingNodes.
     */
    fun calcSeparationAmount(
        rectA: LRectangleD,
        rectB: LRectangleD,
        separationBuffer: Double,
    ): LPointD {
        var overlapX = min(rectA.right, rectB.right) - maxOf(rectA.x, rectB.x)
        var overlapY = min(rectA.bottom, rectB.bottom) - maxOf(rectA.y, rectB.y)

        if (rectA.x <= rectB.x && rectA.right >= rectB.right) {
            overlapX += min(rectB.x - rectA.x, rectA.right - rectB.right)
        } else if (rectB.x <= rectA.x && rectB.right >= rectA.right) {
            overlapX += min(rectA.x - rectB.x, rectB.right - rectA.right)
        }
        if (rectA.y <= rectB.y && rectA.bottom >= rectB.bottom) {
            overlapY += min(rectB.y - rectA.y, rectA.bottom - rectB.bottom)
        } else if (rectB.y <= rectA.y && rectB.bottom >= rectA.bottom) {
            overlapY += min(rectA.y - rectB.y, rectB.bottom - rectA.bottom)
        }

        val directionX = if (rectA.centerX < rectB.centerX) -1.0 else 1.0
        val directionY = if (rectA.centerY < rectB.centerY) -1.0 else 1.0
        var slope = abs((rectB.centerY - rectA.centerY) / (rectB.centerX - rectA.centerX))
        if (rectB.centerY == rectA.centerY && rectB.centerX == rectA.centerX) {
            slope = 1.0
        }

        var moveByY = slope * overlapX
        var moveByX = overlapY / slope
        if (overlapX < moveByX) {
            moveByX = overlapX
        } else {
            moveByY = overlapY
        }
        return LPointD(
            x = -directionX * (moveByX / 2.0 + separationBuffer),
            y = -directionY * (moveByY / 2.0 + separationBuffer),
        )
    }

    /**
     * IGeometry.getIntersection2, exposed by the overloaded
     * IGeometry.getIntersection(rectA, rectB, result) entry point.
     */
    fun getIntersection(
        rectA: LRectangleD,
        rectB: LRectangleD,
    ): LRectangleIntersection {
        val centerAX = rectA.centerX
        val centerAY = rectA.centerY
        val centerBX = rectB.centerX
        val centerBY = rectB.centerY
        if (rectA.intersects(rectB)) {
            return LRectangleIntersection(
                overlaps = true,
                firstX = centerAX,
                firstY = centerAY,
                secondX = centerBX,
                secondY = centerBY,
            )
        }

        if (centerAX == centerBX) {
            return if (centerAY > centerBY) {
                LRectangleIntersection(false, centerAX, rectA.y, centerBX, rectB.bottom)
            } else {
                LRectangleIntersection(false, centerAX, rectA.bottom, centerBX, rectB.y)
            }
        }
        if (centerAY == centerBY) {
            return if (centerAX > centerBX) {
                LRectangleIntersection(false, rectA.x, centerAY, rectB.right, centerBY)
            } else {
                LRectangleIntersection(false, rectA.right, centerAY, rectB.x, centerBY)
            }
        }

        val slopeA = rectA.height / rectA.width
        val slopeB = rectB.height / rectB.width
        val slopePrime = (centerBY - centerAY) / (centerBX - centerAX)
        var clipAX = 0.0
        var clipAY = 0.0
        var clipBX = 0.0
        var clipBY = 0.0
        var clipAFound = false
        var clipBFound = false

        if (-slopeA == slopePrime) {
            if (centerAX > centerBX) {
                clipAX = rectA.x
                clipAY = rectA.bottom
            } else {
                clipAX = rectA.right
                clipAY = rectA.y
            }
            clipAFound = true
        } else if (slopeA == slopePrime) {
            if (centerAX > centerBX) {
                clipAX = rectA.x
                clipAY = rectA.y
            } else {
                clipAX = rectA.right
                clipAY = rectA.bottom
            }
            clipAFound = true
        }

        if (-slopeB == slopePrime) {
            if (centerBX > centerAX) {
                clipBX = rectB.x
                clipBY = rectB.bottom
            } else {
                clipBX = rectB.right
                clipBY = rectB.y
            }
            clipBFound = true
        } else if (slopeB == slopePrime) {
            if (centerBX > centerAX) {
                clipBX = rectB.x
                clipBY = rectB.y
            } else {
                clipBX = rectB.right
                clipBY = rectB.bottom
            }
            clipBFound = true
        }

        val cardinalDirectionA: Int
        val cardinalDirectionB: Int
        if (centerAX > centerBX) {
            if (centerAY > centerBY) {
                cardinalDirectionA = getCardinalDirection(slopeA, slopePrime, 4)
                cardinalDirectionB = getCardinalDirection(slopeB, slopePrime, 2)
            } else {
                cardinalDirectionA = getCardinalDirection(-slopeA, slopePrime, 3)
                cardinalDirectionB = getCardinalDirection(-slopeB, slopePrime, 1)
            }
        } else if (centerAY > centerBY) {
            cardinalDirectionA = getCardinalDirection(-slopeA, slopePrime, 1)
            cardinalDirectionB = getCardinalDirection(-slopeB, slopePrime, 3)
        } else {
            cardinalDirectionA = getCardinalDirection(slopeA, slopePrime, 2)
            cardinalDirectionB = getCardinalDirection(slopeB, slopePrime, 4)
        }

        if (!clipAFound) {
            when (cardinalDirectionA) {
                1 -> {
                    clipAX = centerAX + (-rectA.heightHalf) / slopePrime
                    clipAY = rectA.y
                }
                2 -> {
                    clipAX = rectA.right
                    clipAY = centerAY + rectA.widthHalf * slopePrime
                }
                3 -> {
                    clipAX = centerAX + rectA.heightHalf / slopePrime
                    clipAY = rectA.bottom
                }
                else -> {
                    clipAX = rectA.x
                    clipAY = centerAY + (-rectA.widthHalf) * slopePrime
                }
            }
        }
        if (!clipBFound) {
            when (cardinalDirectionB) {
                1 -> {
                    clipBX = centerBX + (-rectB.heightHalf) / slopePrime
                    clipBY = rectB.y
                }
                2 -> {
                    clipBX = rectB.right
                    clipBY = centerBY + rectB.widthHalf * slopePrime
                }
                3 -> {
                    clipBX = centerBX + rectB.heightHalf / slopePrime
                    clipBY = rectB.bottom
                }
                else -> {
                    clipBX = rectB.x
                    clipBY = centerBY + (-rectB.widthHalf) * slopePrime
                }
            }
        }

        return LRectangleIntersection(
            overlaps = false,
            firstX = clipAX,
            firstY = clipAY,
            secondX = clipBX,
            secondY = clipBY,
        )
    }

    private fun getCardinalDirection(
        slope: Double,
        slopePrime: Double,
        line: Int,
    ): Int = if (slope > slopePrime) line else 1 + line % 4
}

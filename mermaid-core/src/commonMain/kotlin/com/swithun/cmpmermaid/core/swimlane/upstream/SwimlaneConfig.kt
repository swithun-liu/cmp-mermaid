package com.swithun.cmpmermaid.core.swimlane.upstream

// Mermaid.js 12.0.0: swimlanes/config.ts.
internal object SwimlaneConfig {
    object EdgeRouting {
        const val EdgeGap: Double = 12.0
        const val LaneMargin: Double = 20.0
    }

    object Precision {
        const val Epsilon: Double = 1e-6
    }

    object Layering {
        const val GravityIterations: Int = 8
        const val MaximumCrossingOptimizationPasses: Int = 4
        const val DefaultCompactSingleInput: Boolean = true
    }

    object Coordinates {
        const val DefaultLayerGap: Double = 100.0
        const val DefaultNodeGap: Double = 40.0
    }
}

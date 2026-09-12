package io.github.cmpmermaid.core.gantt

import io.github.cmpmermaid.core.CssColorParser
import io.github.cmpmermaid.core.GMResult
import io.github.cmpmermaid.core.MermaidError
import io.github.cmpmermaid.core.MermaidPreprocessor
import io.github.cmpmermaid.core.MermaidRenderContext
import io.github.cmpmermaid.core.MermaidScene
import io.github.cmpmermaid.core.SceneArrowHead
import io.github.cmpmermaid.core.SceneColor
import io.github.cmpmermaid.core.SceneElement
import io.github.cmpmermaid.core.SceneNodeInteraction
import io.github.cmpmermaid.core.ScenePath
import io.github.cmpmermaid.core.ScenePathCommand
import io.github.cmpmermaid.core.ScenePoint
import io.github.cmpmermaid.core.SceneRect
import io.github.cmpmermaid.core.SceneShape
import io.github.cmpmermaid.core.SceneShapeGeometry
import io.github.cmpmermaid.core.SceneShapeKind
import io.github.cmpmermaid.core.SceneShapePaint
import io.github.cmpmermaid.core.SceneShapePath
import io.github.cmpmermaid.core.SceneStrokePattern
import io.github.cmpmermaid.core.SceneText
import io.github.cmpmermaid.core.SceneTextAlignment
import io.github.cmpmermaid.core.SceneTextSpan
import io.github.cmpmermaid.core.SceneTextWeight
import io.github.cmpmermaid.core.TextMetrics
import io.github.cmpmermaid.core.TextMetricsRequest
import io.github.cmpmermaid.core.gantt.upstream.mermaid.GanttCompiledDocument
import io.github.cmpmermaid.core.gantt.upstream.mermaid.GanttDatePort
import io.github.cmpmermaid.core.gantt.upstream.mermaid.GanttDb
import io.github.cmpmermaid.core.gantt.upstream.mermaid.GanttTask
import io.github.cmpmermaid.core.gantt.upstream.mermaid.GanttWeekday
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.time.Clock

/**
 * Native translation of Mermaid 12.0.0's ganttRenderer.js.
 */
internal class GanttLayout {
    fun layout(
        db: GanttDb,
        context: MermaidRenderContext,
    ): GMResult<MermaidScene, MermaidError> {
        val document = when (val compiled = db.compile()) {
            is GMResult.Ok -> compiled.value
            is GMResult.Err -> return compiled
        }
        if (document.tasks.size > context.options.maxEdges) {
            return GMResult.Err(
                MermaidError.ResourceLimit(
                    resource = "Gantt tasks",
                    actual = document.tasks.size,
                    maximum = context.options.maxEdges,
                ),
            )
        }
        val palette = when (val parsed = GanttPalette.from(context)) {
            is GMResult.Ok -> parsed.value
            is GMResult.Err -> return parsed
        }
        val taskLabels = linkedMapOf<String, MeasuredText>()
        document.tasks.forEach { task ->
            val measured = when (
                val result = measure(
                    text = decode(task.description),
                    fontSize = context.options.ganttFontSize,
                    context = context,
                )
            ) {
                is GMResult.Ok -> result.value
                is GMResult.Err -> return result
            }
            taskLabels[task.id] = measured
        }
        val sectionLabels = linkedMapOf<String, MeasuredText>()
        document.tasks.map(GanttTask::section).distinct().forEach { section ->
            val measured = when (
                val result = measure(
                    text = decode(section).replace(BREAK, "\n"),
                    fontSize = context.options.ganttSectionFontSize,
                    context = context,
                )
            ) {
                is GMResult.Ok -> result.value
                is GMResult.Err -> return result
            }
            sectionLabels[section] = measured
        }
        val title = document.title?.takeIf(String::isNotBlank)?.let { source ->
            when (
                val result = measure(
                    text = decode(source),
                    fontSize = context.theme.fontSize,
                    context = context,
                    weight = SceneTextWeight.Bold,
                )
            ) {
                is GMResult.Ok -> result.value
                is GMResult.Err -> return result
            }
        }
        return buildScene(
            document = document,
            taskLabels = taskLabels,
            sectionLabels = sectionLabels,
            title = title,
            palette = palette,
            context = context,
        )
    }

    private fun buildScene(
        document: GanttCompiledDocument,
        taskLabels: Map<String, MeasuredText>,
        sectionLabels: Map<String, MeasuredText>,
        title: MeasuredText?,
        palette: GanttPalette,
        context: MermaidRenderContext,
    ): GMResult<MermaidScene, MermaidError> {
        val tasks = document.tasks.map { task -> task.copy() }
        val regularTasks = tasks.filterNot { task -> task.flags.vertical }
        val categories = regularTasks.map(GanttTask::section).distinct()
        val categoryHeights = assignRows(
            tasks = regularTasks,
            categories = categories,
            compact = context.options.ganttDisplayMode == "compact",
        )
        val rowCount = if (context.options.ganttDisplayMode == "compact") {
            categoryHeights.values.sum()
        } else {
            regularTasks.size
        }
        val width = context.options.ganttUseWidth
        val gap = context.options.ganttBarHeight + context.options.ganttBarGap
        val height = max(
            context.options.ganttTopPadding * 2f + rowCount * gap,
            MIN_HEIGHT,
        )
        if (width <= context.options.ganttLeftPadding + context.options.ganttRightPadding) {
            return GMResult.Err(
                MermaidError.Configuration(
                    "Gantt useWidth must exceed leftPadding + rightPadding",
                ),
            )
        }
        val minimum = tasks.minOfOrNull(GanttTask::startMillis) ?: 0L
        val maximum = tasks.maxOfOrNull(GanttTask::endMillis) ?: minimum + MILLIS_PER_DAY
        val scale = TimeScale(
            minimum = minimum,
            maximum = maximum,
            start = context.options.ganttLeftPadding,
            end = width - context.options.ganttRightPadding,
        )
        val elements = mutableListOf<SceneElement>()

        addSectionBands(
            tasks = regularTasks,
            categories = categories,
            width = width,
            rightPadding = context.options.ganttRightPadding,
            gap = gap,
            height = context.options.ganttBarHeight,
            top = context.options.ganttTopPadding,
            styleCount = context.options.ganttNumberSectionStyles.coerceAtLeast(1),
            palette = palette,
            elements = elements,
        )
        when (
            val excluded = addExcludedRanges(
                document = document,
                scale = scale,
                height = height,
                context = context,
                palette = palette,
                elements = elements,
            )
        ) {
            is GMResult.Ok -> Unit
            is GMResult.Err -> return excluded
        }
        addGrid(
            document = document,
            scale = scale,
            height = height,
            context = context,
            palette = palette,
            elements = elements,
        )
        addTasks(
            tasks = tasks.sortedBy(GanttTask::startMillis)
                .sortedBy { task -> task.flags.vertical },
            regularTaskCount = regularTasks.size,
            taskLabels = taskLabels,
            categories = categories,
            scale = scale,
            width = width,
            gap = gap,
            height = height,
            context = context,
            palette = palette,
            elements = elements,
        )
        addSectionLabels(
            categories = categories,
            categoryHeights = categoryHeights,
            labels = sectionLabels,
            gap = gap,
            context = context,
            palette = palette,
            elements = elements,
        )
        when (
            val today = addTodayMarker(
                document = document,
                scale = scale,
                height = height,
                context = context,
                palette = palette,
                elements = elements,
            )
        ) {
            is GMResult.Ok -> Unit
            is GMResult.Err -> return today
        }
        title?.let { measured ->
            elements += SceneText(
                text = measured.text,
                bounds = SceneRect(
                    left = width / 2f - measured.metrics.width / 2f,
                    top = context.options.ganttTitleTopMargin -
                        measured.metrics.height / 2f,
                    right = width / 2f + measured.metrics.width / 2f,
                    bottom = context.options.ganttTitleTopMargin +
                        measured.metrics.height / 2f,
                ),
                color = palette.title,
                fontSize = measured.fontSize,
                lineHeight = DEFAULT_LINE_HEIGHT,
                fontFamily = context.options.fontFamily ?: context.theme.fontFamily,
                weight = SceneTextWeight.Bold,
                zIndex = 30,
            )
        }
        val interactions = tasks.mapNotNull { task ->
            val interaction = document.interactions[task.id] ?: return@mapNotNull null
            val taskShape = elements.filterIsInstance<SceneShape>()
                .firstOrNull { shape -> shape.id == "gantt-task-${task.id}" }
                ?: return@mapNotNull null
            SceneNodeInteraction(
                nodeId = task.id,
                bounds = taskShape.bounds,
                link = interaction.link,
                linkTarget = interaction.link?.let { "_self" },
                callbackName = interaction.callbackName,
                callbackArgs = interaction.callbackArgs,
            )
        }
        return GMResult.Ok(
            MermaidScene(
                width = width,
                height = height,
                background = context.theme.background,
                elements = elements.sortedBy(SceneElement::zIndex),
                title = document.title,
                accessibilityTitle = document.accessibilityTitle,
                accessibilityDescription = document.accessibilityDescription,
                interactions = interactions,
            ),
        )
    }

    private fun assignRows(
        tasks: List<GanttTask>,
        categories: List<String>,
        compact: Boolean,
    ): Map<String, Int> {
        if (!compact) {
            return categories.associateWith { category ->
                tasks.count { task -> task.section == category }
            }
        }
        val heights = linkedMapOf<String, Int>()
        var orderOffset = 0
        categories.forEach { category ->
            val categoryTasks = tasks.filter { task -> task.section == category }
                .sortedWith(compareBy(GanttTask::startMillis, GanttTask::order))
            val timeline = MutableList(categoryTasks.size.coerceAtLeast(1)) { Long.MIN_VALUE }
            var maxRow = 0
            categoryTasks.forEach { task ->
                val row = timeline.indexOfFirst { end -> task.startMillis >= end }
                    .takeIf { it >= 0 }
                    ?: timeline.lastIndex
                timeline[row] = task.endMillis
                task.order = row + orderOffset
                maxRow = max(maxRow, row)
            }
            val height = maxRow + 1
            heights[category] = height
            orderOffset += height
        }
        return heights
    }

    private fun addSectionBands(
        tasks: List<GanttTask>,
        categories: List<String>,
        width: Float,
        rightPadding: Float,
        gap: Float,
        height: Float,
        top: Float,
        styleCount: Int,
        palette: GanttPalette,
        elements: MutableList<SceneElement>,
    ) {
        tasks.distinctBy(GanttTask::order).forEach { task ->
            val sectionIndex = categories.indexOf(task.section).coerceAtLeast(0) % styleCount
            elements += SceneShape(
                id = "gantt-row-${task.order}",
                bounds = SceneRect(
                    left = 0f,
                    top = task.order * gap + top - 2f,
                    right = width - rightPadding / 2f,
                    bottom = task.order * gap + top - 2f + gap,
                ),
                kind = SceneShapeKind.Rectangle,
                fill = palette.section(sectionIndex).withOpacity(SECTION_OPACITY),
                stroke = TRANSPARENT,
                strokeWidth = 0f,
                cornerRadius = 0f,
                zIndex = 0,
            )
        }
    }

    private fun addExcludedRanges(
        document: GanttCompiledDocument,
        scale: TimeScale,
        height: Float,
        context: MermaidRenderContext,
        palette: GanttPalette,
        elements: MutableList<SceneElement>,
    ): GMResult<Unit, MermaidError> {
        if (document.excludes.isEmpty() && document.includes.isEmpty()) {
            return GMResult.Ok(Unit)
        }
        if (scale.maximum - scale.minimum > FIVE_YEARS_MILLIS) {
            return GMResult.Ok(Unit)
        }
        val ranges = mutableListOf<LongRange>()
        var rangeStart: Long? = null
        var cursor = GanttDatePort.startOfDay(scale.minimum)
        while (cursor <= scale.maximum) {
            val excluded = isInvalidDate(document, cursor)
            if (excluded && rangeStart == null) {
                rangeStart = cursor
            } else if (!excluded && rangeStart != null) {
                ranges += rangeStart..(cursor - 1L)
                rangeStart = null
            }
            cursor = when (val advanced = GanttDatePort.addDays(cursor, 1)) {
                is GMResult.Ok -> advanced.value
                is GMResult.Err -> return advanced
            }
        }
        rangeStart?.let { start -> ranges += start..GanttDatePort.endOfDay(scale.maximum) }
        ranges.forEachIndexed { index, range ->
            elements += SceneShape(
                id = "gantt-exclude-$index",
                bounds = SceneRect(
                    left = scale(range.first),
                    top = context.options.ganttGridLineStartPadding,
                    right = scale(range.last),
                    bottom = height - context.options.ganttTopPadding,
                ),
                kind = SceneShapeKind.Rectangle,
                fill = palette.exclude,
                stroke = TRANSPARENT,
                strokeWidth = 0f,
                cornerRadius = 0f,
                zIndex = 1,
            )
        }
        return GMResult.Ok(Unit)
    }

    private fun addGrid(
        document: GanttCompiledDocument,
        scale: TimeScale,
        height: Float,
        context: MermaidRenderContext,
        palette: GanttPalette,
        elements: MutableList<SceneElement>,
    ) {
        val requested = GanttDatePort.parseTickInterval(
            document.tickInterval ?: context.options.ganttTickInterval,
        )
        val ticks = requested
            ?.let { interval ->
                GanttDatePort.ticks(
                    minimum = scale.minimum,
                    maximum = scale.maximum,
                    interval = interval,
                    weekStart = document.weekday,
                )
            }
            ?.takeIf(List<Long>::isNotEmpty)
            ?: GanttDatePort.automaticTicks(
                minimum = scale.minimum,
                maximum = scale.maximum,
                desiredCount = DEFAULT_AUTOMATIC_TICK_COUNT,
            )
        val axisFormat = document.axisFormat.ifEmpty {
            if (document.dateFormat.trim() == "D") "%d" else context.options.ganttAxisFormat
        }
        val bottom = height - AXIS_BOTTOM_MARGIN
        ticks.forEachIndexed { index, value ->
            val x = scale(value)
            elements += line(
                id = "gantt-grid-$index",
                start = ScenePoint(x, context.options.ganttGridLineStartPadding),
                end = ScenePoint(x, bottom),
                color = palette.grid.withOpacity(GRID_OPACITY),
                width = 1f,
                zIndex = 2,
                look = context.options.look,
            )
            val text = GanttDatePort.format(value, axisFormat)
            elements += SceneText(
                text = text,
                bounds = SceneRect(
                    left = x - AXIS_LABEL_HALF_WIDTH,
                    top = bottom + AXIS_LABEL_GAP,
                    right = x + AXIS_LABEL_HALF_WIDTH,
                    bottom = height,
                ),
                color = palette.text,
                fontSize = AXIS_FONT_SIZE,
                lineHeight = 1f,
                fontFamily = context.options.fontFamily ?: context.theme.fontFamily,
                weight = SceneTextWeight.Normal,
                zIndex = 20,
            )
            if (document.topAxis || context.options.ganttTopAxis) {
                elements += SceneText(
                    text = text,
                    bounds = SceneRect(
                        left = x - AXIS_LABEL_HALF_WIDTH,
                        top = context.options.ganttTopPadding - AXIS_TOP_LABEL_HEIGHT,
                        right = x + AXIS_LABEL_HALF_WIDTH,
                        bottom = context.options.ganttTopPadding,
                    ),
                    color = palette.text,
                    fontSize = AXIS_FONT_SIZE,
                    lineHeight = 1f,
                    fontFamily = context.options.fontFamily ?: context.theme.fontFamily,
                    weight = SceneTextWeight.Normal,
                    zIndex = 20,
                )
            }
        }
    }

    private fun addTasks(
        tasks: List<GanttTask>,
        regularTaskCount: Int,
        taskLabels: Map<String, MeasuredText>,
        categories: List<String>,
        scale: TimeScale,
        width: Float,
        gap: Float,
        height: Float,
        context: MermaidRenderContext,
        palette: GanttPalette,
        elements: MutableList<SceneElement>,
    ) {
        tasks.forEach { task ->
            val sectionIndex = categories.indexOf(task.section).coerceAtLeast(0)
                .mod(context.options.ganttNumberSectionStyles.coerceAtLeast(1))
            val startX = scale(task.startMillis)
            val actualEndX = scale(task.endMillis)
            val renderEndX = scale(task.renderEndMillis ?: task.endMillis)
            val barHeight = context.options.ganttBarHeight
            val centerX = if (task.flags.milestone) {
                startX + (actualEndX - startX) / 2f
            } else {
                (startX + renderEndX) / 2f
            }
            val milestoneSize = barHeight * MILESTONE_BOUND_SCALE
            val bounds = when {
                task.flags.vertical -> SceneRect(
                    left = startX - barHeight * VERTICAL_WIDTH_FACTOR / 2f,
                    top = context.options.ganttGridLineStartPadding,
                    right = startX + barHeight * VERTICAL_WIDTH_FACTOR / 2f,
                    bottom = min(
                        height,
                        context.options.ganttGridLineStartPadding +
                            regularTaskCount * gap +
                            barHeight * 2f,
                    ),
                )
                task.flags.milestone -> SceneRect(
                    left = centerX - milestoneSize / 2f,
                    top = task.order * gap + context.options.ganttTopPadding +
                        (barHeight - milestoneSize) / 2f,
                    right = centerX + milestoneSize / 2f,
                    bottom = task.order * gap + context.options.ganttTopPadding +
                        (barHeight + milestoneSize) / 2f,
                )
                else -> SceneRect(
                    left = startX,
                    top = task.order * gap + context.options.ganttTopPadding,
                    right = max(startX, renderEndX),
                    bottom = task.order * gap + context.options.ganttTopPadding + barHeight,
                )
            }
            val colors = palette.task(task)
            elements += SceneShape(
                id = "gantt-task-${task.id}",
                bounds = bounds,
                kind = if (task.flags.milestone) {
                    SceneShapeKind.Diamond
                } else {
                    SceneShapeKind.RoundedRectangle
                },
                geometry = if (task.flags.milestone) {
                    diamondGeometry(bounds)
                } else {
                    null
                },
                fill = if (task.flags.vertical) TRANSPARENT else colors.fill,
                stroke = if (task.flags.vertical) palette.vertical else colors.stroke,
                strokeWidth = 2f,
                cornerRadius = if (task.flags.vertical || task.flags.milestone) 0f else 3f,
                zIndex = if (task.flags.vertical) 15 else 10,
            )
            val label = taskLabels.getValue(task.id)
            val labelPlacement = labelPlacement(
                task = task,
                label = label,
                bounds = bounds,
                startX = if (task.flags.milestone) {
                    centerX - barHeight / 2f
                } else {
                    startX
                },
                endX = if (task.flags.milestone) {
                    centerX + barHeight / 2f
                } else {
                    renderEndX
                },
                width = width,
                context = context,
            )
            elements += SceneText(
                text = label.text,
                bounds = labelPlacement.bounds,
                color = if (task.flags.vertical) {
                    palette.vertical
                } else if (labelPlacement.outside) {
                    colors.outsideText
                } else {
                    colors.insideText
                },
                fontSize = if (task.flags.vertical) VERTICAL_FONT_SIZE else label.fontSize,
                lineHeight = DEFAULT_LINE_HEIGHT,
                fontFamily = context.options.fontFamily ?: context.theme.fontFamily,
                weight = if ("clickable" in task.classes) {
                    SceneTextWeight.Bold
                } else {
                    SceneTextWeight.Normal
                },
                spans = if (task.flags.milestone) {
                    listOf(
                        SceneTextSpan(
                            start = 0,
                            end = label.text.length,
                            italic = true,
                        ),
                    )
                } else {
                    emptyList()
                },
                horizontalAlignment = labelPlacement.alignment,
                zIndex = 20,
            )
        }
    }

    private fun labelPlacement(
        task: GanttTask,
        label: MeasuredText,
        bounds: SceneRect,
        startX: Float,
        endX: Float,
        width: Float,
        context: MermaidRenderContext,
    ): LabelPlacement {
        if (task.flags.vertical) {
            return LabelPlacement(
                bounds = SceneRect(
                    left = startX - VERTICAL_LABEL_HALF_WIDTH,
                    top = bounds.bottom + VERTICAL_LABEL_GAP,
                    right = startX + VERTICAL_LABEL_HALF_WIDTH,
                    bottom = bounds.bottom + VERTICAL_LABEL_GAP + label.metrics.height,
                ),
                alignment = SceneTextAlignment.Center,
                outside = true,
            )
        }
        val centerY = bounds.center.y
        if (label.metrics.width <= endX - startX) {
            return LabelPlacement(
                bounds = SceneRect(
                    left = bounds.center.x - label.metrics.width / 2f,
                    top = centerY - label.metrics.height / 2f,
                    right = bounds.center.x + label.metrics.width / 2f,
                    bottom = centerY + label.metrics.height / 2f,
                ),
                alignment = SceneTextAlignment.Center,
                outside = false,
            )
        }
        val placeLeft =
            endX + label.metrics.width + 1.5f * context.options.ganttLeftPadding > width
        return if (placeLeft) {
            LabelPlacement(
                bounds = SceneRect(
                    left = startX - label.metrics.width - TASK_LABEL_GAP,
                    top = centerY - label.metrics.height / 2f,
                    right = startX - TASK_LABEL_GAP,
                    bottom = centerY + label.metrics.height / 2f,
                ),
                alignment = SceneTextAlignment.End,
                outside = true,
            )
        } else {
            LabelPlacement(
                bounds = SceneRect(
                    left = endX + TASK_LABEL_GAP,
                    top = centerY - label.metrics.height / 2f,
                    right = endX + TASK_LABEL_GAP + label.metrics.width,
                    bottom = centerY + label.metrics.height / 2f,
                ),
                alignment = SceneTextAlignment.Start,
                outside = true,
            )
        }
    }

    private fun addSectionLabels(
        categories: List<String>,
        categoryHeights: Map<String, Int>,
        labels: Map<String, MeasuredText>,
        gap: Float,
        context: MermaidRenderContext,
        palette: GanttPalette,
        elements: MutableList<SceneElement>,
    ) {
        var rowOffset = 0
        categories.forEachIndexed { index, category ->
            val rows = categoryHeights[category] ?: 0
            val label = labels[category] ?: return@forEachIndexed
            val centerY = context.options.ganttTopPadding + (rowOffset + rows / 2f) * gap
            elements += SceneText(
                text = label.text,
                bounds = SceneRect(
                    left = SECTION_LABEL_LEFT,
                    top = centerY - label.metrics.height / 2f,
                    right = SECTION_LABEL_LEFT + label.metrics.width,
                    bottom = centerY + label.metrics.height / 2f,
                ),
                color = palette.sectionTitle(index),
                fontSize = label.fontSize,
                lineHeight = DEFAULT_LINE_HEIGHT,
                fontFamily = context.options.fontFamily ?: context.theme.fontFamily,
                weight = SceneTextWeight.Normal,
                horizontalAlignment = SceneTextAlignment.Start,
                zIndex = 20,
            )
            rowOffset += rows
        }
    }

    private fun addTodayMarker(
        document: GanttCompiledDocument,
        scale: TimeScale,
        height: Float,
        context: MermaidRenderContext,
        palette: GanttPalette,
        elements: MutableList<SceneElement>,
    ): GMResult<Unit, MermaidError> {
        if (document.todayMarker == "off") return GMResult.Ok(Unit)
        val today = Clock.System.now().toEpochMilliseconds()
        if (today !in scale.minimum..scale.maximum) return GMResult.Ok(Unit)
        val style = when (val parsed = TodayMarkerStyle.parse(document.todayMarker, palette.today)) {
            is GMResult.Ok -> parsed.value
            is GMResult.Err -> return parsed
        }
        val x = scale(today)
        elements += line(
            id = "gantt-today",
            start = ScenePoint(x, context.options.ganttTitleTopMargin),
            end = ScenePoint(x, height - context.options.ganttTitleTopMargin),
            color = style.color.withOpacity(style.opacity),
            width = style.width,
            zIndex = 25,
            look = context.options.look,
        )
        return GMResult.Ok(Unit)
    }

    private fun isInvalidDate(
        document: GanttCompiledDocument,
        millis: Long,
    ): Boolean {
        val dateOnly = GanttDatePort.dateOnly(millis)
        val formatted = GanttDatePort.formatInput(
            millis,
            document.dateFormat.ifEmpty { "YYYY-MM-DD" },
        )
        if (formatted.lowercase() in document.includes || dateOnly.lowercase() in document.includes) {
            return false
        }
        val day = GanttDatePort.weekday(millis)
        if ("weekends" in document.excludes) {
            val next = GanttWeekday.entries[(document.weekendStart.ordinal + 1) % 7]
            if (day == document.weekendStart || day == next) return true
        }
        if (day.name.lowercase() in document.excludes) return true
        return formatted.lowercase() in document.excludes ||
            dateOnly.lowercase() in document.excludes
    }

    private fun measure(
        text: String,
        fontSize: Float,
        context: MermaidRenderContext,
        weight: SceneTextWeight = SceneTextWeight.Normal,
    ): GMResult<MeasuredText, MermaidError> = try {
        GMResult.Ok(
            MeasuredText(
                text = text,
                metrics = context.textMetrics.measure(
                    TextMetricsRequest(
                        text = text,
                        fontSize = fontSize,
                        maxWidth = UNWRAPPED_TEXT_WIDTH,
                        lineHeight = DEFAULT_LINE_HEIGHT,
                        fontFamily = context.options.fontFamily ?: context.theme.fontFamily,
                        weight = weight,
                    ),
                ),
                fontSize = fontSize,
            ),
        )
    } catch (failure: Throwable) {
        GMResult.Err(
            MermaidError.Layout(
                "Gantt text measurement failed: ${failure.message ?: "unknown error"}",
            ),
        )
    }

    private fun line(
        id: String,
        start: ScenePoint,
        end: ScenePoint,
        color: SceneColor,
        width: Float,
        zIndex: Int,
        look: String,
    ): ScenePath = ScenePath(
        id = id,
        points = listOf(start, end),
        commands = listOf(
            ScenePathCommand.MoveTo(start),
            ScenePathCommand.LineTo(end),
        ),
        color = color,
        strokeWidth = width,
        strokePattern = SceneStrokePattern.Solid,
        arrowStart = SceneArrowHead.None,
        arrowEnd = SceneArrowHead.None,
        curve = "linear",
        look = look,
        animated = false,
        zIndex = zIndex,
    )

    private fun diamondGeometry(bounds: SceneRect): SceneShapeGeometry {
        val halfWidth = bounds.width / 2f
        val halfHeight = bounds.height / 2f
        val points = listOf(
            ScenePoint(0f, -halfHeight),
            ScenePoint(halfWidth, 0f),
            ScenePoint(0f, halfHeight),
            ScenePoint(-halfWidth, 0f),
        )
        return SceneShapeGeometry(
            paths = listOf(
                SceneShapePath(
                    points = points,
                    fill = SceneShapePaint.Fill,
                    stroke = SceneShapePaint.Stroke,
                ),
            ),
            outline = points,
        )
    }

    private fun decode(source: String): String =
        MermaidPreprocessor.decodeEntities(source)

    private data class TimeScale(
        val minimum: Long,
        val maximum: Long,
        val start: Float,
        val end: Float,
    ) {
        private val domainMaximum =
            if (maximum > minimum) maximum else minimum + MILLIS_PER_DAY

        operator fun invoke(value: Long): Float {
            val ratio = (value - minimum).toDouble() /
                (domainMaximum - minimum).toDouble()
            return start + ratio.toFloat() * (end - start)
        }
    }

    private data class MeasuredText(
        val text: String,
        val metrics: TextMetrics,
        val fontSize: Float,
    )

    private data class LabelPlacement(
        val bounds: SceneRect,
        val alignment: SceneTextAlignment,
        val outside: Boolean,
    )

    private data class GanttTaskColors(
        val fill: SceneColor,
        val stroke: SceneColor,
        val insideText: SceneColor,
        val outsideText: SceneColor,
    )

    private data class GanttPalette(
        val background: SceneColor,
        val section0: SceneColor,
        val section1: SceneColor,
        val section2: SceneColor,
        val exclude: SceneColor,
        val taskFill: SceneColor,
        val taskStroke: SceneColor,
        val activeFill: SceneColor,
        val activeStroke: SceneColor,
        val doneFill: SceneColor,
        val doneStroke: SceneColor,
        val criticalFill: SceneColor,
        val criticalStroke: SceneColor,
        val taskText: SceneColor,
        val taskTextDark: SceneColor,
        val taskTextOutside: SceneColor,
        val clickableText: SceneColor,
        val text: SceneColor,
        val grid: SceneColor,
        val today: SceneColor,
        val vertical: SceneColor,
        val title: SceneColor,
    ) {
        fun section(index: Int): SceneColor = when (index.mod(4)) {
            0 -> section0
            2 -> section2
            else -> section1
        }

        fun sectionTitle(@Suppress("UNUSED_PARAMETER") index: Int): SceneColor = title

        fun task(task: GanttTask): GanttTaskColors {
            val fill: SceneColor
            val stroke: SceneColor
            val inside: SceneColor
            when {
                task.flags.active && task.flags.critical -> {
                    fill = activeFill
                    stroke = criticalStroke
                    inside = taskTextDark
                }
                task.flags.done && task.flags.critical -> {
                    fill = doneFill
                    stroke = criticalStroke
                    inside = taskTextDark
                }
                task.flags.critical -> {
                    fill = criticalFill
                    stroke = criticalStroke
                    inside = taskText
                }
                task.flags.active -> {
                    fill = activeFill
                    stroke = activeStroke
                    inside = taskTextDark
                }
                task.flags.done -> {
                    fill = doneFill
                    stroke = doneStroke
                    inside = taskTextDark
                }
                else -> {
                    fill = taskFill
                    stroke = taskStroke
                    inside = taskText
                }
            }
            val clickable = "clickable" in task.classes
            return GanttTaskColors(
                fill = fill,
                stroke = stroke,
                insideText = if (clickable) clickableText else inside,
                outsideText = if (clickable) clickableText else taskTextOutside,
            )
        }

        companion object {
            fun from(context: MermaidRenderContext): GMResult<GanttPalette, MermaidError> {
                var invalid: Pair<String, String>? = null
                fun color(
                    name: String,
                    fallback: SceneColor,
                ): SceneColor {
                    val source = context.options.themeVariables[name] ?: return fallback
                    return CssColorParser.parse(source) ?: fallback.also {
                        if (invalid == null) invalid = name to source
                    }
                }

                val palette = GanttPalette(
                    background = context.theme.background,
                    section0 = color("sectionBkgColor", SECTION_0),
                    section1 = color("altSectionBkgColor", context.theme.background),
                    section2 = color("sectionBkgColor2", SECTION_2),
                    exclude = color("excludeBkgColor", EXCLUDE_FILL),
                    taskFill = color("taskBkgColor", TASK_FILL),
                    taskStroke = color("taskBorderColor", TASK_STROKE),
                    activeFill = color("activeTaskBkgColor", ACTIVE_FILL),
                    activeStroke = color("activeTaskBorderColor", TASK_STROKE),
                    doneFill = color("doneTaskBkgColor", LIGHT_GREY),
                    doneStroke = color("doneTaskBorderColor", GREY),
                    criticalFill = color("critBkgColor", RED),
                    criticalStroke = color("critBorderColor", CRITICAL_BORDER),
                    taskText = color("taskTextColor", WHITE),
                    taskTextDark = color("taskTextDarkColor", BLACK),
                    taskTextOutside = color("taskTextOutsideColor", BLACK),
                    clickableText = color("taskTextClickableColor", CLICKABLE),
                    text = color("textColor", context.theme.nodeText),
                    grid = color("gridColor", LIGHT_GREY),
                    today = color("todayLineColor", RED),
                    vertical = color("vertLineColor", NAVY),
                    title = color("titleColor", context.theme.groupText),
                )
                val error = invalid
                return if (error == null) {
                    GMResult.Ok(palette)
                } else {
                    GMResult.Err(
                        MermaidError.Configuration(
                            "Mermaid theme variable '${error.first}' has invalid color " +
                                "'${error.second}'",
                        ),
                    )
                }
            }
        }
    }

    private data class TodayMarkerStyle(
        val color: SceneColor,
        val width: Float,
        val opacity: Float,
    ) {
        companion object {
            fun parse(
                source: String,
                defaultColor: SceneColor,
            ): GMResult<TodayMarkerStyle, MermaidError> {
                if (source.isBlank()) {
                    return GMResult.Ok(TodayMarkerStyle(defaultColor, 2f, 1f))
                }
                val declarations = source.split(',', ';')
                    .map(String::trim)
                    .filter(String::isNotEmpty)
                    .associate { declaration ->
                        val parts = declaration.split(':', limit = 2)
                        parts.first().trim() to parts.getOrElse(1) { "" }.trim()
                    }
                val unsupported = declarations.keys - setOf("stroke", "stroke-width", "opacity")
                if (unsupported.isNotEmpty()) {
                    return GMResult.Err(
                        MermaidError.UnsupportedFeature(
                            feature = "Gantt todayMarker CSS ${unsupported.sorted().joinToString()}",
                        ),
                    )
                }
                val color = declarations["stroke"]?.let(CssColorParser::parse)
                    ?: defaultColor
                val width = declarations["stroke-width"]
                    ?.removeSuffix("px")
                    ?.toFloatOrNull()
                    ?: 2f
                val opacity = declarations["opacity"]?.toFloatOrNull() ?: 1f
                if (width < 0f || opacity !in 0f..1f) {
                    return GMResult.Err(
                        MermaidError.Configuration("Invalid Gantt todayMarker style '$source'"),
                    )
                }
                return GMResult.Ok(TodayMarkerStyle(color, width, opacity))
            }
        }
    }

    private fun SceneColor.withOpacity(opacity: Float): SceneColor {
        val alpha = ((argb ushr 24) and 0xFF).toInt()
        val adjusted = (alpha * opacity.coerceIn(0f, 1f)).roundToInt()
        return SceneColor((argb and 0x00FFFFFF) or (adjusted.toLong() shl 24))
    }

    private companion object {
        val TRANSPARENT = SceneColor(0x00000000)
        val BLACK = SceneColor(0xFF000000)
        val WHITE = SceneColor(0xFFFFFFFF)
        val NAVY = SceneColor(0xFF000080)
        val RED = SceneColor(0xFFFF0000)
        val GREY = SceneColor(0xFF808080)
        val LIGHT_GREY = SceneColor(0xFFD3D3D3)
        val EXCLUDE_FILL = SceneColor(0xFFEEEEEE)
        val SECTION_0 = SceneColor(0x806666FF)
        val SECTION_2 = SceneColor(0xFFFFF400)
        val TASK_FILL = SceneColor(0xFF8A90DD)
        val TASK_STROKE = SceneColor(0xFF534FBC)
        val ACTIVE_FILL = SceneColor(0xFFBFC7FF)
        val CRITICAL_BORDER = SceneColor(0xFFFF8888)
        val CLICKABLE = SceneColor(0xFF003163)
        val BREAK = Regex("""(?i)<br\s*/?>""")
        const val DEFAULT_LINE_HEIGHT = 1.2f
        const val UNWRAPPED_TEXT_WIDTH = 100_000f
        const val SECTION_OPACITY = 0.2f
        const val GRID_OPACITY = 0.8f
        const val MIN_HEIGHT = 100f
        const val AXIS_BOTTOM_MARGIN = 50f
        const val AXIS_LABEL_GAP = 6f
        const val AXIS_TOP_LABEL_HEIGHT = 20f
        const val AXIS_LABEL_HALF_WIDTH = 60f
        const val AXIS_FONT_SIZE = 10f
        const val DEFAULT_AUTOMATIC_TICK_COUNT = 10
        const val TASK_LABEL_GAP = 5f
        const val VERTICAL_WIDTH_FACTOR = 0.08f
        const val MILESTONE_BOUND_SCALE = 1.1313708f
        const val VERTICAL_FONT_SIZE = 15f
        const val VERTICAL_LABEL_HALF_WIDTH = 75f
        const val VERTICAL_LABEL_GAP = 8f
        const val SECTION_LABEL_LEFT = 10f
        const val MILLIS_PER_DAY = 86_400_000L
        const val FIVE_YEARS_MILLIS = 5L * 366L * MILLIS_PER_DAY
    }
}

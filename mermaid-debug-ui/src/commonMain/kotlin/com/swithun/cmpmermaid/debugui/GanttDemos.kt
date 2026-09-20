package com.swithun.cmpmermaid.debugui

internal data class GanttDemo(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
)

internal val ganttDemos = listOf(
    GanttDemo(
        id = "gantt_basic",
        title = "Basic schedule",
        category = "Tasks",
        source = """
            gantt
                title Product delivery
                dateFormat YYYY-MM-DD
                todayMarker off
                section Build
                Implement feature :build, 2025-01-06, 4d
                Review changes :review, after build, 2d
                section Release
                Publish release :release, after review, 1d
        """.trimIndent(),
    ),
    GanttDemo(
        id = "gantt_statuses",
        title = "Task statuses",
        category = "Tasks",
        source = """
            gantt
                dateFormat YYYY-MM-DD
                axisFormat %b %d
                todayMarker off
                section Delivery
                Completed :done, completed, 2025-02-03, 3d
                Active :active, activeTask, after completed, 3d
                Critical done :crit, done, critical, after activeTask, 2d
                Planned :planned, after critical, 3d
        """.trimIndent(),
    ),
    GanttDemo(
        id = "gantt_previous_task",
        title = "Sequential durations",
        category = "Dependencies",
        source = """
            gantt
                dateFormat YYYY-MM-DD
                axisFormat %b %d
                todayMarker off
                section Pipeline
                Discover :discover, 2025-03-03, 2d
                Design :3d
                Build :4d
                Verify :2d
        """.trimIndent(),
    ),
    GanttDemo(
        id = "gantt_after_multiple",
        title = "Multiple dependencies",
        category = "Dependencies",
        source = """
            gantt
                dateFormat YYYY-MM-DD
                axisFormat %b %d
                todayMarker off
                section Parallel work
                Android :android, 2025-04-01, 5d
                iOS :ios, 2025-04-02, 7d
                Web :web, 2025-04-03, 3d
                Integration :integration, after android ios web, 3d
        """.trimIndent(),
    ),
    GanttDemo(
        id = "gantt_until",
        title = "Until dependencies",
        category = "Dependencies",
        source = """
            gantt
                dateFormat YYYY-MM-DD
                axisFormat %b %d
                todayMarker off
                section Preparation
                Launch :milestone, launch, 2025-05-16, 0d
                Stabilize :stabilize, 2025-05-05, until launch
                Documentation :docs, 2025-05-08, until launch
        """.trimIndent(),
    ),
    GanttDemo(
        id = "gantt_weekends",
        title = "Excluded weekends",
        category = "Calendar",
        source = """
            gantt
                dateFormat YYYY-MM-DD
                axisFormat %a %d
                tickInterval 1day
                excludes weekends
                todayMarker off
                section Sprint
                Implementation :implementation, 2025-06-06, 5d
                Validation :validation, after implementation, 3d
        """.trimIndent(),
    ),
    GanttDemo(
        id = "gantt_friday_weekend",
        title = "Friday weekend",
        category = "Calendar",
        source = """
            gantt
                title Friday and Saturday weekend
                dateFormat YYYY-MM-DD
                axisFormat %a %d
                tickInterval 1day
                excludes weekends
                weekend friday
                todayMarker off
                section Schedule
                Work :work, 2025-07-03, 5d
        """.trimIndent(),
    ),
    GanttDemo(
        id = "gantt_includes",
        title = "Included exception",
        category = "Calendar",
        source = """
            gantt
                dateFormat YYYY-MM-DD
                axisFormat %a %d
                tickInterval 1day
                excludes weekends
                includes 2025-08-09
                todayMarker off
                section Schedule
                Release preparation :prep, 2025-08-08, 3d
        """.trimIndent(),
    ),
    GanttDemo(
        id = "gantt_inclusive_end",
        title = "Inclusive end dates",
        category = "Calendar",
        source = """
            gantt
                dateFormat YYYY-MM-DD
                axisFormat %b %d
                inclusiveEndDates
                todayMarker off
                section Fixed dates
                Design :design, 2025-09-01, 2025-09-03
                Build :build, 2025-09-04, 2025-09-08
        """.trimIndent(),
    ),
    GanttDemo(
        id = "gantt_milestones",
        title = "Milestones",
        category = "Markers",
        source = """
            gantt
                dateFormat YYYY-MM-DD
                axisFormat %b %d
                todayMarker off
                section Roadmap
                Kickoff :milestone, kickoff, 2025-10-01, 0d
                Development :development, 2025-10-01, 8d
                Beta :milestone, beta, after development, 0d
                Hardening :hardening, after beta, 4d
                General availability :milestone, ga, after hardening, 0d
        """.trimIndent(),
    ),
    GanttDemo(
        id = "gantt_vertical",
        title = "Vertical markers",
        category = "Markers",
        source = """
            gantt
                dateFormat YYYY-MM-DD
                axisFormat %b %d
                todayMarker off
                section Delivery
                Code freeze :vert, freeze, 2025-11-05, 1d
                Implementation :implementation, 2025-11-01, 8d
                Store review :review, after implementation, 4d
                Release window :vert, release, 2025-11-14, 1d
        """.trimIndent(),
    ),
    GanttDemo(
        id = "gantt_daily_ticks",
        title = "Daily axis ticks",
        category = "Axis",
        source = """
            gantt
                dateFormat YYYY-MM-DD
                axisFormat %a %d
                tickInterval 1day
                todayMarker off
                section Week
                Research :research, 2025-12-01, 2d
                Prototype :prototype, after research, 3d
                Demo :milestone, demo, after prototype, 0d
        """.trimIndent(),
    ),
    GanttDemo(
        id = "gantt_weekly_ticks",
        title = "Monday weekly ticks",
        category = "Axis",
        source = """
            gantt
                dateFormat YYYY-MM-DD
                axisFormat %b %d
                tickInterval 1week
                weekday monday
                todayMarker off
                section Quarter
                Planning :planning, 2026-01-05, 2w
                Execution :execution, after planning, 4w
        """.trimIndent(),
    ),
    GanttDemo(
        id = "gantt_top_axis",
        title = "Top and bottom axes",
        category = "Axis",
        source = """
            ---
            config:
              gantt:
                topAxis: true
            ---
            gantt
                title Dual axis schedule
                dateFormat YYYY-MM-DD
                axisFormat %b %d
                tickInterval 2day
                todayMarker off
                section Release
                Alpha :alpha, 2026-02-02, 4d
                Beta :beta, after alpha, 4d
                Stable :stable, after beta, 3d
        """.trimIndent(),
    ),
    GanttDemo(
        id = "gantt_compact",
        title = "Compact rows",
        category = "Layout",
        source = """
            ---
            displayMode: compact
            ---
            gantt
                title Compact release train
                dateFormat YYYY-MM-DD
                axisFormat %b %d
                todayMarker off
                section Releases
                Version 1 :v1, 2026-03-01, 3d
                Version 2 :v2, 2026-03-07, 3d
                Hotfix :crit, hotfix, 2026-03-02, 7d
                Version 3 :v3, 2026-03-12, 3d
        """.trimIndent(),
    ),
    GanttDemo(
        id = "gantt_time_scale",
        title = "Hour and minute scale",
        category = "Formats",
        source = """
            gantt
                title Deployment window
                dateFormat HH:mm
                axisFormat %H:%M
                tickInterval 15minute
                todayMarker off
                section Rollout
                Prepare :prepare, 17:00, 20m
                Canary :canary, after prepare, 25m
                Decision :milestone, decision, after canary, 0m
                Global :global, after decision, 30m
        """.trimIndent(),
    ),
    GanttDemo(
        id = "gantt_unix_scale",
        title = "Unix timestamp scale",
        category = "Formats",
        source = """
            gantt
                title Relative issue age
                dateFormat X
                axisFormat %s
                todayMarker off
                section Open
                Oldest :oldest, 0, 71
                Recent :recent, 36, 63
                section Closed
                Resolved :done, resolved, 9, 55
        """.trimIndent(),
    ),
    GanttDemo(
        id = "gantt_metadata",
        title = "Title and accessibility",
        category = "Metadata",
        source = """
            ---
            title: Native delivery plan
            ---
            gantt
                accTitle: Accessible delivery schedule
                accDescr: Three phases ending in a production release
                dateFormat YYYY-MM-DD
                axisFormat %b %d
                todayMarker off
                section Product
                Design :design, 2026-04-01, 4d
                Build :build, after design, 6d
                Release :milestone, release, after build, 0d
        """.trimIndent(),
    ),
    GanttDemo(
        id = "gantt_long_labels",
        title = "Long task labels",
        category = "Stress",
        source = """
            gantt
                title Enterprise authentication migration program
                dateFormat YYYY-MM-DD
                axisFormat %b %d
                todayMarker off
                section Identity platform
                Document existing authentication and recovery requirements :requirements, 2026-05-04, 5d
                Implement backward-compatible credential migration :migration, after requirements, 8d
                Validate account recovery across supported clients :validation, after migration, 5d
        """.trimIndent(),
    ),
    GanttDemo(
        id = "gantt_dense",
        title = "Dense cross-team plan",
        category = "Stress",
        source = """
            ---
            displayMode: compact
            config:
              gantt:
                topAxis: true
            ---
            gantt
                title Cross-team launch
                dateFormat YYYY-MM-DD
                axisFormat %b %d
                tickInterval 1week
                weekday monday
                excludes weekends
                todayMarker off
                section Product
                Requirements :requirements, 2026-06-01, 5d
                UX specification :ux, 2026-06-03, 6d
                Signoff :milestone, signoff, after requirements ux, 0d
                section Engineering
                API implementation :api, after requirements, 8d
                Client implementation :client, after ux, 10d
                Integration :crit, integration, after api client signoff, 5d
                section Quality
                Test planning :tests, 2026-06-08, 5d
                End-to-end validation :active, e2e, after integration tests, 5d
                Launch :milestone, launch, after e2e, 0d
        """.trimIndent(),
    ),
)

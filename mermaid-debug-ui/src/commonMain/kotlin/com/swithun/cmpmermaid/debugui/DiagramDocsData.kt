package com.swithun.cmpmermaid.debugui

import com.swithun.cmpmermaid.core.MermaidRenderOptions
import com.swithun.cmpmermaid.core.MermaidThemePreset

private fun lessons(
    cases: List<DiagramDocsCase>,
    notes: List<String>,
): List<DiagramSyntaxLesson> = cases.take(notes.size).mapIndexed { index, demo ->
    DiagramSyntaxLesson(
        title = demo.title,
        source = demo.source,
        note = notes[index],
        initialAspectRatio = demo.initialAspectRatio,
    )
}

internal val xyChartDiagramDocsSpec = DiagramDocsSpec(
    id = "xychart",
    title = "XY Chart",
    syntaxTitle = "XY charts - Basic Syntax",
    description = "Plot bar and line series against categorical or numeric axes, with optional " +
        "titles, legends, data labels, point labels, and horizontal orientation.",
    documentationUrl = "https://mermaid.js.org/syntax/xyChart.html",
    galleryTitle = "XY Chart demo gallery",
    cases = xyChartDemos.map { demo ->
        DiagramDocsCase(demo.id, demo.title, demo.category, demo.source, 1.4f)
    },
    syntaxLessons = lessons(
        xyChartDemos.take(6).map { demo ->
            DiagramDocsCase(demo.id, demo.title, demo.category, demo.source, 1.4f)
        },
        listOf(
            "The minimal chart needs only the xychart header and one bar or line data set.",
            "Titles, categorical labels, explicit ranges, bars, and lines can be combined.",
            "The horizontal modifier swaps the plot projection while preserving axis semantics.",
            "A numeric X axis distributes data points evenly across the declared range.",
            "When axes are omitted, Mermaid derives X and Y domains from the visible data.",
            "Named plots appear in a right-side legend; unnamed plots stay out of the legend.",
        ),
    ),
    initialTheme = MermaidThemePreset.Default,
)

internal val sequenceDiagramDocsSpec = DiagramDocsSpec(
    id = "sequence",
    title = "Sequence",
    syntaxTitle = "Sequence diagrams - Basic Syntax",
    description = "Describe participants and time-ordered messages, then add activations, " +
        "notes, loops, alternatives, and parallel regions.",
    documentationUrl = "https://mermaid.js.org/syntax/sequenceDiagram.html",
    galleryTitle = "Sequence diagram gallery",
    cases = sequenceDemos.map { demo ->
        DiagramDocsCase(
            id = demo.id,
            title = demo.title,
            category = demo.category,
            source = demo.source,
            initialAspectRatio = 1.05f,
        )
    },
    syntaxLessons = lessons(
        cases = sequenceDemos.take(6).map { demo ->
            DiagramDocsCase(
                id = demo.id,
                title = demo.title,
                category = demo.category,
                source = demo.source,
                initialAspectRatio = 1.05f,
            )
        },
        notes = listOf(
            "Messages progress from top to bottom; solid and dotted arrows express request and response.",
            "Declare participants explicitly when their visible labels differ from their identifiers.",
            "Participant metadata selects actor, boundary, control, entity, database, collection, and queue shapes.",
            "Autonumber adds configurable sequence numbers to following messages.",
            "Activation directives show when a participant owns an active call.",
            "A plus or minus suffix on an arrow opens or closes an activation.",
        ),
    ),
)

internal val classDiagramDocsSpec = DiagramDocsSpec(
    id = "class",
    title = "Class",
    syntaxTitle = "Class diagrams - Basic Syntax",
    description = "Define classes, members, methods, annotations, relationships, cardinalities, " +
        "notes, and namespaces.",
    documentationUrl = "https://mermaid.js.org/syntax/classDiagram.html",
    galleryTitle = "Class diagram gallery",
    cases = classDemos.map { demo ->
        DiagramDocsCase(demo.id, demo.title, demo.category, demo.source, 1.25f)
    },
    syntaxLessons = lessons(
        classDemos.take(6).map { demo ->
            DiagramDocsCase(demo.id, demo.title, demo.category, demo.source, 1.25f)
        },
        listOf(
            "Declare classes through relationships or add members with the colon syntax.",
            "A class body groups fields and methods into compartments.",
            "Annotations describe interfaces, enumerations, services, and other stereotypes.",
            "Quoted identifiers and aliases separate stable IDs from visible labels.",
            "Tildes delimit generic type parameters.",
            "Visibility and member classifiers preserve Mermaid's class notation.",
        ),
    ),
    officialLayout = "elk",
)

internal val stateDiagramDocsSpec = DiagramDocsSpec(
    id = "state",
    title = "State",
    syntaxTitle = "State diagrams - Basic Syntax",
    description = "Model states and transitions, including pseudostates, composite states, notes, " +
        "concurrent regions, directions, and styling.",
    documentationUrl = "https://mermaid.js.org/syntax/stateDiagram.html",
    galleryTitle = "State diagram gallery",
    cases = stateDemos.map { demo ->
        DiagramDocsCase(demo.id, demo.title, demo.category, demo.source, 1.1f)
    },
    syntaxLessons = lessons(
        stateDemos.take(6).map { demo ->
            DiagramDocsCase(demo.id, demo.title, demo.category, demo.source, 1.1f)
        },
        listOf(
            "Use [*] for start and end pseudostates and arrows for transitions.",
            "The legacy stateDiagram header remains accepted for compatibility.",
            "An alias gives a state a readable label while keeping a concise identifier.",
            "Repeated descriptions are rendered inside the state body.",
            "Markdown emphasis is supported in state and transition labels.",
            "Text after a colon labels the transition.",
        ),
    ),
    officialLayout = "elk",
)

internal val erDiagramDocsSpec = DiagramDocsSpec(
    id = "er",
    title = "Entity Relationship",
    syntaxTitle = "Entity relationship diagrams - Basic Syntax",
    description = "Describe entities, attributes, keys, aliases, relationship cardinalities, " +
        "directions, subgraphs, and styles.",
    documentationUrl = "https://mermaid.js.org/syntax/entityRelationshipDiagram.html",
    galleryTitle = "Entity relationship diagram gallery",
    cases = erDemos.map { demo ->
        DiagramDocsCase(demo.id, demo.title, demo.category, demo.source, 1.25f)
    },
    syntaxLessons = lessons(
        erDemos.take(6).map { demo ->
            DiagramDocsCase(demo.id, demo.title, demo.category, demo.source, 1.25f)
        },
        listOf(
            "Relationship markers encode minimum and maximum cardinality at each end.",
            "Entity bodies list typed attributes and optional key constraints.",
            "Attributes can carry multiple key types and quoted comments.",
            "Aliases provide readable labels without changing relationship identifiers.",
            "Combine endpoint markers to express the full cardinality matrix.",
            "Solid links are identifying relationships; dotted links are non-identifying.",
        ),
    ),
    nativeOptions = MermaidRenderOptions(layout = "elk"),
    officialLayout = "elk",
)

internal val ganttDiagramDocsSpec = DiagramDocsSpec(
    id = "gantt",
    title = "Gantt",
    syntaxTitle = "Gantt diagrams - Basic Syntax",
    description = "Build schedules from sections and tasks, with explicit dates, durations, " +
        "dependencies, exclusions, milestones, and axis configuration.",
    documentationUrl = "https://mermaid.js.org/syntax/gantt.html",
    galleryTitle = "Gantt diagram gallery",
    cases = ganttDemos.map { demo ->
        DiagramDocsCase(demo.id, demo.title, demo.category, demo.source, 1.7f)
    },
    syntaxLessons = lessons(
        ganttDemos.take(6).map { demo ->
            DiagramDocsCase(demo.id, demo.title, demo.category, demo.source, 1.7f)
        },
        listOf(
            "Set a date format, group tasks into sections, and connect dependent work with after.",
            "Task flags mark active, done, critical, and planned work.",
            "A duration without a start begins at the previous task's end.",
            "An after expression can wait for multiple task identifiers.",
            "Until expressions end work at a named task or milestone.",
            "Calendar exclusions skip configured weekends and dates.",
        ),
    ),
)

internal val pieDiagramDocsSpec = DiagramDocsSpec(
    id = "pie",
    title = "Pie",
    syntaxTitle = "Pie charts - Basic Syntax",
    description = "Declare labeled numeric slices, optionally display values, and configure donut, " +
        "legend, highlight, and theme behavior.",
    documentationUrl = "https://mermaid.js.org/syntax/pie.html",
    galleryTitle = "Pie chart gallery",
    cases = pieDemos.map { demo ->
        DiagramDocsCase(demo.id, demo.title, demo.category, demo.source, 1.25f)
    },
    syntaxLessons = lessons(
        pieDemos.take(6).map { demo ->
            DiagramDocsCase(demo.id, demo.title, demo.category, demo.source, 1.25f)
        },
        listOf(
            "Each quoted label is paired with a non-negative numeric value.",
            "showData appends values to legend labels.",
            "Slices and legend entries retain declaration order.",
            "Decimal values are accepted without rounding the source data.",
            "Zero values remain visible in the legend.",
            "Tiny slices below Mermaid's drawing threshold still remain in the legend.",
        ),
    ),
    initialTheme = MermaidThemePreset.Default,
)

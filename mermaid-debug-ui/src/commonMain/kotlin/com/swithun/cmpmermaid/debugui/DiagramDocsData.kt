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

internal val quadrantDiagramDocsSpec = DiagramDocsSpec(
    id = "quadrant",
    title = "Quadrant Chart",
    syntaxTitle = "Quadrant charts - Basic Syntax",
    description = "Plot normalized points across four labeled regions with configurable axes, " +
        "dimensions, themes, and reusable point classes.",
    documentationUrl = "https://mermaid.js.org/syntax/quadrantChart.html",
    galleryTitle = "Quadrant Chart demo gallery",
    cases = quadrantDemos.map { demo ->
        DiagramDocsCase(demo.id, demo.title, demo.category, demo.source, 1f)
    },
    syntaxLessons = lessons(
        quadrantDemos.map { demo ->
            DiagramDocsCase(demo.id, demo.title, demo.category, demo.source, 1f)
        },
        listOf(
            "Axes and quadrant labels divide a fixed chart into four semantic regions.",
            "Without points, axis and quadrant labels move to their centered positions.",
            "Frontmatter controls dimensions, axis placement, responsive sizing, and colors.",
            "Point classes provide shared styles while inline declarations take precedence.",
        ),
    ),
    initialTheme = MermaidThemePreset.Default,
)

internal val timelineDiagramDocsSpec = DiagramDocsSpec(
    id = "timeline",
    title = "Timeline",
    syntaxTitle = "Timelines - Basic Syntax",
    description = "Arrange periods and their events from left to right or top to bottom, " +
        "with optional sections, titles, metadata, themes, and responsive sizing.",
    documentationUrl = "https://mermaid.js.org/syntax/timeline.html",
    galleryTitle = "Timeline demo gallery",
    cases = timelineDemos.map { demo ->
        DiagramDocsCase(demo.id, demo.title, demo.category, demo.source, 1.55f)
    },
    syntaxLessons = lessons(
        timelineDemos.map { demo ->
            DiagramDocsCase(demo.id, demo.title, demo.category, demo.source, 1.55f)
        },
        listOf(
            "Periods appear on the main axis and each colon introduces a related event.",
            "Sections group adjacent periods and preserve their declaration order.",
            "The TD modifier places periods left of a vertical axis and events to its right.",
            "Additional event lines continue the most recently declared period.",
            "Frontmatter controls spacing, responsive sizing, colors, and accessibility metadata.",
        ),
    ),
    initialTheme = MermaidThemePreset.Default,
)

internal val kanbanDiagramDocsSpec = DiagramDocsSpec(
    id = "kanban",
    title = "Kanban",
    syntaxTitle = "Kanban boards - Basic Syntax",
    description = "Arrange workflow stages as fixed-width columns with indented tasks, " +
        "ticket and assignee metadata, priority markers, themes, and configurable widths.",
    documentationUrl = "https://mermaid.js.org/syntax/kanban.html",
    galleryTitle = "Kanban demo gallery",
    cases = kanbanDemos.map { demo ->
        DiagramDocsCase(demo.id, demo.title, demo.category, demo.source, 1.7f)
    },
    syntaxLessons = lessons(
        kanbanDemos.map { demo ->
            DiagramDocsCase(demo.id, demo.title, demo.category, demo.source, 1.7f)
        },
        listOf(
            "Top-level nodes create stages and indented nodes create task cards.",
            "Task metadata adds ticket, assignee, and priority details.",
            "Long labels wrap within the configured fixed column width.",
            "Indentation deeper than one task level is flattened into the current stage.",
            "Frontmatter controls section width, ticket links, theme colors, and look.",
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
    officialLayout = "dagre",
    playgroundLayouts = listOf("dagre"),
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
    officialLayout = "dagre",
    playgroundLayouts = listOf("dagre"),
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
    nativeOptions = MermaidRenderOptions(layout = "dagre"),
    officialLayout = "dagre",
    playgroundLayouts = listOf("dagre"),
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

internal val journeyDiagramDocsSpec = DiagramDocsSpec(
    id = "journey",
    title = "User Journey",
    syntaxTitle = "User Journey diagrams - Basic Syntax",
    description = "Split a workflow into sections, score each task from one to five, and show " +
        "which actors participate in every step.",
    documentationUrl = "https://mermaid.js.org/syntax/userJourney.html",
    galleryTitle = "User Journey diagram gallery",
    cases = journeyDemos.map { demo ->
        DiagramDocsCase(
            id = demo.id,
            title = demo.title,
            category = demo.category,
            source = demo.source,
            initialAspectRatio = 1.85f,
        )
    },
    syntaxLessons = lessons(
        journeyDemos.take(6).map { demo ->
            DiagramDocsCase(
                id = demo.id,
                title = demo.title,
                category = demo.category,
                source = demo.source,
                initialAspectRatio = 1.85f,
            )
        },
        listOf(
            "Use the journey header, then group scored tasks under named sections.",
            "Scores control the vertical face position from one at the bottom to five at the top.",
            "List multiple actors after the score to mark every participant on a task.",
            "The actor legend is sorted alphabetically, matching Mermaid's Journey database.",
            "Repeated actor names keep their task markers while appearing once in the legend.",
            "The actor list is optional when a task represents an automatic or anonymous step.",
        ),
    ),
    initialTheme = MermaidThemePreset.Default,
)

internal val requirementDiagramDocsSpec = DiagramDocsSpec(
    id = "requirement",
    title = "Requirement",
    syntaxTitle = "Requirement diagrams - Basic Syntax",
    description = "Model SysML requirements, external elements, verification methods, and " +
        "typed relationships.",
    documentationUrl = "https://mermaid.js.org/syntax/requirementDiagram.html",
    galleryTitle = "Requirement diagram gallery",
    cases = requirementDemos.map { demo ->
        DiagramDocsCase(
            id = demo.id,
            title = demo.title,
            category = demo.category,
            source = demo.source,
            initialAspectRatio = 1.35f,
        )
    },
    syntaxLessons = lessons(
        requirementDemos.take(6).map { demo ->
            DiagramDocsCase(
                id = demo.id,
                title = demo.title,
                category = demo.category,
                source = demo.source,
                initialAspectRatio = 1.35f,
            )
        },
        listOf(
            "A requirement records its type, identifier, text, risk, and verification method.",
            "Six SysML requirement types are available and may be connected in one model.",
            "Seven labeled relationship types express containment, derivation, and evidence.",
            "The reverse arrow form preserves the same source and destination semantics.",
            "Direction TB lays out dependent requirements from top to bottom.",
            "Direction BT reverses the rank order while preserving relationship direction.",
        ),
    ),
    initialTheme = MermaidThemePreset.ReduxColor,
)

internal val gitGraphDiagramDocsSpec = DiagramDocsSpec(
    id = "gitgraph",
    title = "Git Graph",
    syntaxTitle = "Git Graph diagrams - Basic Syntax",
    description = "Visualize commit history, branches, merges, cherry-picks, tags, and " +
        "release workflows.",
    documentationUrl = "https://mermaid.js.org/syntax/gitgraph.html",
    galleryTitle = "Git Graph diagram gallery",
    cases = gitGraphDemos.map { demo ->
        DiagramDocsCase(
            id = demo.id,
            title = demo.title,
            category = demo.category,
            source = demo.source,
            initialAspectRatio = 1.65f,
        )
    },
    syntaxLessons = lessons(
        gitGraphDemos.take(8).map { demo ->
            DiagramDocsCase(
                id = demo.id,
                title = demo.title,
                category = demo.category,
                source = demo.source,
                initialAspectRatio = 1.65f,
            )
        },
        listOf(
            "Use commit to append work to the currently checked-out branch.",
            "NORMAL, REVERSE, and HIGHLIGHT select the official commit glyphs.",
            "Attach one or more quoted tags to mark releases and milestones.",
            "branch creates and checks out a branch; checkout and switch select an existing one.",
            "A merge commit links the current head and the merged branch head.",
            "Nested branches preserve their parent history when they merge back.",
            "Cherry-pick copies a commit from another branch and records its source tag.",
            "Cherry-picking a merge requires one of that merge commit's immediate parents.",
        ),
    ),
    initialTheme = MermaidThemePreset.ReduxColor,
)

internal val mindmapDiagramDocsSpec = DiagramDocsSpec(
    id = "mindmap",
    title = "Mindmap",
    syntaxTitle = "Mindmaps - Basic Syntax",
    description = "Organize concepts into an indentation-based hierarchy with Mermaid's " +
        "Mindmap shapes, text formatting, themes, and layout algorithms.",
    documentationUrl = "https://mermaid.js.org/syntax/mindmap.html",
    galleryTitle = "Mindmap demo gallery",
    cases = mindmapDemos.map { demo ->
        DiagramDocsCase(
            id = demo.id,
            title = demo.title,
            category = demo.category,
            source = demo.source,
            initialAspectRatio = demo.initialAspectRatio,
        )
    },
    syntaxLessons = lessons(
        mindmapDemos.take(8).map { demo ->
            DiagramDocsCase(
                id = demo.id,
                title = demo.title,
                category = demo.category,
                source = demo.source,
                initialAspectRatio = demo.initialAspectRatio,
            )
        },
        listOf(
            "Indentation defines parent and child relationships below one root node.",
            "Default, square, rounded, circle, cloud, bang, and hexagon shapes are supported.",
            "A node attaches to the nearest earlier node with less indentation.",
            "Markdown emphasis is preserved in labels and participates in text measurement.",
            "Long labels wrap at maxNodeWidth, while br tags create explicit line breaks.",
            "Unicode text follows the same hierarchy and layout rules as Latin labels.",
            "HTML entities are decoded at Mermaid's parser boundary.",
            "Percent-prefixed Mermaid comments are removed before Mindmap parsing.",
        ),
    ),
    initialTheme = MermaidThemePreset.Default,
    officialLayout = "cose-bilkent",
    playgroundLayouts = listOf("cose-bilkent", "dagre", "tidy-tree"),
)

internal val packetDiagramDocsSpec = DiagramDocsSpec(
    id = "packet",
    title = "Packet",
    syntaxTitle = "Packet diagrams - Basic Syntax",
    description = "Describe fixed-width protocol fields with explicit bit ranges or relative " +
        "bit counts, automatic row splitting, titles, metadata, and sizing controls.",
    documentationUrl = "https://mermaid.js.org/syntax/packet.html",
    galleryTitle = "Packet demo gallery",
    cases = packetDemos.map { demo ->
        DiagramDocsCase(
            id = demo.id,
            title = demo.title,
            category = demo.category,
            source = demo.source,
            initialAspectRatio = 2.4f,
        )
    },
    syntaxLessons = lessons(
        packetDemos.map { demo ->
            DiagramDocsCase(
                id = demo.id,
                title = demo.title,
                category = demo.category,
                source = demo.source,
                initialAspectRatio = 2.4f,
            )
        },
        listOf(
            "Explicit start-end ranges place fields at exact contiguous bit positions.",
            "The +count form advances automatically from the previous field.",
            "Single-number fields occupy one bit and share the current row.",
            "Packet configuration controls row dimensions, bit labels, spacing, and sizing.",
            "Packet-beta, accessibility metadata, entities, and Unicode use the same model.",
        ),
    ),
    initialTheme = MermaidThemePreset.Default,
)

internal val radarDiagramDocsSpec = DiagramDocsSpec(
    id = "radar",
    title = "Radar",
    syntaxTitle = "Radar charts - Basic Syntax",
    description = "Compare multiple series across named axes with positional or referenced " +
        "values, circular or polygon graticules, legends, themes, and sizing controls.",
    documentationUrl = "https://mermaid.js.org/syntax/radar.html",
    galleryTitle = "Radar demo gallery",
    cases = radarDemos.map { demo ->
        DiagramDocsCase(
            id = demo.id,
            title = demo.title,
            category = demo.category,
            source = demo.source,
            initialAspectRatio = 1f,
        )
    },
    syntaxLessons = lessons(
        radarDemos.map { demo ->
            DiagramDocsCase(
                id = demo.id,
                title = demo.title,
                category = demo.category,
                source = demo.source,
                initialAspectRatio = 1f,
            )
        },
        listOf(
            "Axis declarations establish both display labels and curve value order.",
            "Polygon graticules connect each tick across all declared axes.",
            "Detailed entries may appear in any order and are resolved by axis identifier.",
            "Frontmatter controls dimensions, margins, axis scale, curve tension, and colors.",
            "Titles, accessibility metadata, comments, entities, and Unicode share the grammar.",
        ),
    ),
    initialTheme = MermaidThemePreset.Default,
)

internal val sankeyDiagramDocsSpec = DiagramDocsSpec(
    id = "sankey",
    title = "Sankey",
    syntaxTitle = "Sankey diagrams - Basic Syntax",
    description = "Visualize directed quantities using three-column CSV records, D3 Sankey " +
        "alignment, value labels, link colors, and configurable node geometry.",
    documentationUrl = "https://mermaid.js.org/syntax/sankey.html",
    galleryTitle = "Sankey demo gallery",
    cases = sankeyDemos.map { demo ->
        DiagramDocsCase(
            id = demo.id,
            title = demo.title,
            category = demo.category,
            source = demo.source,
            initialAspectRatio = 1.5f,
        )
    },
    syntaxLessons = lessons(
        sankeyDemos.map { demo ->
            DiagramDocsCase(
                id = demo.id,
                title = demo.title,
                category = demo.category,
                source = demo.source,
                initialAspectRatio = 1.5f,
            )
        },
        listOf(
            "Each CSV row defines a source, target, and numeric flow value.",
            "Quoted fields preserve commas and doubled quote characters.",
            "Left, right, center, and justify alignment follow d3-sankey semantics.",
            "Node colors, link coloring, geometry, and outlined labels are configurable.",
            "The sankey-beta alias and fixed or responsive sizing share one renderer.",
        ),
    ),
    initialTheme = MermaidThemePreset.Default,
)

internal val treemapDiagramDocsSpec = DiagramDocsSpec(
    id = "treemap",
    title = "Treemap",
    syntaxTitle = "Treemap diagrams - Basic Syntax",
    description = "Compare weighted hierarchical data using nested rectangles, D3 squarify " +
        "layout, class styles, value formats, metadata, themes, and sizing controls.",
    documentationUrl = "https://mermaid.js.org/syntax/treemap.html",
    galleryTitle = "Treemap demo gallery",
    cases = treemapDemos.map { demo ->
        DiagramDocsCase(
            id = demo.id,
            title = demo.title,
            category = demo.category,
            source = demo.source,
            initialAspectRatio = 1.45f,
        )
    },
    syntaxLessons = lessons(
        treemapDemos.map { demo ->
            DiagramDocsCase(
                id = demo.id,
                title = demo.title,
                category = demo.category,
                source = demo.source,
                initialAspectRatio = 1.45f,
            )
        },
        listOf(
            "Quoted sections and leaves form a hierarchy through indentation.",
            "Multiple outer nodes share the synthetic root used by the D3 layout.",
            "Class selectors apply reusable fill, stroke, text, width, and font styles.",
            "Frontmatter controls spacing, sizing, typography, visibility, and value formats.",
            "Titles, accessibility metadata, comments, and Unicode share the grammar.",
        ),
    ),
    initialTheme = MermaidThemePreset.Default,
)

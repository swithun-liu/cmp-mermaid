package com.swithun.cmpmermaid.debugui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swithun.cmpmermaid.compose.MermaidDiagram
import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidCompatibility
import com.swithun.cmpmermaid.core.MermaidTheme
import com.swithun.cmpmermaid.core.MermaidThemePreset
import com.swithun.cmpmermaid.debugui.generated.productionCorpusCases
import com.swithun.cmpmermaid.debugui.generated.stabilityCorpusCases
import com.swithun.cmpmermaid.debugui.generated.visualParityCorpusCases

enum class MermaidDebugPreview {
    Native,
    Official,
    ;

    companion object {
        fun from(value: String?): MermaidDebugPreview =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: Native
    }
}

data class MermaidDebugLaunchOptions(
    val auditDemoId: String? = null,
    val auditPreview: MermaidDebugPreview = MermaidDebugPreview.Native,
    val auditLayout: String = "dagre",
    val auditThemeName: String? = null,
    val openPlayground: Boolean = false,
    val playgroundDiagramId: String = "flowchart",
    val playgroundPreview: MermaidDebugPreview = MermaidDebugPreview.Native,
    val openLoadTest: Boolean = false,
    val autoRunLoadTest: Boolean = false,
)

private enum class DebugScreen {
    DiagramTypes,
    Flowchart,
    XyChart,
    Quadrant,
    Timeline,
    Kanban,
    Sequence,
    Class,
    State,
    Er,
    Gantt,
    Pie,
    Journey,
    Requirement,
    GitGraph,
    Mindmap,
    Packet,
    Radar,
    Sankey,
    Treemap,
    Venn,
    Ishikawa,
    Cynefin,
    Playground,
    LoadTest,
}

private enum class DiagramStability(
    val label: String,
) {
    Reaudit("RE-AUDIT"),
    Beta("BETA"),
}

private data class DiagramDestination(
    val screen: DebugScreen,
    val spec: DiagramDocsSpec,
    val summary: String,
    val stability: DiagramStability = DiagramStability.Beta,
)

private val destinations = listOf(
    DiagramDestination(
        DebugScreen.Flowchart,
        flowchartDiagramDocsSpec,
        "Dagre and ELK layouts",
        DiagramStability.Reaudit,
    ),
    DiagramDestination(
        DebugScreen.XyChart,
        xyChartDiagramDocsSpec,
        "Bar and line series with categorical or numeric axes",
        DiagramStability.Reaudit,
    ),
    DiagramDestination(
        DebugScreen.Quadrant,
        quadrantDiagramDocsSpec,
        "Four-region prioritization charts with normalized data points",
        DiagramStability.Beta,
    ),
    DiagramDestination(
        DebugScreen.Timeline,
        timelineDiagramDocsSpec,
        "Horizontal and vertical period-event histories with sections",
        DiagramStability.Beta,
    ),
    DiagramDestination(
        DebugScreen.Kanban,
        kanbanDiagramDocsSpec,
        "Workflow stages with task metadata and priority markers",
        DiagramStability.Beta,
    ),
    DiagramDestination(
        DebugScreen.Sequence,
        sequenceDiagramDocsSpec,
        "Participants, messages, notes, and control regions",
        DiagramStability.Reaudit,
    ),
    DiagramDestination(
        DebugScreen.Class,
        classDiagramDocsSpec,
        "Classes, relations, notes, and namespaces",
        DiagramStability.Reaudit,
    ),
    DiagramDestination(
        DebugScreen.State,
        stateDiagramDocsSpec,
        "States, transitions, composites, notes, and concurrency",
        DiagramStability.Reaudit,
    ),
    DiagramDestination(
        DebugScreen.Er,
        erDiagramDocsSpec,
        "Entities, attributes, cardinalities, and subgraphs",
        DiagramStability.Reaudit,
    ),
    DiagramDestination(
        DebugScreen.Gantt,
        ganttDiagramDocsSpec,
        "Tasks, dependencies, exclusions, and milestones",
        DiagramStability.Reaudit,
    ),
    DiagramDestination(
        DebugScreen.Pie,
        pieDiagramDocsSpec,
        "Pie and donut charts with configurable legends",
        DiagramStability.Reaudit,
    ),
    DiagramDestination(
        DebugScreen.Journey,
        journeyDiagramDocsSpec,
        "Sections, scored tasks, and multi-actor journeys",
        DiagramStability.Reaudit,
    ),
    DiagramDestination(
        DebugScreen.Requirement,
        requirementDiagramDocsSpec,
        "SysML requirements, elements, and typed relationships",
        DiagramStability.Reaudit,
    ),
    DiagramDestination(
        DebugScreen.GitGraph,
        gitGraphDiagramDocsSpec,
        "Commits, branches, merges, cherry-picks, tags, and three orientations",
        DiagramStability.Reaudit,
    ),
    DiagramDestination(
        DebugScreen.Mindmap,
        mindmapDiagramDocsSpec,
        "Hierarchies, seven shapes, themes, and three native layouts",
        DiagramStability.Reaudit,
    ),
    DiagramDestination(
        DebugScreen.Packet,
        packetDiagramDocsSpec,
        "Fixed-width packet fields with ranges, counts, and row splitting",
        DiagramStability.Reaudit,
    ),
    DiagramDestination(
        DebugScreen.Radar,
        radarDiagramDocsSpec,
        "Multi-series polar charts with circular or polygon graticules",
        DiagramStability.Reaudit,
    ),
    DiagramDestination(
        DebugScreen.Sankey,
        sankeyDiagramDocsSpec,
        "Directed quantities with CSV input and D3 Sankey layout",
        DiagramStability.Beta,
    ),
    DiagramDestination(
        DebugScreen.Treemap,
        treemapDiagramDocsSpec,
        "Weighted hierarchies with D3 squarify layout and class styling",
        DiagramStability.Beta,
    ),
    DiagramDestination(
        DebugScreen.Venn,
        vennDiagramDocsSpec,
        "Area-proportional set relationships with unions and nested text",
        DiagramStability.Beta,
    ),
    DiagramDestination(
        DebugScreen.Ishikawa,
        ishikawaDiagramDocsSpec,
        "Cause-and-effect fishbones with alternating and nested branches",
        DiagramStability.Beta,
    ),
    DiagramDestination(
        DebugScreen.Cynefin,
        cynefinDiagramDocsSpec,
        "Five decision domains with organic boundaries and transitions",
        DiagramStability.Beta,
    ),
)

private val stabilityAuditCases: List<Pair<DiagramDocsSpec, DiagramDocsCase>> =
    stabilityCorpusCases.mapNotNull { corpusCase ->
        destinations.firstOrNull { destination ->
            destination.spec.id == corpusCase.diagramId
        }?.let { destination ->
            destination.spec to DiagramDocsCase(
                id = corpusCase.id,
                title = corpusCase.title,
                category = "Stable corpus",
                source = corpusCase.source,
                initialAspectRatio = corpusCase.initialAspectRatio,
            )
        }
    }

private val productionAuditCases: List<Pair<DiagramDocsSpec, DiagramDocsCase>> =
    productionCorpusCases.mapNotNull { corpusCase ->
        destinations.firstOrNull { destination ->
            destination.spec.id == corpusCase.diagramId
        }?.let { destination ->
            destination.spec to DiagramDocsCase(
                id = corpusCase.id,
                title = corpusCase.title,
                category = "Production conformance corpus",
                source = corpusCase.source,
                initialAspectRatio = corpusCase.initialAspectRatio,
            )
        }
    }

private val visualParityAuditCases: List<Pair<DiagramDocsSpec, DiagramDocsCase>> =
    visualParityCorpusCases.mapNotNull { corpusCase ->
        destinations.firstOrNull { destination ->
            destination.spec.id == corpusCase.diagramId
        }?.let { destination ->
            destination.spec to DiagramDocsCase(
                id = corpusCase.id,
                title = corpusCase.title,
                category = "Large-scale visual parity corpus",
                source = corpusCase.source,
                initialAspectRatio = corpusCase.initialAspectRatio,
            )
        }
    }

@Composable
fun MermaidDebugApp(
    options: MermaidDebugLaunchOptions = MermaidDebugLaunchOptions(),
) {
    var screenName by rememberSaveable {
        mutableStateOf(
            when {
                options.openLoadTest -> DebugScreen.LoadTest.name
                options.openPlayground -> DebugScreen.Playground.name
                else -> DebugScreen.DiagramTypes.name
            },
        )
    }
    val screen = DebugScreen.entries.firstOrNull { it.name == screenName }
        ?: DebugScreen.DiagramTypes
    var playgroundDiagramId by rememberSaveable {
        mutableStateOf(options.playgroundDiagramId)
    }
    val playgroundDestination = destinations
        .firstOrNull { destination -> destination.spec.id == playgroundDiagramId }
        ?: destinations.first()
    val colors = lightColorScheme(
        primary = Color(0xFF007F86),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFD4F4F3),
        onPrimaryContainer = Color(0xFF003739),
        secondary = Color(0xFF65558F),
        background = Color(0xFFF7F8FA),
        surface = Color.White,
        surfaceVariant = Color(0xFFF0F2F5),
        outlineVariant = Color(0xFFDDE1E6),
    )
    val auditCase = remember(options.auditDemoId) {
        options.auditDemoId?.let { requestedId ->
            destinations.firstNotNullOfOrNull { destination ->
                destination.spec.cases
                    .firstOrNull { it.id == requestedId }
                    ?.let { demo -> destination.spec to demo }
            } ?: stabilityAuditCases.firstOrNull { (_, corpusCase) ->
                corpusCase.id == requestedId
            } ?: productionAuditCases.firstOrNull { (_, corpusCase) ->
                corpusCase.id == requestedId
            } ?: visualParityAuditCases.firstOrNull { (_, corpusCase) ->
                corpusCase.id == requestedId
            }
        }
    }

    MaterialTheme(colorScheme = colors) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding(),
        ) {
            if (auditCase != null) {
                DiagramAuditScreen(
                    spec = auditCase.first,
                    demo = auditCase.second,
                    preview = options.auditPreview,
                    layoutOverride = options.auditLayout,
                    themeNameOverride = options.auditThemeName,
                )
                return@Box
            }

            val backTarget = when (screen) {
                DebugScreen.DiagramTypes -> null
                DebugScreen.Playground -> playgroundDestination.screen
                DebugScreen.LoadTest -> DebugScreen.DiagramTypes
                else -> DebugScreen.DiagramTypes
            }
            PlatformBackHandler(enabled = backTarget != null) {
                screenName = backTarget?.name ?: DebugScreen.DiagramTypes.name
            }

            when (screen) {
                DebugScreen.DiagramTypes -> DiagramTypesScreen(
                    onOpen = { screenName = it.screen.name },
                    onOpenLoadTest = { screenName = DebugScreen.LoadTest.name },
                )
                DebugScreen.Playground -> DiagramPlaygroundScreen(
                    spec = playgroundDestination.spec,
                    initialPreview = options.playgroundPreview,
                    onBack = { screenName = playgroundDestination.screen.name },
                )
                DebugScreen.LoadTest -> ProductionLoadTestScreen(
                    onBack = { screenName = DebugScreen.DiagramTypes.name },
                    autoRun = options.autoRunLoadTest,
                )
                else -> {
                    val destination = destinations.firstOrNull { it.screen == screen }
                    if (destination == null) {
                        DiagramTypesScreen(
                            onOpen = { screenName = it.screen.name },
                            onOpenLoadTest = { screenName = DebugScreen.LoadTest.name },
                        )
                    } else {
                        UnifiedDiagramDocsScreen(
                            spec = destination.spec,
                            onBack = { screenName = DebugScreen.DiagramTypes.name },
                            actionLabel = "Playground",
                            onAction = {
                                playgroundDiagramId = destination.spec.id
                                screenName = DebugScreen.Playground.name
                            },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DiagramTypesScreen(
    onOpen: (DiagramDestination) -> Unit,
    onOpenLoadTest: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "CMP Mermaid",
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.sp,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(contentPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 760.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    Text(
                        text = "Diagram types",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.sp,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Native Compose Multiplatform renderers",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                item(key = "production-load-test") {
                    DiagramTypeRow(
                        title = "Production load test",
                        description = "${productionCorpusCases.size} mixed complex diagrams",
                        stability = DiagramStability.Reaudit,
                        onClick = onOpenLoadTest,
                    )
                }
                destinations.forEach { destination ->
                    item(key = destination.spec.id) {
                        DiagramTypeRow(
                            title = destination.spec.title,
                            description = destination.summary,
                            stability = destination.stability,
                            onClick = { onOpen(destination) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DiagramTypeRow(
    title: String,
    description: String,
    stability: DiagramStability,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.AccountTree,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.width(8.dp))
                Surface(
                    color = when (stability) {
                        DiagramStability.Reaudit -> Color(0xFFFFF3CD)
                        DiagramStability.Beta -> Color(0xFFFEF3C7)
                    },
                    shape = RoundedCornerShape(4.dp),
                ) {
                    Text(
                        text = stability.label,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        color = when (stability) {
                            DiagramStability.Reaudit -> Color(0xFF7A4E00)
                            DiagramStability.Beta -> Color(0xFF92400E)
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.sp,
                    )
                }
            }
            Spacer(Modifier.height(3.dp))
            Text(
                text = description,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Open $title",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun DiagramAuditScreen(
    spec: DiagramDocsSpec,
    demo: DiagramDocsCase,
    preview: MermaidDebugPreview,
    layoutOverride: String,
    themeNameOverride: String?,
) {
    val usesCorpusLayout =
        spec.id == "flowchart" || spec.id == "requirement" || spec.id == "mindmap"
    val themePreset = MermaidThemePreset.entries.firstOrNull { preset ->
        preset.configName.equals(themeNameOverride, ignoreCase = true)
    } ?: spec.initialTheme
    val nativeOptions = if (spec.id == "mindmap") {
        spec.nativeOptions.copy(
            mindmap = spec.nativeOptions.mindmap.copy(
                layoutAlgorithm = layoutOverride,
            ),
            themeName = themePreset.configName,
        )
    } else {
        spec.nativeOptions.copy(
            layout = if (usesCorpusLayout) layoutOverride else spec.nativeOptions.layout,
            themeName = themePreset.configName,
        )
    }
    var auditStatus by remember(demo.id, preview, layoutOverride, themePreset) {
        mutableStateOf(AUDIT_STATUS_LOADING)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .semantics {
                contentDescription = auditStatus
            }
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        when (preview) {
            MermaidDebugPreview.Native -> MermaidDiagram(
                source = demo.source,
                modifier = Modifier.fillMaxSize(),
                theme = MermaidTheme.preset(themePreset),
                options = nativeOptions,
                contentDescription = "${demo.title} native audit preview",
                respectSourceViewportSizing = false,
                onRenderResult = { result ->
                    auditStatus = when (result) {
                        is GMResult.Ok ->
                            "$AUDIT_STATUS_READY_PREFIX${result.value.toAuditManifestJson()}"
                        is GMResult.Err ->
                            "$AUDIT_STATUS_ERROR_PREFIX${result.error.message}"
                    }
                },
            )
            MermaidDebugPreview.Official -> OfficialMermaidDiagram(
                source = demo.source,
                layout = if (usesCorpusLayout) layoutOverride else spec.officialLayout,
                themeName = themePreset.configName,
                look = spec.nativeOptions.look,
                modifier = Modifier.fillMaxSize(),
                onRenderResult = { result ->
                    auditStatus = when (result) {
                        OfficialRenderResult.Loading -> AUDIT_STATUS_LOADING
                        is OfficialRenderResult.Ready -> AUDIT_STATUS_READY_PREFIX
                        is OfficialRenderResult.Error -> {
                            "$AUDIT_STATUS_ERROR_PREFIX${result.message}"
                        }
                    }
                },
            )
        }
    }
}

private const val AUDIT_STATUS_LOADING = "cmp-mermaid-audit:loading"
private const val AUDIT_STATUS_READY_PREFIX = "cmp-mermaid-audit:ready:"
private const val AUDIT_STATUS_ERROR_PREFIX = "cmp-mermaid-audit:error:"

internal val debugUiVersionLabel: String
    get() = "Mermaid ${MermaidCompatibility.BASELINE_VERSION}"

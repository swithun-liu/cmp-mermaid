package com.swithun.cmpmermaid.debugui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swithun.cmpmermaid.compose.MermaidDiagram
import com.swithun.cmpmermaid.core.MermaidCompatibility
import com.swithun.cmpmermaid.core.MermaidRenderOptions
import com.swithun.cmpmermaid.core.MermaidTheme
import com.swithun.cmpmermaid.core.MermaidThemePreset

internal data class DiagramDocsCase(
    val id: String,
    val title: String,
    val category: String,
    val source: String,
    val initialAspectRatio: Float = 1.4f,
)

internal data class DiagramSyntaxLesson(
    val title: String,
    val source: String,
    val note: String,
    val initialAspectRatio: Float = 1.6f,
)

internal data class DiagramDocsSpec(
    val id: String,
    val title: String,
    val syntaxTitle: String,
    val description: String,
    val documentationUrl: String,
    val galleryTitle: String,
    val cases: List<DiagramDocsCase>,
    val syntaxLessons: List<DiagramSyntaxLesson>,
    val initialTheme: MermaidThemePreset = MermaidThemePreset.ReduxColor,
    val nativeOptions: MermaidRenderOptions = MermaidRenderOptions(),
    val officialLayout: String = "dagre",
    val playgroundLayouts: List<String> = emptyList(),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun UnifiedDiagramDocsScreen(
    spec: DiagramDocsSpec,
    onBack: () -> Unit,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    var selectedTab by rememberSaveable(spec.id) { mutableIntStateOf(0) }
    var selectedThemeName by rememberSaveable(spec.id) {
        mutableStateOf(spec.initialTheme.name)
    }
    val selectedThemePreset = MermaidThemePreset.entries
        .firstOrNull { it.name == selectedThemeName }
        ?: spec.initialTheme
    val selectedTheme = remember(selectedThemePreset) {
        MermaidTheme.preset(selectedThemePreset)
    }
    val themedOptions = remember(spec.nativeOptions, selectedThemePreset) {
        spec.nativeOptions.copy(themeName = selectedThemePreset.configName)
    }
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                            )
                        }
                    },
                    title = {
                        Column {
                            Text(
                                text = spec.title,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.sp,
                            )
                            Text(
                                text = "Mermaid ${MermaidCompatibility.BASELINE_VERSION}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp,
                            )
                        }
                    },
                    actions = {
                        MermaidThemeMenuAction(
                            selectedTheme = selectedThemePreset,
                            onThemeSelected = { theme ->
                                selectedThemeName = theme.name
                            },
                        )
                        if (actionLabel != null && onAction != null) {
                            TextButton(onClick = onAction) {
                                Icon(
                                    imageVector = Icons.Outlined.Code,
                                    contentDescription = null,
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(actionLabel)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                )
                PrimaryTabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Basic Syntax") },
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Demo Gallery") },
                    )
                }
            }
        },
    ) { contentPadding ->
        when (selectedTab) {
            0 -> UnifiedBasicSyntaxPage(
                spec = spec,
                contentPadding = contentPadding,
                theme = selectedTheme,
                options = themedOptions,
            )
            else -> UnifiedDemoGalleryPage(
                spec = spec,
                contentPadding = contentPadding,
                theme = selectedTheme,
                options = themedOptions,
                themeName = selectedThemePreset.configName,
            )
        }
    }
}

@Composable
private fun UnifiedBasicSyntaxPage(
    spec: DiagramDocsSpec,
    contentPadding: PaddingValues,
    theme: MermaidTheme,
    options: MermaidRenderOptions,
) {
    val uriHandler = LocalUriHandler.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(contentPadding),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            Text(
                text = spec.syntaxTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = spec.description,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp,
            )
            Spacer(Modifier.height(14.dp))
            OutlinedButton(
                onClick = { uriHandler.openUri(spec.documentationUrl) },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                    contentDescription = null,
                )
                Spacer(Modifier.width(8.dp))
                Text("Official Mermaid documentation")
            }
        }
        items(spec.syntaxLessons, key = DiagramSyntaxLesson::title) { lesson ->
            UnifiedSyntaxLesson(
                lesson = lesson,
                theme = theme,
                options = options,
            )
        }
    }
}

@Composable
private fun UnifiedSyntaxLesson(
    lesson: DiagramSyntaxLesson,
    theme: MermaidTheme,
    options: MermaidRenderOptions,
) {
    Column {
        Text(
            text = lesson.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(8.dp))
        CodeBlock(lesson.source)
        Spacer(Modifier.height(10.dp))
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val previewHeight = (maxWidth / lesson.initialAspectRatio)
                .coerceIn(180.dp, 420.dp)
            PreviewFrame(label = "CMP Native") {
                MermaidDiagram(
                    source = lesson.source,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(previewHeight),
                    theme = theme,
                    options = options,
                    contentDescription = "${lesson.title} CMP rendering",
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        NoticeBlock(
            title = "INFO",
            text = lesson.note,
            warning = false,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnifiedDemoGalleryPage(
    spec: DiagramDocsSpec,
    contentPadding: PaddingValues,
    theme: MermaidTheme,
    options: MermaidRenderOptions,
    themeName: String,
) {
    val categories = remember(spec.cases) {
        listOf("All") + spec.cases.map(DiagramDocsCase::category).distinct()
    }
    var selectedCategory by rememberSaveable(spec.id) { mutableStateOf("All") }
    val visibleCases = remember(spec.cases, selectedCategory) {
        if (selectedCategory == "All") {
            spec.cases
        } else {
            spec.cases.filter { it.category == selectedCategory }
        }
    }
    var selectedIndex by rememberSaveable(spec.id, selectedCategory) {
        mutableIntStateOf(0)
    }
    var selectedPreview by rememberSaveable(spec.id) { mutableIntStateOf(0) }
    var sourceExpanded by rememberSaveable(spec.id) { mutableStateOf(false) }
    val demo = visibleCases.getOrNull(selectedIndex)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(contentPadding),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = spec.galleryTitle,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "${spec.cases.size} native cases with an on-device Mermaid.js " +
                    "${MermaidCompatibility.BASELINE_VERSION} reference",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(end = 16.dp),
            ) {
                items(categories, key = { it }) { category ->
                    FilterChip(
                        selected = category == selectedCategory,
                        onClick = {
                            selectedCategory = category
                            selectedIndex = 0
                            selectedPreview = 0
                            sourceExpanded = false
                        },
                        label = {
                            val count = if (category == "All") {
                                spec.cases.size
                            } else {
                                spec.cases.count { it.category == category }
                            }
                            Text("$category $count")
                        },
                    )
                }
            }
        }
        if (demo != null) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = {
                            selectedIndex =
                                (selectedIndex - 1 + visibleCases.size) % visibleCases.size
                            selectedPreview = 0
                            sourceExpanded = false
                        },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous case",
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = demo.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = "${selectedIndex + 1} / ${visibleCases.size} · ${demo.category}",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                    IconButton(
                        onClick = {
                            selectedIndex = (selectedIndex + 1) % visibleCases.size
                            selectedPreview = 0
                            sourceExpanded = false
                        },
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next case",
                        )
                    }
                }
            }
            item {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    listOf("CMP Native", "Official").forEachIndexed { index, label ->
                        SegmentedButton(
                            selected = selectedPreview == index,
                            onClick = { selectedPreview = index },
                            shape = SegmentedButtonDefaults.itemShape(index, 2),
                            label = { Text(label) },
                        )
                    }
                }
            }
            item(key = "${demo.id}_$selectedPreview") {
                ResponsiveDiagramPreview(
                    demo = demo,
                    selectedPreview = selectedPreview,
                    theme = theme,
                    options = options,
                    themeName = themeName,
                    officialLayout = spec.officialLayout,
                )
            }
            item {
                TextButton(onClick = { sourceExpanded = !sourceExpanded }) {
                    Text(if (sourceExpanded) "Hide source" else "Source")
                }
                if (sourceExpanded) {
                    CodeBlock(demo.source)
                }
            }
        }
    }
}

@Composable
private fun ResponsiveDiagramPreview(
    demo: DiagramDocsCase,
    selectedPreview: Int,
    theme: MermaidTheme,
    options: MermaidRenderOptions,
    themeName: String,
    officialLayout: String,
) {
    val aspectRatio = demo.initialAspectRatio.coerceAtLeast(0.1f)
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val previewHeight = (maxWidth / aspectRatio).coerceIn(200.dp, 520.dp)
        PreviewFrame(
            label = if (selectedPreview == 0) {
                "CMP Native"
            } else {
                "Official Mermaid.js ${MermaidCompatibility.BASELINE_VERSION}"
            },
        ) {
            if (selectedPreview == 0) {
                MermaidDiagram(
                    source = demo.source,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(previewHeight),
                    theme = theme,
                    options = options,
                    contentDescription = "${demo.title} CMP rendering",
                )
            } else {
                OfficialMermaidDiagram(
                    source = demo.source,
                    layout = officialLayout,
                    themeName = themeName,
                    look = options.look,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(previewHeight),
                )
            }
        }
    }
}

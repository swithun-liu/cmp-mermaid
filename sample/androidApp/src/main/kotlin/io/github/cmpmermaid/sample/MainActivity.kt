package io.github.cmpmermaid.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.cmpmermaid.compose.MermaidDiagram
import io.github.cmpmermaid.core.MermaidCompatibility
import io.github.cmpmermaid.core.MermaidTheme
import io.github.cmpmermaid.sample.generated.FlowchartDemo
import io.github.cmpmermaid.sample.generated.flowchartDemos

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MermaidDocsApp()
        }
    }
}

private enum class DocsScreen {
    DiagramTypes,
    Flowchart,
}

@Composable
private fun MermaidDocsApp() {
    var screen by rememberSaveable { mutableStateOf(DocsScreen.DiagramTypes) }
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

    MaterialTheme(colorScheme = colors) {
        BackHandler(enabled = screen != DocsScreen.DiagramTypes) {
            screen = DocsScreen.DiagramTypes
        }
        when (screen) {
            DocsScreen.DiagramTypes -> DiagramTypesScreen(
                onFlowchartClick = { screen = DocsScreen.Flowchart },
            )
            DocsScreen.Flowchart -> FlowchartDocsScreen(
                onBack = { screen = DocsScreen.DiagramTypes },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DiagramTypesScreen(
    onFlowchartClick: () -> Unit,
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(contentPadding),
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
            item {
                DiagramTypeRow(onClick = onFlowchartClick)
            }
        }
    }
}

@Composable
private fun DiagramTypeRow(
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
                    text = "Flowchart",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.width(8.dp))
                StatusLabel()
            }
            Spacer(Modifier.height(3.dp))
            Text(
                text = "Mermaid ${MermaidCompatibility.BASELINE_VERSION}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Open Flowchart",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun StatusLabel() {
    Surface(
        color = Color(0xFFFEF3C7),
        shape = RoundedCornerShape(4.dp),
    ) {
        Text(
            text = "BETA",
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            color = Color(0xFF92400E),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.sp,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FlowchartDocsScreen(
    onBack: () -> Unit,
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
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
                                text = "Flowchart",
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
            0 -> BasicSyntaxPage(contentPadding)
            else -> DemoGalleryPage(contentPadding)
        }
    }
}

@Composable
private fun BasicSyntaxPage(
    contentPadding: PaddingValues,
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
                text = "Flowcharts - Basic Syntax",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "A flowchart is composed of nodes and links. Declare its direction after the flowchart keyword.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 24.sp,
            )
            Spacer(Modifier.height(14.dp))
            OutlinedButton(
                onClick = {
                    uriHandler.openUri("https://mermaid.js.org/syntax/flowchart.html")
                },
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                    contentDescription = null,
                )
                Spacer(Modifier.width(8.dp))
                Text("Official Mermaid documentation")
            }
        }
        item {
            NoticeBlock(
                title = "WARNING",
                text = "Lowercase \"o\" or \"x\" immediately after a link is interpreted as a circle or cross edge. Add a space or capitalize the node id.",
                warning = true,
            )
        }
        item {
            SyntaxLesson(
                title = "A node (default)",
                source = """
                    flowchart LR
                      id
                """.trimIndent(),
                note = "The node id is displayed when no separate label is provided.",
                height = 130,
            )
        }
        item {
            SyntaxLesson(
                title = "A node with text",
                source = """
                    flowchart LR
                      id1[This is the text in the box]
                """.trimIndent(),
                note = "A label enclosed in square brackets replaces the visible node id.",
                height = 140,
            )
        }
        item {
            SyntaxLesson(
                title = "Direction",
                source = """
                    flowchart LR
                      Start --> Finish
                """.trimIndent(),
                note = "Use TB, BT, LR, or RL to control the primary layout direction.",
                height = 150,
            )
        }
        item {
            SyntaxLesson(
                title = "Nodes and links",
                source = """
                    flowchart TB
                      A([Request]) --> B{Valid?}
                      B -->|Yes| C[(Store)]
                      B -->|No| D[Reject]
                """.trimIndent(),
                note = "Node shapes and edge labels can be combined in the same statement.",
                height = 250,
            )
        }
    }
}

@Composable
private fun SyntaxLesson(
    title: String,
    source: String,
    note: String,
    height: Int,
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(8.dp))
        CodeBlock(source)
        Spacer(Modifier.height(10.dp))
        PreviewFrame(label = "CMP Native") {
            MermaidDiagram(
                source = source,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height.dp),
                theme = MermaidTheme.MermaidDefault,
                contentDescription = "$title CMP rendering",
            )
        }
        Spacer(Modifier.height(10.dp))
        NoticeBlock(
            title = "INFO",
            text = note,
            warning = false,
        )
    }
}

@Composable
private fun NoticeBlock(
    title: String,
    text: String,
    warning: Boolean,
) {
    val background = if (warning) Color(0xFFFFF7E0) else Color(0xFFEFF4FA)
    val accent = if (warning) Color(0xFF9A6700) else Color(0xFF175CD3)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(background, RoundedCornerShape(6.dp))
            .border(1.dp, accent.copy(alpha = 0.22f), RoundedCornerShape(6.dp))
            .padding(14.dp),
    ) {
        Text(
            text = title,
            color = accent,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = text,
            color = Color(0xFF30343B),
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 21.sp,
        )
    }
}

@Composable
private fun DemoGalleryPage(
    contentPadding: PaddingValues,
) {
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
                text = "Flowchart demo gallery",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "${flowchartDemos.size} cases compared against official Mermaid.js ${MermaidCompatibility.BASELINE_VERSION}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        items(flowchartDemos, key = FlowchartDemo::id) { demo ->
            DemoComparison(demo)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DemoComparison(
    demo: FlowchartDemo,
) {
    var selectedPreview by rememberSaveable(demo.id) { mutableIntStateOf(0) }
    var expanded by rememberSaveable("${demo.id}_source") { mutableStateOf(false) }
    val shape = RoundedCornerShape(8.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
            .padding(vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = demo.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = demo.category,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            TextButton(
                onClick = { expanded = !expanded },
            ) {
                Text(if (expanded) "Hide source" else "Source")
            }
        }
        Spacer(Modifier.height(12.dp))
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            listOf("CMP Native", "Official").forEachIndexed { index, label ->
                SegmentedButton(
                    selected = selectedPreview == index,
                    onClick = { selectedPreview = index },
                    shape = SegmentedButtonDefaults.itemShape(index, 2),
                    label = { Text(label) },
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        if (selectedPreview == 0) {
            NativePreview(
                demo = demo,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        } else {
            OfficialPreview(
                demo = demo,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }
        if (expanded) {
            Spacer(Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Box(modifier = Modifier.padding(16.dp)) {
                CodeBlock(demo.source)
            }
        }
    }
}

@Composable
private fun NativePreview(
    demo: FlowchartDemo,
    modifier: Modifier,
) {
    PreviewFrame(
        label = "CMP Native",
        modifier = modifier,
    ) {
        MermaidDiagram(
            source = demo.source,
            modifier = Modifier
                .fillMaxWidth()
                .height(previewHeight(demo).dp),
            theme = MermaidTheme.MermaidDefault,
            contentDescription = "${demo.title} CMP rendering",
        )
    }
}

@Composable
private fun OfficialPreview(
    demo: FlowchartDemo,
    modifier: Modifier,
) {
    PreviewFrame(
        label = "Official Mermaid.js 12.0.0",
        modifier = modifier,
    ) {
        Image(
            painter = painterResource(demo.officialDrawable),
            contentDescription = "${demo.title} official rendering",
            modifier = Modifier
                .fillMaxWidth()
                .height(previewHeight(demo).dp)
                .background(Color.White),
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
private fun PreviewFrame(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.sp,
        )
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(6.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(6.dp)),
        ) {
            content()
        }
    }
}

@Composable
private fun CodeBlock(
    source: String,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF171A21), RoundedCornerShape(6.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
    ) {
        Text(
            text = source,
            color = Color(0xFFE6EDF3),
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            letterSpacing = 0.sp,
            maxLines = 16,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private fun previewHeight(demo: FlowchartDemo): Int = when (demo.id) {
    "question_workflow", "subgraph", "nested_subgraphs", "long_labels" -> 270
    else -> 210
}

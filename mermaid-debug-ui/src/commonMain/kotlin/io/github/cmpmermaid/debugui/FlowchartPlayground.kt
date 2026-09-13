package io.github.cmpmermaid.debugui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.cmpmermaid.compose.MermaidDiagram
import io.github.cmpmermaid.compose.rememberMermaidCjkFontFamily
import io.github.cmpmermaid.core.MermaidCompatibility
import io.github.cmpmermaid.core.MermaidRenderOptions
import io.github.cmpmermaid.core.MermaidTheme
import io.github.cmpmermaid.debugui.generated.FlowchartDemo
import io.github.cmpmermaid.debugui.generated.flowchartDemos

private enum class PlaygroundLayout(
    val label: String,
    val option: String,
) {
    Elk(label = "ELK", option = "elk"),
    Dagre(label = "Dagre", option = "dagre"),
}

private enum class PlaygroundRenderer(
    val label: String,
) {
    Native(label = "CMP Native"),
    Official(label = "Official JS"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FlowchartPlaygroundScreen(
    onBack: () -> Unit,
) {
    val initialDemo = flowchartDemos.first()
    var selectedDemoId by rememberSaveable { mutableStateOf(initialDemo.id) }
    var draftSource by rememberSaveable { mutableStateOf(initialDemo.source) }
    var renderedSource by rememberSaveable { mutableStateOf(initialDemo.source) }
    var selectedLayoutName by rememberSaveable {
        mutableStateOf(PlaygroundLayout.Elk.name)
    }
    var selectedRendererName by rememberSaveable {
        mutableStateOf(PlaygroundRenderer.Native.name)
    }
    val selectedLayout = PlaygroundLayout.entries
        .firstOrNull { it.name == selectedLayoutName }
        ?: PlaygroundLayout.Elk
    val selectedRenderer = PlaygroundRenderer.entries
        .firstOrNull { it.name == selectedRendererName }
        ?: PlaygroundRenderer.Native
    val selectedDemo = flowchartDemos
        .firstOrNull { it.id == selectedDemoId }
        ?: initialDemo
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
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
                            text = "Flowchart Playground",
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
        },
    ) { contentPadding ->
        PlaygroundContent(
            contentPadding = contentPadding,
            selectedDemo = selectedDemo,
            selectedDemoId = selectedDemoId,
            draftSource = draftSource,
            renderedSource = renderedSource,
            selectedLayout = selectedLayout,
            selectedRenderer = selectedRenderer,
            focusManager = focusManager,
            onDemoSelected = { demo ->
                selectedDemoId = demo.id
                draftSource = demo.source
                renderedSource = demo.source
            },
            onDraftChange = { draftSource = it },
            onLayoutSelected = { layout ->
                selectedLayoutName = layout.name
            },
            onRendererSelected = { renderer ->
                selectedRendererName = renderer.name
            },
            onReset = {
                draftSource = selectedDemo.source
                renderedSource = selectedDemo.source
                focusManager.clearFocus()
            },
            onRender = {
                renderedSource = draftSource
                focusManager.clearFocus()
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaygroundContent(
    contentPadding: PaddingValues,
    selectedDemo: FlowchartDemo,
    selectedDemoId: String,
    draftSource: String,
    renderedSource: String,
    selectedLayout: PlaygroundLayout,
    selectedRenderer: PlaygroundRenderer,
    focusManager: FocusManager,
    onDemoSelected: (FlowchartDemo) -> Unit,
    onDraftChange: (String) -> Unit,
    onLayoutSelected: (PlaygroundLayout) -> Unit,
    onRendererSelected: (PlaygroundRenderer) -> Unit,
    onReset: () -> Unit,
    onRender: () -> Unit,
) {
    var examplesExpanded by remember { mutableStateOf(false) }
    val hasPendingChanges = draftSource != renderedSource
    val cjkFontFamily = if (draftSource.cjkFontRanges().isNotEmpty()) {
        rememberMermaidCjkFontFamily()
    } else {
        null
    }
    val sourceVisualTransformation = remember(cjkFontFamily) {
        cjkFontFamily?.let(::CjkFontVisualTransformation) ?: VisualTransformation.None
    }
    val renderOptions = remember(selectedLayout) {
        MermaidRenderOptions(layout = selectedLayout.option)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(contentPadding)
            .imePadding(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            Text(
                text = "Example",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(8.dp))
            ExposedDropdownMenuBox(
                expanded = examplesExpanded,
                onExpandedChange = { examplesExpanded = it },
            ) {
                OutlinedTextField(
                    value = selectedDemo.title,
                    onValueChange = {},
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth(),
                    readOnly = true,
                    singleLine = true,
                    label = { Text("${flowchartDemos.size} presets") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = examplesExpanded,
                        )
                    },
                )
                ExposedDropdownMenu(
                    expanded = examplesExpanded,
                    onDismissRequest = { examplesExpanded = false },
                ) {
                    flowchartDemos.forEach { demo ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = demo.title,
                                        fontWeight = if (demo.id == selectedDemoId) {
                                            FontWeight.SemiBold
                                        } else {
                                            FontWeight.Normal
                                        },
                                    )
                                    Text(
                                        text = demo.category,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.labelSmall,
                                    )
                                }
                            },
                            onClick = {
                                examplesExpanded = false
                                focusManager.clearFocus()
                                onDemoSelected(demo)
                            },
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Layout",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(8.dp))
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth(),
            ) {
                PlaygroundLayout.entries.forEachIndexed { index, layout ->
                    SegmentedButton(
                        selected = selectedLayout == layout,
                        onClick = { onLayoutSelected(layout) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = PlaygroundLayout.entries.size,
                        ),
                        label = { Text(layout.label) },
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = draftSource,
                onValueChange = onDraftChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 240.dp, max = 360.dp),
                label = { Text("Mermaid source") },
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    letterSpacing = 0.sp,
                ),
                visualTransformation = sourceVisualTransformation,
                supportingText = {
                    Text("${draftSource.length} / 50,000 characters")
                },
                minLines = 10,
                maxLines = 18,
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                RenderState(
                    hasPendingChanges = hasPendingChanges,
                    modifier = Modifier.weight(1f),
                )
                OutlinedButton(onClick = onReset) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = null,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Reset")
                }
                Button(
                    onClick = onRender,
                    enabled = draftSource.isNotBlank(),
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Render")
                }
            }
        }

        item {
            Text(
                text = "Preview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(8.dp))
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth(),
            ) {
                PlaygroundRenderer.entries.forEachIndexed { index, renderer ->
                    SegmentedButton(
                        selected = selectedRenderer == renderer,
                        onClick = { onRendererSelected(renderer) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = PlaygroundRenderer.entries.size,
                        ),
                        label = { Text(renderer.label) },
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(6.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(6.dp),
                    )
                    .padding(8.dp),
                contentAlignment = Alignment.Center,
            ) {
                when (selectedRenderer) {
                    PlaygroundRenderer.Native -> MermaidDiagram(
                        source = renderedSource,
                        modifier = Modifier.fillMaxSize(),
                        theme = MermaidTheme.FlowchartDefault,
                        options = renderOptions,
                        contentDescription = "Playground Flowchart native preview",
                    )
                    PlaygroundRenderer.Official -> OfficialMermaidDiagram(
                        source = renderedSource,
                        layout = selectedLayout.option,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

private class CjkFontVisualTransformation(
    private val fontFamily: FontFamily,
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val transformed = AnnotatedString.Builder(text).apply {
            text.text.cjkFontRanges().forEach { range ->
                addStyle(
                    style = SpanStyle(fontFamily = fontFamily),
                    start = range.first,
                    end = range.last + 1,
                )
            }
        }.toAnnotatedString()
        return TransformedText(transformed, OffsetMapping.Identity)
    }
}

private fun String.cjkFontRanges(): List<IntRange> = buildList {
    var rangeStart = -1
    var index = 0
    while (index < length) {
        val first = this@cjkFontRanges[index].code
        val hasSurrogatePair =
            first in HIGH_SURROGATE_RANGE &&
                index + 1 < length &&
                this@cjkFontRanges[index + 1].code in LOW_SURROGATE_RANGE
        val codePoint = if (hasSurrogatePair) {
            val second = this@cjkFontRanges[index + 1].code
            SUPPLEMENTARY_CODE_POINT_OFFSET +
                ((first - HIGH_SURROGATE_START) shl 10) +
                (second - LOW_SURROGATE_START)
        } else {
            first
        }
        val characterLength = if (hasSurrogatePair) 2 else 1
        if (codePoint.usesCjkFont()) {
            if (rangeStart < 0) {
                rangeStart = index
            }
        } else if (rangeStart >= 0) {
            add(rangeStart until index)
            rangeStart = -1
        }
        index += characterLength
    }
    if (rangeStart >= 0) {
        add(rangeStart until length)
    }
}

private fun Int.usesCjkFont(): Boolean =
    this in 0x2E80..0x303F ||
        this in 0x3040..0x30FF ||
        this in 0x3100..0x312F ||
        this in 0x31A0..0x31EF ||
        this in 0x3400..0x4DBF ||
        this in 0x4E00..0x9FFF ||
        this in 0xAC00..0xD7AF ||
        this in 0xF900..0xFAFF ||
        this in 0xFE30..0xFE4F ||
        this in 0xFF00..0xFFEF ||
        this in 0x20000..0x2FA1F

private val HIGH_SURROGATE_RANGE = 0xD800..0xDBFF
private val LOW_SURROGATE_RANGE = 0xDC00..0xDFFF
private const val HIGH_SURROGATE_START = 0xD800
private const val LOW_SURROGATE_START = 0xDC00
private const val SUPPLEMENTARY_CODE_POINT_OFFSET = 0x10000

@Composable
private fun RenderState(
    hasPendingChanges: Boolean,
    modifier: Modifier = Modifier,
) {
    val color = if (hasPendingChanges) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.primary
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape),
        )
        Spacer(Modifier.width(7.dp))
        Text(
            text = if (hasPendingChanges) "Edited" else "Rendered",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

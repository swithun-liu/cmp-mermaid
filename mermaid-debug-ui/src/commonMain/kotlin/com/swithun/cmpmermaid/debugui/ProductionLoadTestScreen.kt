package com.swithun.cmpmermaid.debugui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swithun.cmpmermaid.compose.MermaidDiagram
import com.swithun.cmpmermaid.core.GMResult
import com.swithun.cmpmermaid.core.MermaidRenderOptions
import com.swithun.cmpmermaid.debugui.generated.StabilityCorpusCase
import com.swithun.cmpmermaid.debugui.generated.productionCorpusCases
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProductionLoadTestScreen(
    onBack: () -> Unit,
    autoRun: Boolean,
) {
    val listState = rememberLazyListState()
    var reachedLastCase by remember { mutableStateOf(false) }
    LaunchedEffect(autoRun) {
        if (autoRun) {
            productionCorpusCases.indices.forEach { index ->
                listState.scrollToItem(index)
                delay(AUTO_RUN_ITEM_DELAY_MILLIS)
            }
        }
    }
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
                            text = "Production load test",
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.sp,
                        )
                        Text(
                            text = if (reachedLastCase) {
                                "${productionCorpusCases.size}/${productionCorpusCases.size} rendered"
                            } else {
                                "${productionCorpusCases.size} mixed diagrams"
                            },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(contentPadding),
            contentAlignment = Alignment.TopCenter,
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 900.dp)
                    .semantics {
                        contentDescription = "Production load test corpus"
                    },
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(
                    items = productionCorpusCases,
                    key = StabilityCorpusCase::id,
                ) { case ->
                    LoadTestDiagram(
                        case = case,
                        onRendered = if (case == productionCorpusCases.last()) {
                            {
                                if (!reachedLastCase) {
                                    reachedLastCase = true
                                    println("$LOAD_TEST_COMPLETE_MARKER ${case.id}")
                                }
                            }
                        } else {
                            null
                        },
                    )
                }
            }
        }
    }
}

private const val AUTO_RUN_ITEM_DELAY_MILLIS = 75L

@Composable
private fun LoadTestDiagram(
    case: StabilityCorpusCase,
    onRendered: (() -> Unit)?,
) {
    val shape = RoundedCornerShape(8.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = case.title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = case.id,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall,
        )
        MermaidDiagram(
            source = case.source,
            options = MermaidRenderOptions(layout = case.layout),
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(MaterialTheme.colorScheme.surface)
                .semantics {
                    contentDescription = "Load test diagram ${case.id}"
                },
            respectSourceViewportSizing = false,
            onRenderResult = { result ->
                if (result is GMResult.Ok) {
                    onRendered?.invoke()
                }
            },
        )
    }
}

private const val LOAD_TEST_COMPLETE_MARKER = "CMP_MERMAID_LOAD_TEST_COMPLETE"

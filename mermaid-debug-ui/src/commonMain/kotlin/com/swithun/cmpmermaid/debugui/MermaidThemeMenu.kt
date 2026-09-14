package com.swithun.cmpmermaid.debugui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.swithun.cmpmermaid.core.MermaidTheme
import com.swithun.cmpmermaid.core.MermaidThemePreset

@Composable
internal fun MermaidThemeMenuAction(
    selectedTheme: MermaidThemePreset,
    onThemeSelected: (MermaidThemePreset) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Outlined.Palette,
                contentDescription = "Diagram theme: ${selectedTheme.configName}",
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(220.dp),
        ) {
            Text(
                text = "Diagram theme",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
            MermaidThemePreset.entries.forEach { preset ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = preset.configName,
                            fontWeight = if (preset == selectedTheme) {
                                FontWeight.SemiBold
                            } else {
                                FontWeight.Normal
                            },
                        )
                    },
                    leadingIcon = {
                        ThemeSwatch(preset)
                    },
                    trailingIcon = if (preset == selectedTheme) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                            )
                        }
                    } else {
                        null
                    },
                    onClick = {
                        expanded = false
                        onThemeSelected(preset)
                    },
                )
            }
        }
    }
}

@Composable
private fun ThemeSwatch(preset: MermaidThemePreset) {
    val theme = remember(preset) { MermaidTheme.preset(preset) }
    val shape = RoundedCornerShape(4.dp)
    Row(
        modifier = Modifier
            .size(width = 32.dp, height = 22.dp)
            .clip(shape)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(Color(theme.background.argb.toInt())),
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(Color(theme.nodeFill.argb.toInt())),
        )
    }
}

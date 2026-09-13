package io.github.cmpmermaid.debugui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun NoticeBlock(
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
internal fun PreviewFrame(
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
internal fun CodeBlock(
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

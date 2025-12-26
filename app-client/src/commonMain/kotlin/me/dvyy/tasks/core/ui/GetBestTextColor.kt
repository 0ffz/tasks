package me.dvyy.tasks.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

@Composable
fun Color.getBestTextColor() = when {
    this == Color.Transparent -> MaterialTheme.colorScheme.onSurface
    luminance() > 0.36f -> Color.Black
    else -> Color.White
}

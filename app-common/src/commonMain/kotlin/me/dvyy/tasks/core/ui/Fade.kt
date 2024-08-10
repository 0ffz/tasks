package me.dvyy.tasks.core.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver

@Composable
fun Color.fade(alpha: Float): Color {
    val background = MaterialTheme.colorScheme.background
    return background.copy(alpha = 1f - alpha).compositeOver(this)
}

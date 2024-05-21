package me.dvyy.tasks.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import me.dvyy.tasks.platforms.AppColorScheme

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColorScheme(),
        typography = AppTypography(),
    ) {
        content()
    }
}

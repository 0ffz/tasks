package me.dvyy.tasks.app.ui.elements

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun PlatformSpecificTopBarActions() {
}

@Composable
actual fun PlatformTopBarContainer(modifier: Modifier, content: @Composable () -> Unit) {
    content()
}

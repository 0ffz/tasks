package me.dvyy.tasks.app.ui.elements

import androidx.compose.runtime.Composable

@Composable
actual fun PlatformSpecificTopBarActions() {
}

@Composable
actual fun PlatformTopBarContainer(content: @Composable () -> Unit) {
    content()
}

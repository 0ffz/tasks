package me.dvyy.tasks.core.ui.modifiers

import androidx.compose.ui.Modifier

actual fun Modifier.onHoverIfAvailable(
    onEnter: () -> Unit,
    onExit: () -> Unit
): Modifier = this

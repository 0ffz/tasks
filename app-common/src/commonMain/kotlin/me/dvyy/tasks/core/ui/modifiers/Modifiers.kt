package me.dvyy.tasks.core.ui.modifiers

import androidx.compose.ui.Modifier

expect fun Modifier.onHoverIfAvailable(
    onEnter: () -> Unit,
    onExit: () -> Unit
): Modifier

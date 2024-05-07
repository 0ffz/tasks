package me.dvyy.tasks.platforms

import androidx.compose.ui.Modifier

actual fun Modifier.onHoverIfAvailable(
    onEnter: () -> Unit,
    onExit: () -> Unit
): Modifier = this

package me.dvyy.tasks.platforms

import androidx.compose.ui.Modifier

expect fun Modifier.onHoverIfAvailable(
    onEnter: () -> Unit,
    onExit: () -> Unit
): Modifier

package me.dvyy.tasks.layout.ui

import androidx.compose.ui.graphics.vector.ImageVector
import me.dvyy.tasks.layout.ui.screens.builder.ScreenDest

data class LayoutButton(
    val displayName: String,
    val id: String,
    val structure: ScreenDest,
    val icon: ImageVector,
)

package me.dvyy.tasks.layout.ui

import androidx.compose.ui.graphics.vector.ImageVector

data class LayoutButton(
    val displayName: String,
    val id: String,
    val structure: LayoutStructure.Single,
    val icon: ImageVector,
)

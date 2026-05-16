package me.dvyy.tasks.core.ui

import androidx.compose.ui.unit.Dp

expect object PlatformSpecifics {
    val preferLongPressDrag: Boolean
    val minHitSize: Dp
}

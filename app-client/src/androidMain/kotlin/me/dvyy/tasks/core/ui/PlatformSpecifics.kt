package me.dvyy.tasks.core.ui

import androidx.compose.ui.unit.dp

actual object PlatformSpecifics {
    actual val preferLongPressDrag = true
    actual val minHitSize = 48.dp
    actual val paddingInnerSize = 0.dp
    actual val currentOS: OS = OS.ANDROID
    actual val hoverAvailable: Boolean = false
}

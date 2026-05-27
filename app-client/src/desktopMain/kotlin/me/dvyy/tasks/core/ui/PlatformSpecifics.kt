package me.dvyy.tasks.core.ui

import androidx.compose.ui.unit.dp

actual object PlatformSpecifics {
    actual val preferLongPressDrag = false
    actual val minHitSize = 40.dp
    actual val paddingInnerSize = 6.dp
    actual val currentOS: OS
        get() {
            val osName = System.getProperty("os.name").lowercase()
            return when {
                osName.contains("win") -> OS.WINDOWS
                osName.contains("mac") -> OS.MACOS
                osName.contains("nix") || osName.contains("nux") || osName.contains("aix") -> OS.LINUX
                else -> OS.UNKNOWN
            }
        }
    actual val hoverAvailable: Boolean = true
}

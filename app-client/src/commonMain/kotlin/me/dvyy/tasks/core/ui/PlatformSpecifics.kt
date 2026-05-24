package me.dvyy.tasks.core.ui

import androidx.compose.ui.unit.Dp

enum class OS(
    val extension: String,
) {
    ANDROID("apk"), LINUX("AppImage"), WINDOWS("msi"), MACOS("dmg"), UNKNOWN("error")
}
expect object PlatformSpecifics {
    val preferLongPressDrag: Boolean
    val minHitSize: Dp
    val paddingInnerSize: Dp
    val currentOS: OS
}

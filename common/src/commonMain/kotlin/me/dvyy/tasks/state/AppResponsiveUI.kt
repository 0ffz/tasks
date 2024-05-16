package me.dvyy.tasks.state

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

class AppResponsiveUI(private val windowSizeClass: WindowSizeClass) {
    val width get() = windowSizeClass.widthSizeClass
    val height get() = windowSizeClass.heightSizeClass
    val dateColumns get() = if (windowSizeClass.widthSizeClass > WindowWidthSizeClass.Medium) 7 else 1

    val atMostSmall get() = windowSizeClass.widthSizeClass <= WindowWidthSizeClass.Compact
    val atMostMedium get() = windowSizeClass.widthSizeClass <= WindowWidthSizeClass.Medium
}

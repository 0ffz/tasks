package me.dvyy.tasks.state

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp

class AppUIState(private val windowSizeClass: WindowSizeClass) {
    val width get() = windowSizeClass.widthSizeClass
    val height get() = windowSizeClass.heightSizeClass
    val dateColumns get() = if (windowSizeClass.widthSizeClass > WindowWidthSizeClass.Medium) 7 else 1

    private val atMostSmall get() = windowSizeClass.widthSizeClass <= WindowWidthSizeClass.Compact
    private val atMostMedium get() = windowSizeClass.widthSizeClass <= WindowWidthSizeClass.Medium

    // Tasks
    val taskHeight = 42.dp
    val taskCheckboxSize = 42.dp
    val taskHighlightHeight = 26.dp
    val taskTextPadding = 8.dp
    val alwaysShowCheckbox get() = isSingleColumn

    // Task lists
    val taskListWidth = 300.dp

    // App
    val isSingleColumn get() = atMostMedium
    val appScrollable get() = isSingleColumn
    val smallTopBar get() = !isSingleColumn
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberAppUIState(): AppUIState {
    val windowSizeClass = calculateWindowSizeClass()
    return remember(windowSizeClass) { AppUIState(windowSizeClass) }
}

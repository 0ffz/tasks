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

    val atMostSmall get() = windowSizeClass.widthSizeClass <= WindowWidthSizeClass.Compact
    val atMostMedium get() = windowSizeClass.widthSizeClass <= WindowWidthSizeClass.Medium

    val taskHeight = 42.dp
    val taskCheckboxSize = 42.dp
    val taskHighlightHeight = 26.dp
    val taskTextPadding = 8.dp

    val singleColumnLists get() = atMostMedium
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberAppUIState(): AppUIState {
    val windowSizeClass = calculateWindowSizeClass()
    return remember(windowSizeClass) { AppUIState(windowSizeClass) }
}

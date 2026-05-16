package me.dvyy.tasks.app.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


val UI @Composable @ReadOnlyComposable get() = LocalUIState.current

val LocalUIState = compositionLocalOf<AppUIState> { error("No local responsive UI") }

@Immutable
class AppUIState(private val windowSizeClass: WindowSizeClass) {
    // Window size
    val width get() = windowSizeClass.widthSizeClass
    val height get() = windowSizeClass.heightSizeClass
    val dateColumns get() = if (windowSizeClass.widthSizeClass > WindowWidthSizeClass.Medium) 7 else 1

    private val atMostSmall get() = windowSizeClass.widthSizeClass <= WindowWidthSizeClass.Compact
    private val atMostMedium get() = windowSizeClass.widthSizeClass <= WindowWidthSizeClass.Medium

    // App state
    val isSmall get() = atMostMedium
    val appScrollable get() = isSmall
    val smallTopBar get() = !isSmall

    val taskHighlightHeight = 24.dp
    val taskTextPadding = 4.dp
    val horizontalTaskTextPadding = 4.dp
    val alwaysShowCheckbox get() = isSmall

    // Week view
    val dividerHeight = if (isSmall) 42.dp else 15.dp

    // General
    val padding = Padding()
    val size = Sizes()
    val elevation = Elevations()
    val tasks = Tasks()
    val shapes = Shapes()


    // Tabs
    val tabHeight = 44.dp
    val tabPadding = padding.md

    // Task lists
    val taskListWidth = 300.dp

    val sideBarWidth = size.xxl
    val bottomBarHeight = size.xxl
    val sideBarPadding = padding.sm


    class Padding {
        val sm: Dp = 4.dp
        val md: Dp = 8.dp
        val lg: Dp = 12.dp
        val xl: Dp = 16.dp
        val xxl: Dp = 24.dp
    }

    class Sizes {
        val xsm: Dp = 2.dp
        val sm: Dp = 4.dp
        val md: Dp = 20.dp
        val lg: Dp = 32.dp
        val xl: Dp = 40.dp
        val xxl: Dp = 48.dp
    }

    class Elevations {
        val lv0 = 0.dp
        val lv1 = 1.dp
        val lv2 = 3.dp
        val lv3 = 6.dp
        val lv4 = 8.dp
        val lv5 = 12.dp
    }

    class Tasks {
        val height = 40.dp
        val checkboxSize = 40.dp
        val propertyButtonSize = 40.dp

        val completedFade = 0.3f
    }

    @Immutable
    data class Shapes(
        val roundedCornerSize: Dp = 8.dp,
        val rounded: RoundedCornerShape = RoundedCornerShape(roundedCornerSize),
    )
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberAppUIState(): AppUIState {
    val windowSizeClass = calculateWindowSizeClass()
    return remember(windowSizeClass) { AppUIState(windowSizeClass) }
}

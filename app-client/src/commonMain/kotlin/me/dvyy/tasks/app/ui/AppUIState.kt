package me.dvyy.tasks.app.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.core.ui.PlatformSpecifics


val UI @Composable @ReadOnlyComposable get() = LocalUIState.current

val LocalUIState = compositionLocalOf<AppUIState> { error("No local responsive UI") }

@Immutable
class AppUIState(
    private val windowSizeClass: WindowSizeClass,
    /** Drives most other UI properties, defines the minimum comfortable hit target size for this platform. */
    val minHitTargetSize: Dp,
) {
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
    val taskTextPadding = 3.dp
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
    val tabHeight = tasks.height
    val tabPadding = padding.md

    // Task lists
    val taskListWidth = 300.dp

    val sideBarWidth = tasks.height
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

    inner class Tasks {
        val height = minHitTargetSize
        val propertyButtonSize = minHitTargetSize

        val completedFade = 0.3f
    }

    @Immutable
    data class Shapes(
        val roundedCornerSize: Dp = 8.dp,
        val rounded: RoundedCornerShape = RoundedCornerShape(roundedCornerSize),
        val roundedExtra: RoundedCornerShape = RoundedCornerShape(12.dp),
    )
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberAppUIState(): AppUIState {
    val windowSizeClass = calculateWindowSizeClass()
    val minHitSize = PlatformSpecifics.minHitSize
    return remember(windowSizeClass) { AppUIState(windowSizeClass, minHitSize) }
}

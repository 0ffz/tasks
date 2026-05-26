package me.dvyy.tasks.layout.ui.screens.builder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.QuestionMark
import me.dvyy.tasks.layout.ui.layouts.LayoutDefinition

@Immutable
data class Screen(
    val icon: ImageVector = TablerIcons.Outlined.QuestionMark,
    val tabLabel: @Composable () -> Unit = {},
    val trailingOptions: @Composable () -> Unit = {},
    val content: @Composable ScreenScope.() -> Unit = {},
)

data class ScreenScope(
    val onLayoutChange: (LayoutDefinition) -> Unit,
    val layout: LayoutDefinition,
    val layoutIndex: Int,
)

fun screen(
    icon: ImageVector = TablerIcons.Outlined.QuestionMark,
    tabLabel: @Composable () -> Unit = {},
    trailingOptions: @Composable () -> Unit = {},
    content: @Composable ScreenScope.() -> Unit = {},
) = Screen(
    icon,
    tabLabel,
    trailingOptions,
    content
)
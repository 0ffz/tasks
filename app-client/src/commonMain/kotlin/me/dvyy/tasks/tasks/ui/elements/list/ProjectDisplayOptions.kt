package me.dvyy.tasks.tasks.ui.elements.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import me.dvyy.tasks.app.ui.LocalUIState

@Stable
data class ProjectDisplayOptions(
    val scrollable: Boolean = true,
    val fullHeight: Boolean = false,
    val coloredHeader: Boolean = false,
)

@Composable
fun rememberProjectDisplayOptions(
    scrollable: Boolean = true,
    fullHeight: Boolean? = null,
    coloredHeader: Boolean = false,
): ProjectDisplayOptions {
    val ui = LocalUIState.current
    return remember(ui, scrollable, fullHeight, coloredHeader) {
        ProjectDisplayOptions(
            scrollable = scrollable,
            fullHeight = fullHeight ?: !ui.isSmall,
            coloredHeader = coloredHeader
        )
    }
}
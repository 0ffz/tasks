package me.dvyy.tasks.layout.ui.layouts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import me.dvyy.tasks.core.ui.components.ColumnOrRow
import me.dvyy.tasks.layout.ui.LayoutStructure
import me.dvyy.tasks.tasks.ui.elements.views.Divider

@Composable
fun ScrollableLayout(
    structure: LayoutStructure.Scrollable,
) {
    val scrollState = rememberScrollState()
    val hor = structure.orientation == Orientation.Horizontal
    val scrollModifier =
        if (hor) Modifier.horizontalScroll(scrollState)
        else Modifier.verticalScroll(scrollState)

    ColumnOrRow(
        structure.orientation,
        Modifier.fillMaxSize().then(scrollModifier)
    ) {
        structure.views.forEach {
            var isExpanded by remember { mutableStateOf(true) }
            Divider(
                structure.orientation,
                toggleable = true,
                applyHoverCursor = false,
                onToggle = { isExpanded = it })
            AnimatedVisibility(
                visible = isExpanded,
                enter = if (hor) expandHorizontally() else expandVertically(),
                exit = if (hor) shrinkHorizontally() else shrinkVertically()
            ) {
                Layout(it)
            }
        }
    }
}

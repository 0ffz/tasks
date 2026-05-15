package me.dvyy.tasks.tasks.ui.elements.task.properties

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.dvyy.tasks.model.Highlight
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButton
import me.dvyy.tasks.tasks.ui.elements.task.color

@Composable
fun HighlightButton(
    highlight: Highlight,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.Companion,
    content: @Composable () -> Unit = {},
) {
    BoxButton(onClick = onClick, highlight.color, modifier = modifier) {
        content()
    }
}
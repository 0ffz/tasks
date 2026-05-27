package me.dvyy.tasks.tasks.ui.elements.task.properties

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.model.Highlight

@Composable
fun HighlightButton(
    highlight: Highlight,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.Companion,
    shape: Shape = UI.shapes.rounded,
    content: @Composable () -> Unit = {},
) {
    //TODO
//    BoxButton(onClick = onClick, modifier, highlight.color, tooltip = "Highlight", shape = shape) {
//        content()
//    }
}
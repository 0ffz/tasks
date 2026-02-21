package me.dvyy.tasks.tasks.ui.elements.task.properties

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.dvyy.tasks.model.Highlight

@Composable
fun HighlightButtons(
    onSelectHighlight: (Highlight) -> Unit,
    toggleFocused: () -> Unit,
    modifier: Modifier = Modifier.Companion,
) = Column {
    // One row for light variants, one for dark => (true, false)
    listOf(true, false).forEach { isLight ->
        Row(modifier) {
            Highlight.Type.entries.forEach { type ->
                val highlight = Highlight(type, isLight)
                HighlightButton(
                    highlight,
                    onClick = { onSelectHighlight(highlight); toggleFocused() },
                )
            }
        }
    }
}
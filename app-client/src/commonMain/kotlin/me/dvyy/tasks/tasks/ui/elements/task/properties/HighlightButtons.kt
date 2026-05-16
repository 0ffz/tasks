package me.dvyy.tasks.tasks.ui.elements.task.properties

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.model.Highlight

@Composable
fun HighlightButtons(
    onSelectHighlight: (Highlight) -> Unit,
    toggleFocused: () -> Unit,
    modifier: Modifier = Modifier.Companion,
) = Column {
    // One row for light variants, one for dark => (true, false)
    listOf(true, false).forEach { isLight ->
        Row(modifier.padding(3.dp).clip(UI.shapes.rounded), horizontalArrangement = Arrangement.spacedBy(0.dp)) {
            Highlight.Type.entries.forEach { type ->
                val highlight = Highlight(type, isLight)
                HighlightButton(
                    highlight,
                    onClick = { onSelectHighlight(highlight); toggleFocused() },
                    padded = false,
                    shape = RectangleShape,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
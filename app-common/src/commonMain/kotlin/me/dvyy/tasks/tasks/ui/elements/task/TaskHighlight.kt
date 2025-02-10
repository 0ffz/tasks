package me.dvyy.tasks.tasks.ui.elements.task

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.core.ui.fade

@Composable
fun TaskHighlight(
    text: String,
    highlight: Color,
    completed: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val adjustedHighlight by animateColorAsState(highlight.fade(if (completed) UI.tasks.completedFade else 1f))
    Surface(
        color = adjustedHighlight,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = modifier.height(UI.taskHighlightHeight),
    ) {
        TaskTextPadding {
            Text(text, Modifier.alpha(0f))
        }
    }
}

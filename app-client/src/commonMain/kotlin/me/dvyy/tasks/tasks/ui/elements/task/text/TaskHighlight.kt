package me.dvyy.tasks.tasks.ui.elements.task.text

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.core.ui.fade
import me.dvyy.tasks.tasks.ui.elements.task.color
import me.dvyy.tasks.tasks.ui.state.TaskUiState

@Composable
fun TaskHighlight(
    task: TaskUiState,
    modifier: Modifier = Modifier,
) {
    val adjustedHighlight by animateColorAsState(task.highlight.color.fade(if (task.completed) UI.tasks.completedFade else 1f))
    Surface(
        color = adjustedHighlight,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = modifier.height(UI.taskHighlightHeight),
    ) {
        TaskTextPadding {
            Text(task.text, Modifier.alpha(0f))
        }
    }
}

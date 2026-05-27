package me.dvyy.tasks.tasks.ui.elements.task.text

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import dev.seyfarth.tablericons.outlined.SquareRounded
import dev.seyfarth.tablericons.outlined.SquareRoundedCheck
import me.dvyy.tasks.app.AppIcons
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.core.ui.fade
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButton
import me.dvyy.tasks.tasks.ui.elements.task.disableDragGestures
import me.dvyy.tasks.tasks.ui.state.TaskState

@Composable
fun TaskCheckBox(
    task: TaskState,
    icon: ImageVector? = null,
    completedIcon: ImageVector? = null,
) {
    val textColor by animateColorAsState(MaterialTheme.colorScheme.onSurface.fade(if (task.uiState.completed) 0.3f else 1f))
    val completed = task.uiState.completed
    BoxButton(
        icon = if (completed) AppIcons.SquareRoundedCheck else AppIcons.SquareRounded,
        onClick = { task.updateUi { it.copy(completed = !it.completed) } },
        tooltip = if (completed) "Completed" else "Check",
        shape = CircleShape,
        border = null,
        tint = textColor,
        modifier = Modifier.height(UI.tasks.height).disableDragGestures()
    )
}

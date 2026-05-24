package me.dvyy.tasks.tasks.ui.elements.task.text

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.SquareRounded
import dev.seyfarth.tablericons.outlined.SquareRoundedCheck
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
    BoxButton(
        onClick = { task.updateUi { it.copy(completed = !it.completed) } },
        shape = CircleShape,
        border = null,
        modifier = Modifier.height(UI.tasks.height).disableDragGestures()
    ) {
        val textColor by animateColorAsState(MaterialTheme.colorScheme.onSurface.fade(if (task.uiState.completed) 0.3f else 1f))
        when {
            task.uiState.completed -> Icon(completedIcon ?: TablerIcons.Outlined.SquareRoundedCheck, contentDescription = "Completed", tint = textColor)
            else -> Icon(icon ?: TablerIcons.Outlined.SquareRounded, contentDescription = "Mark as completed")
        }
    }
}

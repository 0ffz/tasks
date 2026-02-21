package me.dvyy.tasks.tasks.ui.elements.task.text

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import me.dvyy.tasks.app.ui.UI
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
        when {
            task.uiState.completed -> Icon(completedIcon ?: Icons.Outlined.TaskAlt, contentDescription = "Completed")
            else -> Icon(icon ?: Icons.Outlined.RadioButtonUnchecked, contentDescription = "Mark as completed")
        }
    }
}

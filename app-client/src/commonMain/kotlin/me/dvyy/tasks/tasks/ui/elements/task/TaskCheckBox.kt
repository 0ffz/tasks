package me.dvyy.tasks.tasks.ui.elements.task

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
import me.dvyy.tasks.tasks.ui.state.TaskUiState

@Composable
fun TaskCheckBox(
    selected: Boolean,
    task: TaskUiState,
    setTask: (TaskUiState) -> Unit,
    icon: ImageVector? = null,
    completedIcon: ImageVector? = null,
) {
    BoxButton(
        onClick = { setTask(task.copy(completed = !task.completed)) },
        shape = CircleShape,
        border = null,
        modifier = Modifier.height(UI.tasks.height)
    ) {
        when {
            task.completed -> Icon(completedIcon ?: Icons.Outlined.TaskAlt, contentDescription = "Completed")
            else -> Icon(icon ?: Icons.Outlined.RadioButtonUnchecked, contentDescription = "Mark as completed")
        }
    }
}

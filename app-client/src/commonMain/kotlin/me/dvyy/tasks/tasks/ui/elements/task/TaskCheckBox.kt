package me.dvyy.tasks.tasks.ui.elements.task

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.core.ui.fade
import me.dvyy.tasks.tasks.ui.state.TaskUiState

@Composable
fun TaskCheckBox(
    task: TaskUiState,
    setTask: (TaskUiState) -> Unit,
    icon: ImageVector? = null,
    completedIcon: ImageVector? = null,
) {
    IconButton(
        onClick = { setTask(task.copy(completed = !task.completed)) },
        colors = IconButtonDefaults.iconButtonColors().let {
            it.copy(contentColor = it.contentColor.fade(if (task.completed) UI.tasks.completedFade else 1f))
        },
        modifier = Modifier.size(UI.tasks.checkboxSize)
    ) {
        when {
            task.completed -> Icon(completedIcon ?: Icons.Outlined.TaskAlt, contentDescription = "Completed")
            else -> Icon(icon ?: Icons.Outlined.RadioButtonUnchecked, contentDescription = "Mark as completed")
        }
    }
}

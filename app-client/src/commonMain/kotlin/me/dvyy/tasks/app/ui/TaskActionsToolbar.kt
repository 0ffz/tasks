package me.dvyy.tasks.app.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.EditCalendar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import me.dvyy.tasks.tasks.ui.TasksViewModel

//TODO test out vs inline toolbar on touchscreen
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskActionsToolbar(tasksViewModel: TasksViewModel) {
    var expanded by remember { mutableStateOf(true) }
    val selectedTask by tasksViewModel.selectedTask.collectAsState()
    val interactions = selectedTask?.let { tasksViewModel.interactionsFor(it.list, it.task) }
    HorizontalFloatingToolbar(
        expanded = selectedTask != null,
        trailingContent = {
            TooltipBox(
                positionProvider =
                    TooltipDefaults.rememberTooltipPositionProvider(
                        TooltipAnchorPosition.Above
                    ),
                tooltip = { PlainTooltip { Text("Move task") } },
                state = rememberTooltipState(),
            ) {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Outlined.EditCalendar, contentDescription = "Add task")
                }
            }
            FilledTonalIconButton(onClick = { interactions?.onDelete() }) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete")
            }

        },

        ) {
        FilledIconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Outlined.Add, contentDescription = "Add task")
        }
    }
}
package me.dvyy.tasks.app.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.seyfarth.tablericons.TablerIcons
import dev.seyfarth.tablericons.outlined.AppWindow
import dev.seyfarth.tablericons.outlined.Menu2
import kotlinx.coroutines.launch
import me.dvyy.tasks.tasks.ui.TasksViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

//TODO test out vs inline toolbar on touchscreen
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskActionsToolbar(
    modifier: Modifier = Modifier,
    tasksViewModel: TasksViewModel = koinViewModel(),
    app: AppState = koinInject(),
) {
    var expanded by remember { mutableStateOf(true) }
    val selectedTask by tasksViewModel.selectedTask.collectAsState()
    val scope = rememberCoroutineScope()
    HorizontalFloatingToolbar(
        modifier = modifier,
        expanded = selectedTask != null,
        trailingContent = {
            FilledTonalIconButton(onClick = { }) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete")
            }

        },

        ) {
        IconButton(onClick = { scope.launch { app.drawerState.open() } }) {
            Icon(TablerIcons.Outlined.Menu2, contentDescription = "Add task")
        }
        FilledIconButton(onClick = { expanded = !expanded }) {
            Icon(Icons.Outlined.Add, contentDescription = "Add task")
        }
        IconButton(onClick = { scope.launch { app.drawerState.open() } }) {
            Icon(TablerIcons.Outlined.AppWindow, contentDescription = "Add task")
        }
    }
}
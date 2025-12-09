package me.dvyy.tasks.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import me.dvyy.tasks.tasks.ui.TasksViewModel
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppAndroid() = KoinContext {
    App(topBar = {}, contentModifier = Modifier.imePadding()) {
        TaskDeselectHandler()
    }
}


@Composable
fun TaskDeselectHandler(
    viewModel: TasksViewModel = koinViewModel(),
) {
    val selectedTask by viewModel.selectedTask.collectAsState()
    val focusManager = LocalFocusManager.current
    BackHandler {
        if (selectedTask != null) {
            viewModel.selectTask(null)
            focusManager.clearFocus()
        }
    }
}

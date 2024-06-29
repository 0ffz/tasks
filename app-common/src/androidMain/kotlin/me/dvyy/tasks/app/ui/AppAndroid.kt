package me.dvyy.tasks.app.ui

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalFocusManager
import me.dvyy.tasks.app.data.DriverFactory
import me.dvyy.tasks.app.data.createDatabase
import me.dvyy.tasks.di.koinViewModel
import me.dvyy.tasks.tasks.ui.TasksViewModel
import org.koin.dsl.module

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppAndroid(applicationContext: Context) = App(
    extraModules = listOf(module {
        single { createDatabase(DriverFactory(applicationContext)) }
    }),
) {
    TaskDeselectHandler()
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

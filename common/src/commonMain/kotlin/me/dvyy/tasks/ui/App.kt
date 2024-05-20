package me.dvyy.tasks.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import me.dvyy.tasks.data.PersistentStore
import me.dvyy.tasks.data.TaskRepository
import me.dvyy.tasks.di.appModule
import me.dvyy.tasks.state.AppState
import me.dvyy.tasks.state.LocalUIState
import me.dvyy.tasks.state.rememberAppUIState
import me.dvyy.tasks.stateholder.TasksViewModel
import me.dvyy.tasks.ui.elements.app.AppDialogs
import me.dvyy.tasks.ui.elements.app.AppDrawer
import me.dvyy.tasks.ui.elements.app.AppTopBar
import me.dvyy.tasks.ui.elements.modifiers.clickableWithoutRipple
import me.dvyy.tasks.ui.screens.home.HomeScreen
import me.dvyy.tasks.ui.theme.AppTheme
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    AppTheme {
        KoinApplication(application = {
            modules(appModule())
        }) {
            val app: AppState = koinInject()
            val responsive = rememberAppUIState()
//        val tasksStateHolder = remember { TasksStateHolder() }
            CompositionLocalProvider(
                LocalUIState provides responsive,
            ) {
                val tasksStateHolder = viewModel {
                    TasksViewModel(
                        tasks = TaskRepository(
                            localStore = PersistentStore(),
                            syncClient = app.sync,
                            ioDispatcher = Dispatchers.Default
                        )
                    )
                }
                BoxWithConstraints(
                    Modifier.clickableWithoutRipple { tasksStateHolder.selectTask(null) }
                ) {
                    val scrollBehavior = if (responsive.isSingleColumn)
                        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
                    else TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
                    AppDrawer {
                        Scaffold(
                            Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
                            topBar = { AppTopBar(scrollBehavior) },
                        ) { paddingValues ->
                            Box(Modifier.padding(paddingValues)) {
                                HomeScreen()
                            }
                        }
                        AppDialogs()
                    }
                }
            }
        }
    }
}


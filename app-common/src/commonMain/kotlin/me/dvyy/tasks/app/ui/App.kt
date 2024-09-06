package me.dvyy.tasks.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import me.dvyy.tasks.app.ui.dialogs.AppDialogs
import me.dvyy.tasks.app.ui.elements.AppDrawer
import me.dvyy.tasks.app.ui.elements.AppTopBar
import me.dvyy.tasks.app.ui.theme.AppTheme
import me.dvyy.tasks.core.ui.modifiers.clickableWithoutRipple
import me.dvyy.tasks.di.*
import me.dvyy.tasks.tasks.ui.TasksViewModel
import me.dvyy.tasks.tasks.ui.elements.list.WeekView
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.koinApplication

fun createAppKoinApplication(extras: KoinAppDeclaration = {}) = koinApplication {
    extras()
    modules(
        appModule(),
        repositoriesModule(),
        authModule(),
        syncModule(),
        viewModelsModule(),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    topBar: @Composable (TopAppBarScrollBehavior) -> Unit = { AppTopBar(it) },
    extras: @Composable () -> Unit = { },
) {
    AppTheme {
        val responsive = rememberAppUIState()
        val tasksViewModel = koinViewModel<TasksViewModel>()
        CompositionLocalProvider(
            LocalUIState provides responsive,
        ) {
            val scrollBehavior = if (responsive.isSingleColumn)
                TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
            else TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
            AppDrawer {
                Scaffold(
                    topBar = { topBar(scrollBehavior) },
                ) { paddingValues ->
                    Box(
                        Modifier.padding(paddingValues)
                            .clickableWithoutRipple { tasksViewModel.selectTask(null) }) {
                        WeekView(scrollBehavior)
                    }
                }
                AppDialogs()
            }
        }
        extras()
    }
}


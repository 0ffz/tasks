package me.dvyy.tasks.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import me.dvyy.tasks.app.ui.elements.AppDialogs
import me.dvyy.tasks.app.ui.elements.AppDrawer
import me.dvyy.tasks.app.ui.elements.AppTopBar
import me.dvyy.tasks.app.ui.theme.AppTheme
import me.dvyy.tasks.core.ui.modifiers.clickableWithoutRipple
import me.dvyy.tasks.di.*
import me.dvyy.tasks.tasks.ui.HomeScreen
import me.dvyy.tasks.tasks.ui.TasksViewModel
import org.koin.compose.KoinApplication
import org.koin.core.module.Module

@Composable
fun AppKoinContext(extraModules: List<Module> = emptyList(), content: @Composable () -> Unit) {
    KoinApplication(application = {
        modules(extraModules)
        modules(
            appModule(),
            repositoriesModule(),
            authModule(),
            syncModule(),
            viewModelsModule(),
        )
    }) {
        content()
    }
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
                    Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = { topBar(scrollBehavior) },
                ) { paddingValues ->
                    Box(
                        Modifier.padding(paddingValues)
                            .clickableWithoutRipple { tasksViewModel.selectTask(null) }) {
                        HomeScreen()
                    }
                }
                AppDialogs()
            }
        }
        extras()
    }
}


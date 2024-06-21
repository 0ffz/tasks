package me.dvyy.tasks.app.ui

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
import me.dvyy.Database
import me.dvyy.tasks.app.ui.elements.AppDialogs
import me.dvyy.tasks.app.ui.elements.AppDrawer
import me.dvyy.tasks.app.ui.elements.AppTopBar
import me.dvyy.tasks.app.ui.theme.AppTheme
import me.dvyy.tasks.core.ui.modifiers.clickableWithoutRipple
import me.dvyy.tasks.di.appModule
import me.dvyy.tasks.di.koinViewModel
import me.dvyy.tasks.di.syncModule
import me.dvyy.tasks.di.viewModelsModule
import me.dvyy.tasks.tasks.ui.HomeScreen
import me.dvyy.tasks.tasks.ui.TasksViewModel
import org.koin.compose.KoinApplication
import org.koin.dsl.module

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(database: Database) {
    AppTheme {
        KoinApplication(application = {
            modules(
                appModule(),
                syncModule(),
                viewModelsModule(),
                module { single { database } }
            )
        }) {
            val responsive = rememberAppUIState()
            val tasksViewModel = koinViewModel<TasksViewModel>()
            CompositionLocalProvider(
                LocalUIState provides responsive,
            ) {
                BoxWithConstraints(
                    Modifier.clickableWithoutRipple { tasksViewModel.selectTask(null) }
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


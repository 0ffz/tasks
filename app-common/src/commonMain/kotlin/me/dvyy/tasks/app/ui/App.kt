package me.dvyy.tasks.app.ui

import androidx.compose.foundation.layout.Box
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
import me.dvyy.tasks.app.ui.elements.AppDialogs
import me.dvyy.tasks.app.ui.elements.AppDrawer
import me.dvyy.tasks.app.ui.elements.AppTopBar
import me.dvyy.tasks.app.ui.theme.AppTheme
import me.dvyy.tasks.core.ui.modifiers.clickableWithoutRipple
import me.dvyy.tasks.db.Database
import me.dvyy.tasks.di.*
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
                module { single { database } },
                appModule(),
                repositoriesModule(),
                authModule(),
                syncModule(),
                viewModelsModule(),
            )
        }) {
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
                        topBar = { AppTopBar(scrollBehavior) },
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
        }
    }
}


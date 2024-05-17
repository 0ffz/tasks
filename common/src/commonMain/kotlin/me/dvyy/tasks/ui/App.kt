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
import me.dvyy.tasks.state.*
import me.dvyy.tasks.stateholder.TasksStateHolder
import me.dvyy.tasks.ui.elements.app.AppDialogs
import me.dvyy.tasks.ui.elements.app.AppDrawer
import me.dvyy.tasks.ui.elements.app.AppTopBar
import me.dvyy.tasks.ui.elements.modifiers.clickableWithoutRipple
import me.dvyy.tasks.ui.screens.home.HomeScreen
import me.dvyy.tasks.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(state: AppState? = null) {
    AppTheme {
        val app = state ?: rememberAppState()
        val responsive = rememberAppUIState()
//        val tasksStateHolder = remember { TasksStateHolder() }
        CompositionLocalProvider(
            AppStateProvider provides app,
            LocalUIState provides responsive,
            LocalTimeState provides app.time,
        ) {
            val tasksStateHolder = viewModel { TasksStateHolder() }
            BoxWithConstraints(
                Modifier.clickableWithoutRipple { tasksStateHolder.selectTask(null) }
            ) {
                val scrollBehavior = if (responsive.singleColumnLists)
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


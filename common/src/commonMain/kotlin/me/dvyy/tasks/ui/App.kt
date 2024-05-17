package me.dvyy.tasks.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import me.dvyy.tasks.state.*
import me.dvyy.tasks.ui.elements.app.AppDialogs
import me.dvyy.tasks.ui.elements.app.AppDrawer
import me.dvyy.tasks.ui.elements.app.AppTopBar
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
            var ready by remember { mutableStateOf(false) }
            BoxWithConstraints(
//                Modifier.clickableWithoutRipple { tasksStateHolder.selectTask(null) }
            ) {
//                LaunchedEffect(Unit) {
//                    app.loadTasksForWeek()
//                    ready = true
//                }
//                if (!ready) {
//                    Surface(Modifier.fillMaxSize()) {
//                        Box(contentAlignment = Alignment.Center) {
//                            CircularProgressIndicator()
//                        }
//                    }
//                    return@BoxWithConstraints
//                }


                val scrollBehavior = if (responsive.atMostMedium)
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


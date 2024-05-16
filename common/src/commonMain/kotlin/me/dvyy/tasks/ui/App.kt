package me.dvyy.tasks.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import me.dvyy.tasks.state.AppState
import me.dvyy.tasks.state.AppStateProvider
import me.dvyy.tasks.ui.elements.app.AppDialogs
import me.dvyy.tasks.ui.elements.app.AppDrawer
import me.dvyy.tasks.ui.elements.app.AppTopBar
import me.dvyy.tasks.ui.elements.modifiers.clickableWithoutRipple
import me.dvyy.tasks.ui.screens.home.HomeScreen
import me.dvyy.tasks.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    AppTheme {
        val app = remember { AppState() }
        var constraintsReady by remember { mutableStateOf(false) }
        var tasksReady by remember { mutableStateOf(false) }
        val ready = constraintsReady && tasksReady
        CompositionLocalProvider(AppStateProvider provides app) {
            BoxWithConstraints(
                Modifier.clickableWithoutRipple { app.selectedTask.value = null }
            ) {
                LaunchedEffect(constraints) {
                    app.isSmallScreen.value = constraints.maxWidth < AppConstants.VIEW_SMALL_MAX_WIDTH
                    constraintsReady = true
                }

                LaunchedEffect(Unit) {
                    app.loadTasksForWeek()
                    tasksReady = true
                }

                if (!ready) {
                    Surface(Modifier.fillMaxSize()) {
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    return@BoxWithConstraints
                }

                val smallScreen by app.isSmallScreen.collectAsState()
                val scrollBehavior = if (smallScreen)
                    TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
                else TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
                AppDrawer {
                    Scaffold(
                        Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
                        topBar = { AppTopBar(scrollBehavior) },
                    ) { paddingValues ->
                        Box(Modifier/*.statusBarsPadding()*/.padding(paddingValues)) {
                            HomeScreen()
                        }
                    }
                    AppDialogs()
                }
            }
        }
    }
}


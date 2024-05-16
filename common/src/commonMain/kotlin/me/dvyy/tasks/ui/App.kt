package me.dvyy.tasks.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import me.dvyy.tasks.state.AppResponsiveUI
import me.dvyy.tasks.state.AppState
import me.dvyy.tasks.state.AppStateProvider
import me.dvyy.tasks.state.LocalResponsiveUI
import me.dvyy.tasks.ui.elements.app.AppDialogs
import me.dvyy.tasks.ui.elements.app.AppDrawer
import me.dvyy.tasks.ui.elements.app.AppTopBar
import me.dvyy.tasks.ui.elements.modifiers.clickableWithoutRipple
import me.dvyy.tasks.ui.screens.home.HomeScreen
import me.dvyy.tasks.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun App(state: AppState? = null) {
    AppTheme {
        val app = state ?: remember { AppState() }
        val windowSizeClass = calculateWindowSizeClass()
        val responsive = remember(windowSizeClass) { AppResponsiveUI(windowSizeClass) }
        CompositionLocalProvider(AppStateProvider provides app, LocalResponsiveUI provides responsive) {
            var ready by remember { mutableStateOf(false) }
            BoxWithConstraints(
                Modifier.clickableWithoutRipple { app.selectedTask.value = null }
            ) {

                LaunchedEffect(Unit) {
                    app.loadTasksForWeek()
                    ready = true
                }
                if (!ready) {
                    Surface(Modifier.fillMaxSize()) {
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    return@BoxWithConstraints
                }


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


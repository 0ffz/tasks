package me.dvyy.tasks

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.dvyy.tasks.state.AppState
import me.dvyy.tasks.state.AppStateProvider
import me.dvyy.tasks.state.LocalAppState
import me.dvyy.tasks.ui.screens.home.HomeScreen
import me.dvyy.tasks.ui.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    MaterialTheme(
        colorScheme = darkColorScheme(),
        typography = AppTypography(),
    ) {
        val state = remember { AppState() }
        CompositionLocalProvider(AppStateProvider provides state) {
            val smallScreen by state.isSmallScreen.collectAsState()
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(scrollBehavior: TopAppBarScrollBehavior) {
    val app = LocalAppState
    val small by app.isSmallScreen.collectAsState()
    val modifier = if (small) Modifier else Modifier.heightIn(max = 45.dp)
    CenterAlignedTopAppBar(
        title = { AppTopBarTitle() },
        navigationIcon = {
            AppDrawerIconButton()
        },
        actions = {
            AppTopBarActions()
        },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
    )
//    TopAppBar(
//        title = { Text("Tasks") },
//        actions = {
//            AppDrawerIconButton()
//        }
//    )
}

@Composable
fun AppTopBarTitle() {
    // current week
    val app = LocalAppState
    val small by app.isSmallScreen.collectAsState()
    val weekStart by app.weekStart.collectAsState()
    val fontSize = if (small) 24.sp else 18.sp
    Text(
        "Week ${(weekStart.dayOfMonth / 7) + 1}, ${
            weekStart.month.name.lowercase().capitalize(Locale.current)
        } ${weekStart.year}",
        style = MaterialTheme.typography.headlineSmall.copy(fontSize = fontSize),
    )
}

@Composable
fun AppTopBarActions() {
    val app = AppStateProvider.current
    // Previous and next icon buttons
    FilledTonalIconButton(onClick = { app.previousWeek() }) {
        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Previous")
    }
    FilledTonalIconButton(onClick = { app.nextWeek() }) {
        Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = "Next")
    }
}

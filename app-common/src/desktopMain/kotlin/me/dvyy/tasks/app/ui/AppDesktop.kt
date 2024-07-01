package me.dvyy.tasks.app.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.rememberWindowState
import me.dvyy.tasks.app.data.DriverFactory
import me.dvyy.tasks.app.data.TopbarViewModel
import me.dvyy.tasks.app.data.createDatabase
import me.dvyy.tasks.app.ui.topbar.DesktopTopBar
import me.dvyy.tasks.db.Database
import me.dvyy.tasks.di.koinViewModel
import org.koin.dsl.module

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationScope.AppDesktop() {
    val windowState = rememberWindowState(width = 1200.dp, height = 960.dp)
    val icon = painterResource("icon.png")
    var resizable by remember { mutableStateOf(true) }
    Window(
        state = windowState,
        title = "Tasks",
        icon = icon,
        onCloseRequest = ::exitApplication,
        undecorated = true,
        resizable = resizable
    ) {
        App(
            extraModules = remember {
                listOf(module {
                    single<Database> { createDatabase(DriverFactory()) }
                    single {
                        TopbarViewModel(
                            windowState = windowState,
                            windowScope = this@Window,
                            onClose = { exitApplication() }
                        )
                    }
                })
            },
            topBar = {
                DesktopTopBar()
                val isFloating by koinViewModel<TopbarViewModel>().floatingWindowSize.collectAsState()
                LaunchedEffect(isFloating) {
                    resizable = isFloating == null
                }
            }
        )
    }
}

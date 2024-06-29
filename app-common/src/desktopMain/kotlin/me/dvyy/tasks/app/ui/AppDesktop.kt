package me.dvyy.tasks.app.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import me.dvyy.tasks.app.data.DriverFactory
import me.dvyy.tasks.app.data.TopbarViewModel
import me.dvyy.tasks.app.data.createDatabase
import me.dvyy.tasks.app.ui.topbar.DesktopTopBar
import me.dvyy.tasks.db.Database
import org.koin.dsl.module

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationScope.AppDesktop() {
    val windowState = rememberWindowState(width = 1200.dp, height = 960.dp)
    Window(
        state = windowState,
        onCloseRequest = ::exitApplication,
        undecorated = true,
    ) {
        App(
            extraModules = listOf(module {
                single<Database> { createDatabase(DriverFactory()) }
                single {
                    TopbarViewModel(
                        windowState = windowState,
                        windowScope = this@Window,
                        onClose = { exitApplication() }
                    )
                }
            }),
            topBar = {
                DesktopTopBar()
            }
        )
    }
}

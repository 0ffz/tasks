package me.dvyy.tasks.app.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import me.dvyy.tasks.app.data.DriverFactory
import me.dvyy.tasks.app.data.TopbarViewModel
import me.dvyy.tasks.app.data.createDatabase
import me.dvyy.tasks.app.ui.topbar.DesktopTopBar
import me.dvyy.tasks.db.Database
import me.dvyy.tasks.di.koinViewModel
import org.koin.compose.koinInject
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationScope.AppDesktop() = AppKoinContext {
    val windowState = rememberWindowState(width = 1200.dp, height = 960.dp)
    val icon = painterResource("icon.png")
    var resizable by remember { mutableStateOf(true) }
    val prefs = koinInject<PreferencesViewModel>()
    var density by prefs.floatSetting("density", LocalDensity.current.density)
    Window(
        state = windowState,
        title = "Tasks",
        icon = icon,
        onCloseRequest = ::exitApplication,
        undecorated = true,
        resizable = resizable,
        onKeyEvent = {
            if(it.type == KeyEventType.KeyUp) return@Window false
            when {
                it.isCtrlPressed && it.key == Key.Equals -> {
                    density += 0.1f
                    true
                }
                it.isCtrlPressed && it.key == Key.Minus -> {
                    density -= 0.1f
                    true
                }
                else -> {
                    // let other handlers receive this event
                    false
                }
            }
        }
    ) {
        KoinPlatform.getKoin().loadModules(remember {
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
        })
        CompositionLocalProvider(LocalDensity provides Density(density)) {
            App(
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
}

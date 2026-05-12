package me.dvyy.tasks.app.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.rememberWindowState
import co.touchlab.kermit.Logger
import co.touchlab.kermit.platformLogWriter
import me.dvyy.app_client.generated.resources.Res
import me.dvyy.app_client.generated.resources.icon
import me.dvyy.tasks.app.createAppKoinApplication
import me.dvyy.tasks.app.data.TopbarViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.KoinIsolatedContext
import org.koin.compose.getKoin
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationScope.AppDesktop(
    overrides: Module = module { },
) = KoinIsolatedContext(createAppKoinApplication(overrides = overrides)) {
    Logger.setLogWriters(platformLogWriter(ColoredFormatter))
    val windowState = rememberWindowState(
        placement = WindowPlacement.Maximized,
        width = 1200.dp,
        height = 960.dp
    )
    val icon = painterResource(Res.drawable.icon)
    var resizable by remember { mutableStateOf(true) }
    val prefs = koinInject<PreferencesViewModel>()
    val density by prefs.density.collectAsState()
    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "Tasks",
        icon = icon,
        undecorated = true,
        resizable = resizable,
        onKeyEvent = {
            if (it.type == KeyEventType.KeyUp) return@Window false
            when {
                it.isCtrlPressed && it.key == Key.Equals -> {
                    prefs.density.value += 0.1f
                    true
                }

                it.isCtrlPressed && it.key == Key.Minus -> {
                    prefs.density.value -= 0.1f
                    true
                }

                else -> {
                    // let other handlers receive this event
                    false
                }
            }
        }
    ) {
        getKoin().loadModules(remember {
            listOf(module {
//                single<Database> { createClientDatabase() }
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
            Box(Modifier.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RectangleShape)) {
                App(
                    topBar = {
//                    DesktopTopBar()
                        val isFloating by koinViewModel<TopbarViewModel>().floatingWindowSize.collectAsState()
                        LaunchedEffect(isFloating) {
                            resizable = isFloating == null
                        }
                    }
                )
            }
        }
    }
}

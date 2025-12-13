package me.dvyy.tasks.app.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.flow.update
import me.dvyy.tasks.app.ui.LocalUIState
import me.dvyy.tasks.app.ui.UI
import me.dvyy.tasks.app.ui.elements.TopBarContainer
import me.dvyy.tasks.core.ui.modifiers.clickableWithoutRipple
import me.dvyy.tasks.settings.ui.SettingsScreen
import me.dvyy.tasks.tasks.ui.elements.list.optional
import org.koin.compose.viewmodel.koinViewModel

sealed interface AppScreen {
    data object Settings : AppScreen
}

@Composable
fun AppScreens(app: DialogViewModel = koinViewModel()) {
    val ui = LocalUIState.current
    val screenState by app.screen.collectAsState()
    screenState ?: return

    /*if (ui.isSmall) Surface {
        Box(Modifier.systemBarsPadding()) {
            Screens()
        }
    } else */Dialog(
        onDismissRequest = { app.screen.update { null } },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(Modifier.fillMaxSize().clickableWithoutRipple { app.screen.update { null } })
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            val padding = if (ui.isSmall) 0.dp else 32.dp
            Surface(
                Modifier
                    .widthIn(max = 1200.dp)
                    .optional(!ui.isSmall) { heightIn(max = 1200.dp) }
                    .optional(ui.isSmall) { padding(top = UI.tabHeight * 0.75f) }
                    .fillMaxSize().padding(padding),
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 1.dp
            ) {
                Screens()
            }
        }
    }
}

@Composable
private fun Screens(
    app: DialogViewModel = koinViewModel(),
) {
    val screenState by app.screen.collectAsState()
    val screen = screenState ?: return
    when (screen) {
        AppScreen.Settings -> SettingsScreen()
    }
}

@Composable
fun ScreenContainer(
    title: String,
    extraItems: @Composable RowScope.() -> Unit = {},
    app: DialogViewModel = koinViewModel(),
    onClose: () -> Unit = { app.screen.update { null } },
    content: @Composable () -> Unit,
) {
    Column {
        TopBarContainer {
            Row(verticalAlignment = Alignment.CenterVertically) {
                extraItems()
                Spacer(Modifier.weight(1f))
                Text(title)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onClose) {
                    Icon(Icons.Rounded.Close, "Close")
                }
            }
        }
        content()
    }
}
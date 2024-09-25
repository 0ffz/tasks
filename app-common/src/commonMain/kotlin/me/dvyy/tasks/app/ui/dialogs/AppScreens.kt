package me.dvyy.tasks.app.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import me.dvyy.tasks.core.ui.modifiers.clickableWithoutRipple
import me.dvyy.tasks.settings.ui.SettingsScreen
import me.dvyy.tasks.tasks.ui.elements.list.thenOptional
import org.koin.compose.viewmodel.koinViewModel

sealed interface AppScreen {
    data object Settings : AppScreen
}

@Composable
fun AppScreens(app: DialogViewModel = koinViewModel()) {
    val ui = LocalUIState.current
    val screenState by app.screen.collectAsState()
    screenState ?: return

    if (ui.isSingleColumn) Surface {
        Box(Modifier.systemBarsPadding()) {
            Screens()
        }
    } else Dialog(
        onDismissRequest = { app.screen.update { null } },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(Modifier.fillMaxSize().clickableWithoutRipple { app.screen.update { null } })
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            val padding = if (ui.isSingleColumn) 0.dp else 32.dp
            Surface(
                Modifier
                    .widthIn(max = 1600.dp)
                    .thenOptional(!ui.isSingleColumn) { heightIn(max = 1200.dp) }
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
    Box {
        when (screen) {
            AppScreen.Settings -> SettingsScreen()
        }
        IconButton(
            onClick = { app.screen.update { null } },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(Icons.Rounded.Close, "Close")
        }
    }
}

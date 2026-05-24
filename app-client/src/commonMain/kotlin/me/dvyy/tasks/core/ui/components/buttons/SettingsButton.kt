package me.dvyy.tasks.core.ui.components.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import me.dvyy.tasks.app.ui.AppState
import me.dvyy.tasks.app.ui.dialogs.AppScreen
import me.dvyy.tasks.app.ui.dialogs.DialogViewModel
import me.dvyy.tasks.tasks.ui.elements.helpers.buttons.BoxButton
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsButton(
    dialogs: DialogViewModel = koinViewModel(),
    app: AppState = koinInject(),
) {
    val scope = rememberCoroutineScope()
    BoxButton(onClick = {
        scope.launch {
            dialogs.showScreen(AppScreen.Settings())
            app.drawerState.close()
        }
    }) {
         Icon(Icons.Outlined.Settings, contentDescription = "Settings")
    }
}

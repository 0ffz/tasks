package me.dvyy.tasks.core.ui.components.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import me.dvyy.tasks.app.ui.AppState
import me.dvyy.tasks.app.ui.dialogs.AppScreen
import me.dvyy.tasks.app.ui.dialogs.DialogViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsButton(
    dialogs: DialogViewModel = koinViewModel(),
    app: AppState = koinInject(),
) {
    val scope = rememberCoroutineScope()
    IconButton(onClick = {
        scope.launch {
            dialogs.showScreen(AppScreen.Settings)
            app.drawerState.close()
        }
    }) {
         Icon(Icons.Outlined.Settings, contentDescription = "Settings")
    }
}

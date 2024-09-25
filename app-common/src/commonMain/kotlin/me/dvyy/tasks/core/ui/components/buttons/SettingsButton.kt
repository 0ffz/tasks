package me.dvyy.tasks.core.ui.components.buttons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import me.dvyy.tasks.app.ui.dialogs.AppScreen
import me.dvyy.tasks.app.ui.dialogs.DialogViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsButton(
    dialogs: DialogViewModel = koinViewModel()
) {
    IconButton(onClick = { dialogs.showScreen(AppScreen.Settings) }) {
         Icon(Icons.Outlined.Settings, contentDescription = "Settings")
    }
}

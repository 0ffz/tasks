package me.dvyy.tasks.app.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import me.dvyy.tasks.di.koinViewModel

@Composable
fun SettingsButton(
    dialogs: DialogViewModel = koinViewModel()
) {
    IconButton(onClick = { dialogs.showScreen(AppScreen.Settings) }) {
         Icon(Icons.Outlined.Settings, contentDescription = "Settings")
    }
}

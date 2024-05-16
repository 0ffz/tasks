package me.dvyy.tasks.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import me.dvyy.tasks.state.AppDialog
import me.dvyy.tasks.state.LocalAppState
import me.dvyy.tasks.ui.elements.sync.SyncButton

@Composable
fun HomeFAB() {
    val app = LocalAppState
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        var toggled by remember { mutableStateOf(false) }
        SyncButton()
        AnimatedVisibility(toggled) {
            SmallFloatingActionButton(onClick = {
                app.activeDialog.value = AppDialog.Auth
            }) {
                Icon(Icons.Rounded.Cloud, contentDescription = "Server setup")
            }
        }
        Row {
            val username by app.auth.username.collectAsState()
//            Text(username, style = MaterialTheme.typography.labelLarge)
            FloatingActionButton(onClick = { toggled = !toggled }) {
                Icon(Icons.Rounded.Settings, contentDescription = "Settings")
            }
        }
    }
}

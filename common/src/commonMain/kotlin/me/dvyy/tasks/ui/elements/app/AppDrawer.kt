package me.dvyy.tasks.ui.elements.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.dvyy.tasks.NoRippleInteractionSource
import me.dvyy.tasks.state.AppDialog
import me.dvyy.tasks.state.LocalAppState

@Composable
fun AppDrawer(content: @Composable () -> Unit) {
    val app = LocalAppState
    ModalNavigationDrawer(
        drawerState = app.drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(Modifier.padding(16.dp)) {
                    Text("Tasks", modifier = Modifier.padding(16.dp))
                    val username by app.auth.username.collectAsState()
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Outlined.Settings, contentDescription = "Settings") },
                        label = { Text(text = "Settings") },
                        selected = false,
                        onClick = { /*TODO*/ }
                    )
                    Spacer(Modifier.weight(1f))
                    if (username == null) {
                        NavigationDrawerItem(
                            icon = { Icon(Icons.AutoMirrored.Outlined.Login, contentDescription = "Switch account") },
                            label = { Text(text = "Login") },
                            selected = false,
                            onClick = { app.activeDialog.value = AppDialog.Auth }
                        )
                    } else {
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Outlined.AccountCircle, contentDescription = "Account") },
                            label = { Text(text = "$username") },
                            selected = false,
                            onClick = { },
                            interactionSource = NoRippleInteractionSource()
                        )
                        NavigationDrawerItem(
                            icon = { Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = "Account") },
                            label = { Text(text = "Logout") },
                            selected = false,
                            onClick = { app.auth.logout() }
                        )
                    }

                }
            }
        },
    ) {
        content()
    }
}

@Composable
fun AppDrawerIconButton() {
    val app = LocalAppState
    val scope = rememberCoroutineScope()
    IconButton(
        onClick = {
            scope.launch {
                app.drawerState.open()
            }
        },
    ) {
        Icon(
            Icons.Outlined.Menu,
            contentDescription = "Menu",
        )
    }
}
